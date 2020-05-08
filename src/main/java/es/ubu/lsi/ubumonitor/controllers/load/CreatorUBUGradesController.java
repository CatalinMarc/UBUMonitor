package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.State;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.Tracking;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.SubDataBase;
import es.ubu.lsi.ubumonitor.webservice.api.core.completion.CoreCompletionGetActivitiesCompletionStatus;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetCategories;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetContents;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetEnrolledCoursesByTimelineClassification;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetUserAdministrationOptions;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetUserNavigationOptions;
import es.ubu.lsi.ubumonitor.webservice.api.core.enrol.CoreEnrolGetEnrolledUsers;
import es.ubu.lsi.ubumonitor.webservice.api.core.enrol.CoreEnrolGetUsersCourses;
import es.ubu.lsi.ubumonitor.webservice.api.core.users.CoreUserGetUsersByField;
import es.ubu.lsi.ubumonitor.webservice.api.core.users.CoreUserGetUsersByField.Field;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradeItems;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradesTable;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Clase encargada de usar las funciones de la REST API de Moodle para conseguir
 * los datos de los usuarios
 * 
 * @author Yi Peng Ji
 *
 */
public class CreatorUBUGradesController {

	private static final String SHORTNAME = "shortname";

	private static final String FULLNAME = "fullname";

	private static final String DESCRIPTIONFORMAT = "descriptionformat";

	private static final String DESCRIPTION = "description";

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatorUBUGradesController.class);

	private static final Controller CONTROLLER = Controller.getInstance();

	/**
	 * Icono de carpeta, indica que el grade item es de categoria.
	 */
	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";

	/**
	 * Nivel de jearquia del grade Item
	 */
	private static final Pattern NIVEL = Pattern.compile("level(\\d+)");

	public static JSONArray getJSONArrayResponse(WSFunctionAbstract webServiceFunction) throws IOException {
		try (Response response = WebService.getResponse(webServiceFunction)) {
			return new JSONArray(new JSONTokener(response.body()
					.byteStream()));

		}

	}

	public static JSONObject getJSONObjectResponse(WSFunctionAbstract webServiceFunction) throws IOException {
		try (Response response = WebService.getResponse(webServiceFunction)) {
			return new JSONObject(new JSONTokener(response.body()
					.byteStream()));

		}

	}

	/**
	 * Busca los cursos matriculados del alumno.
	 * 
	 * @param userid id del usuario
	 * @return lista de cursos matriculados por el usuario
	 * @throws IOException error de conexion moodle
	 */
	public static List<Course> getUserCourses(int userid) throws IOException {
		JSONArray jsonArray = getJSONArrayResponse(new CoreEnrolGetUsersCourses(userid));
		List<Course> courses = createCourses(jsonArray, true);
		createCourseAdministrationOptions(courses);
		return courses;
	}

	/**
	 * Crea un usuario moodle del que se loguea en la aplicación
	 * 
	 * @param username nombre de usuario
	 * @return el usuario moodle
	 * @throws IOException si no ha podido conectarse al servidor moodle
	 */
	public static MoodleUser createMoodleUser(String username) throws IOException {
		JSONObject jsonObject = getJSONArrayResponse(new CoreUserGetUsersByField(Field.USERNAME, username))
				.getJSONObject(0);

		MoodleUser moodleUser = new MoodleUser();
		moodleUser.setId(jsonObject.getInt("id"));

		moodleUser.setUserName(jsonObject.optString("username"));

		moodleUser.setFullName(jsonObject.optString(FULLNAME));

		moodleUser.setEmail(jsonObject.optString("email"));

		moodleUser.setFirstAccess(Instant.ofEpochSecond(jsonObject.optLong("firstaccess")));

		moodleUser.setLastAccess(Instant.ofEpochSecond(jsonObject.optLong("lastaccess")));

		byte[] imageBytes = downloadImage(jsonObject.optString("profileimageurlsmall", null));

		moodleUser.setUserPhoto(imageBytes);

		moodleUser.setLang(jsonObject.optString("lang"));

		moodleUser.setServerTimezone(findServerTimezone(CONTROLLER.getUrlHost()
				.toString()));
		// 99 significa que el usuario esta usando la zona horaria del servidor
		ZoneId userTimeZone = "99".equals(jsonObject.optString("timezone")) ? moodleUser.getServerTimezone()
				: ZoneId.of(jsonObject.optString("timezone", "UTC"));

		moodleUser.setTimezone(userTimeZone);

		List<Course> courses = getUserCourses(moodleUser.getId());
		moodleUser.setCourses(courses);

		Set<Integer> ids = courses.stream()
				.mapToInt(c -> c.getCourseCategory()
						.getId()) // cogemos los ids de cada
									// curso
				.boxed() // convertimos a Integer
				.collect(Collectors.toSet());

		createCourseCategories(ids);

		moodleUser.setInProgressCourses(
				coursesByTimeline(CoreCourseGetEnrolledCoursesByTimelineClassification.IN_PROGRESS));
		moodleUser.setPastCourses(coursesByTimeline(CoreCourseGetEnrolledCoursesByTimelineClassification.PAST));
		moodleUser.setFutureCourses(coursesByTimeline(CoreCourseGetEnrolledCoursesByTimelineClassification.FUTURE));
		moodleUser.setRecentCourses(getRecentCourses(moodleUser.getId()));

		return moodleUser;
	}

	/**
	 * Busca la zona horaria del servidor a partir del perfil del usuario.
	 * 
	 * @return zona horaria del servidor.
	 * @throws IOException si no puede conectarse con el servidor
	 */
	public static ZoneId findServerTimezone(String host) throws IOException {

		LOGGER.info("Buscando el tiempo del servidor desde el perfil del usuario.");
		// vamos a la edicion del perfil de usuario para ver la zona horaria del
		// servidor
		Document d = Jsoup.parse(Connection.getResponse(host + "/user/edit.php?lang=en")
				.body()
				.string());

		// timezone tendra una estructuctura parecida a: Server timezone (Europe/Madrid)
		// lo unico que nos interesa es lo que hay dentro de los parentesis:
		// Europe/Madrid
		String timezone = d.selectFirst("select#id_timezone option[value=99]")
				.text();

		String timezoneParsed = timezone.substring(17, timezone.length() - 1);
		LOGGER.info("Timezone del servidor: {}", timezoneParsed);
		return ZoneId.of(timezoneParsed);

	}

	private static List<Course> coursesByTimeline(String classification) {
		try {

			JSONArray courses = getJSONObjectResponse(
					new CoreCourseGetEnrolledCoursesByTimelineClassification(classification)).getJSONArray("courses");

			return createCourses(courses, true);
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	private static List<Course> getRecentCourses(int userid) throws IOException {

		try {
			JSONArray jsonArray = getRecentCoursesResponse(userid);
			return createCourses(jsonArray, true);
		} catch (Exception ex) {
			return Collections.emptyList();
		}

	}

	private static JSONArray getRecentCoursesResponse(int userid) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("methodname", WSFunctionEnum.CORE_COURSE_GET_RECENT_COURSES);

		JSONObject args = new JSONObject();
		args.put("userid", userid);
		args.put("limit", 8);

		jsonObject.put("args", args);

		jsonArray.put(jsonObject);

		Response response = Connection.getResponse(new Request.Builder()
				.url(CONTROLLER.getUrlHost() + "/lib/ajax/service.php?sesskey=" + CONTROLLER.getSesskey())
				.post(RequestBody.create(jsonArray.toString(), MediaType.parse("application/json; charset=utf-8")))
				.build());
		JSONArray responseArray = new JSONArray(new JSONTokener(response.body()
				.byteStream()));
		response.close();
		return responseArray.getJSONObject(0)
				.getJSONArray("data");

	}

	/**
	 * Descarga una imagen de moodle, necesario usar los cookies en versiones
	 * posteriores al 3.5
	 * 
	 * @param url url de la image
	 * @return array de bytes de la imagen o un array de byte vacío si la url es
	 *         null
	 * @throws IOException si hay algun problema al descargar la imagen
	 */
	private static byte[] downloadImage(String url) throws IOException {
		if (url == null) {
			return new byte[0];
		}

		return Connection.getResponse(url)
				.body()
				.bytes();

	}

	/**
	 * Crea las categorias de curso.
	 * 
	 * @param ids ids de las categorias
	 * @throws IOException si hay algun problema conectarse a moodle
	 */
	public static void createCourseCategories(Set<Integer> ids) throws IOException {

		JSONArray jsonArray = getJSONArrayResponse(new CoreCourseGetCategories(ids));
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			int id = jsonObject.getInt("id");
			CourseCategory courseCategory = CONTROLLER.getDataBase()
					.getCourseCategories()
					.getById(id);
			courseCategory.setName(jsonObject.getString("name"));
			courseCategory.setDescription(jsonObject.getString(DESCRIPTION));
			courseCategory.setDescriptionFormat(DescriptionFormat.get(jsonObject.getInt(DESCRIPTIONFORMAT)));
			courseCategory.setCoursecount(jsonObject.getInt("coursecount"));
			courseCategory.setDepth(jsonObject.getInt("depth"));
			courseCategory.setPath(jsonObject.getString("path"));

		}

	}

	/**
	 * Crea e inicializa los usuarios matriculados de un curso.
	 * 
	 * @param courseid id del curso
	 * @return lista de usuarios matriculados en el curso
	 * @throws IOException si no ha podido conectarse
	 */
	public static List<EnrolledUser> createEnrolledUsers(int courseid) throws IOException {

		JSONArray users = getJSONArrayResponse(CoreEnrolGetEnrolledUsers.newBuilder(courseid)
				.build());

		List<EnrolledUser> enrolledUsers = new ArrayList<>();

		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			enrolledUsers.add(createEnrolledUser(user));
		}
		return enrolledUsers;

	}

	/**
	 * Crea el usuario matriculado a partir del json parcial de la respuesta de
	 * moodle
	 * 
	 * @param user json parcial del usuario
	 *             {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return usuario matriculado
	 * @throws IOException si hay un problema de conexion con moodle
	 */
	public static EnrolledUser createEnrolledUser(JSONObject user) throws IOException {

		int id = user.getInt("id");

		EnrolledUser enrolledUser = CONTROLLER.getDataBase()
				.getUsers()
				.getById(id);

		enrolledUser.setFirstname(user.optString("firstname"));
		enrolledUser.setLastname(user.optString("lastname"));
		enrolledUser.setFullName(user.optString(FULLNAME));
		enrolledUser.setFirstaccess(Instant.ofEpochSecond(user.optLong("firstaccess", -1))); // -1 si no esta disponible
		enrolledUser.setLastaccess(Instant.ofEpochSecond(user.optLong("lastaccess", -1)));// -1 si no esta disponible
		enrolledUser.setLastcourseaccess(Instant.ofEpochSecond(user.optLong("lastcourseaccess", -1)));// disponible en
																										// moodle 3.7
		enrolledUser.setDescription(user.optString(DESCRIPTION));
		enrolledUser.setDescriptionformat(DescriptionFormat.get(user.optInt(DESCRIPTIONFORMAT)));
		enrolledUser.setCity(user.optString("city"));
		enrolledUser.setCountry(user.optString("country"));
		enrolledUser.setProfileimageurl(user.optString("profileimageurl"));
		enrolledUser.setEmail(user.optString("email"));

		String imageUrl = user.optString("profileimageurl", null);
		enrolledUser.setProfileimageurlsmall(imageUrl);

		if (imageUrl != null) {
			byte[] imageBytes = downloadImage(imageUrl);

			enrolledUser.setImageBytes(imageBytes);
		}

		List<Course> courses = createCourses(user.optJSONArray("enrolledcourses"), false);
		courses.forEach(course -> course.addEnrolledUser(enrolledUser));

		List<Role> roles = createRoles(user.optJSONArray("roles"));
		roles.forEach(role -> role.addEnrolledUser(enrolledUser));

		List<Group> groups = createGroups(user.optJSONArray("groups"));
		groups.forEach(group -> group.addEnrolledUser(enrolledUser));

		return enrolledUser;

	}

	/**
	 * Crea los cursos a partir del json parcial de la función moodle de los
	 * usuarios matriculados del curso y de la funcion cursos matriculados de un
	 * usuario.
	 * 
	 * @param jsonArray json parcial
	 *                  {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 *                  o
	 *                  {@link webservice.WSFunctions#CORE_ENROL_GET_USERS_COURSES}
	 * @return lista de cursos
	 */
	public static List<Course> createCourses(JSONArray jsonArray, boolean userCourses) {
		if (jsonArray == null)
			return Collections.emptyList();

		List<Course> courses = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			courses.add(userCourses ? createUserCourse(jsonObject) : createEnrolledCourse(jsonObject));
		}
		return courses;

	}

	/**
	 * Crea un curso e inicializa sus atributos
	 * 
	 * @param jsonObject json parcial
	 *                   {@link webservice.WSFunctions#CORE_ENROL_GET_USERS_COURSES}
	 * @return curso creado
	 */
	public static Course createUserCourse(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		int id = jsonObject.getInt("id");
		Course course = CONTROLLER.getDataBase()
				.getCourses()
				.getById(id);

		String shortName = jsonObject.getString(SHORTNAME);
		String fullName = jsonObject.getString(FULLNAME);

		String idNumber = jsonObject.optString("idnumber");
		String summary = jsonObject.optString("summary");
		DescriptionFormat summaryFormat = DescriptionFormat.get(jsonObject.optInt("summaryformat"));
		Instant startDate = Instant.ofEpochSecond(jsonObject.optLong("startdate"));
		Instant endDate = Instant.ofEpochSecond(jsonObject.optLong("enddate"));
		boolean isFavorite = jsonObject.optBoolean("isfavourite");

		int categoryId = jsonObject.optInt("category");
		if (categoryId != 0) {
			CourseCategory courseCategory = CONTROLLER.getDataBase()
					.getCourseCategories()
					.getById(categoryId);
			course.setCourseCategory(courseCategory);
		}

		course.setShortName(shortName);
		course.setFullName(fullName);
		course.setIdNumber(idNumber);
		course.setSummary(summary);
		course.setSummaryformat(summaryFormat);
		course.setStartDate(startDate);
		course.setEndDate(endDate);
		course.setFavorite(isFavorite);

		return course;

	}

	/**
	 * Crea un curso e inicializa sus atributos
	 * 
	 * @param jsonObject json parcial
	 *                   {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return curso creado
	 */
	public static Course createEnrolledCourse(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		int id = jsonObject.getInt("id");
		Course course = CONTROLLER.getDataBase()
				.getCourses()
				.getById(id);

		String shortName = jsonObject.getString(SHORTNAME);
		String fullName = jsonObject.getString(FULLNAME);

		course.setShortName(shortName);
		course.setFullName(fullName);
		course.setHasActivityCompletion(jsonObject.optBoolean("enablecompletion", true));

		return course;

	}

	/**
	 * Crea los roles que tiene el usuario dentro del curso.
	 * 
	 * @param jsonArray json parcial
	 *                  {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return lista de roles
	 */
	public static List<Role> createRoles(JSONArray jsonArray) {

		if (jsonArray == null)
			return Collections.emptyList();

		List<Role> roles = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			roles.add(createRole(jsonObject));
		}
		return roles;
	}

	/**
	 * Crea un rol
	 * 
	 * @param jsonObject json parcial de la funcion
	 *                   {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return el rol creado
	 */
	public static Role createRole(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int roleid = jsonObject.getInt("roleid");

		Role role = CONTROLLER.getDataBase()
				.getRoles()
				.getById(roleid);

		String name = jsonObject.getString("name");
		String shortName = jsonObject.getString(SHORTNAME);

		role.setRoleName(name);
		role.setRoleShortName(shortName);

		CONTROLLER.getDataBase()
				.getActualCourse()
				.addRole(role);

		return role;

	}

	/**
	 * Crea los grupos del curso
	 * 
	 * @param jsonArray json parcial de
	 *                  {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return listado de grupos
	 */
	public static List<Group> createGroups(JSONArray jsonArray) {

		if (jsonArray == null)
			return Collections.emptyList();

		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groups.add(createGroup(jsonObject));
		}
		return groups;
	}

	/**
	 * Crea un grupo a partir del json
	 * 
	 * @param jsonObject {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return el grupo
	 */
	public static Group createGroup(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int groupid = jsonObject.getInt("id");
		Group group = CONTROLLER.getDataBase()
				.getGroups()
				.getById(groupid);

		String name = jsonObject.getString("name");
		String description = jsonObject.getString(DESCRIPTION);
		DescriptionFormat descriptionFormat = DescriptionFormat.get(jsonObject.getInt(DESCRIPTIONFORMAT));

		group.setGroupName(name);
		group.setDescription(description);
		group.setDescriptionFormat(descriptionFormat);

		CONTROLLER.getDataBase()
				.getActualCourse()
				.addGroup(group);

		return group;

	}

	/**
	 * Crea los modulos del curso a partir de la funcion de moodle
	 * {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * 
	 * @param courseid id del curso
	 * @return lista de modulos del curso
	 * @throws IOException error de conexion con moodle
	 */
	public static List<CourseModule> createSectionsAndModules(int courseid) throws IOException {

		JSONArray jsonArray = getJSONArrayResponse(CoreCourseGetContents.newBuilder(courseid)
				.setExcludecontents(true)
				.build());

		List<CourseModule> modulesList = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject sectionJson = jsonArray.getJSONObject(i);

			if (sectionJson == null)
				throw new IllegalStateException("Nulo en la secciones del curso");

			Section section = createSection(sectionJson);
			CONTROLLER.getActualCourse()
					.addSection(section);

			JSONArray modules = sectionJson.getJSONArray("modules");

			for (int j = 0; j < modules.length(); j++) {
				JSONObject mJson = modules.getJSONObject(j);
				CourseModule courseModule = createModule(mJson);
				if (courseModule != null) {
					courseModule.setSection(section);
					modulesList.add(courseModule);

				}

			}
		}

		return modulesList;

	}

	private static Section createSection(JSONObject sectionJson) {

		int id = sectionJson.getInt("id");

		Section section = CONTROLLER.getDataBase()
				.getSections()
				.getById(id);
		section.setName(sectionJson.optString("name"));
		section.setVisible(sectionJson.optInt("visible") == 1);
		section.setSummary(sectionJson.getString("summary"));
		section.setSummaryformat(DescriptionFormat.get(sectionJson.optInt("summaryformat")));
		section.setSectionNumber(sectionJson.optInt("section", -1));
		section.setHiddenbynumsections(sectionJson.optInt("hiddenbynumsections"));

		return section;
	}

	/**
	 * Crea los modulos del curso a partir del json de
	 * {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * 
	 * @param jsonObject de {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * @return modulo del curso
	 */
	public static CourseModule createModule(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int cmid = jsonObject.getInt("id");

		ModuleType moduleType = ModuleType.get(jsonObject.getString("modname"));

		CourseModule module = CONTROLLER.getDataBase()
				.getModules()
				.getById(cmid);

		module.setCmid(jsonObject.getInt("id"));
		module.setUrl(jsonObject.optString("url"));
		module.setModuleName(jsonObject.optString("name"));
		module.setInstance(jsonObject.optInt("instance"));
		module.setDescription(jsonObject.optString(DESCRIPTION));
		module.setVisible(jsonObject.optInt("visible", 1) != 0);
		module.setUservisible(jsonObject.optBoolean("uservisible"));
		module.setVisibleoncoursepage(jsonObject.optInt("visibleoncoursepage"));
		module.setModicon(jsonObject.optString("modicon"));
		module.setModuleType(moduleType);
		module.setIndent(jsonObject.optInt("indent"));

		CONTROLLER.getDataBase()
				.getActualCourse()
				.addModule(module);

		return module;

	}

	public static void createActivitiesCompletionStatus(int courseid, Collection<EnrolledUser> users)
			throws IOException {
		for (EnrolledUser user : users) {
			createActivitiesCompletionStatus(courseid, user.getId());
		}
	}

	public static void createActivitiesCompletionStatus(int courseid, int userid) throws IOException {

		JSONObject jsonObject = getJSONObjectResponse(
				new CoreCompletionGetActivitiesCompletionStatus(courseid, userid));
		SubDataBase<CourseModule> modules = CONTROLLER.getDataBase()
				.getModules();
		SubDataBase<EnrolledUser> enrolledUsers = CONTROLLER.getDataBase()
				.getUsers();

		JSONArray statuses = jsonObject.optJSONArray("statuses");
		if (statuses == null) {
			return;
		}
		for (int i = 0; i < statuses.length(); i++) {
			JSONObject status = statuses.getJSONObject(i);

			CourseModule courseModule = modules.getById(status.getInt("cmid"));
			State state = State.getByIndex(status.getInt("state"));
			Instant timecompleted = Instant.ofEpochSecond(status.getLong("timecompleted"));
			Tracking tracking = Tracking.getByIndex(status.getInt("tracking"));
			int iduser = status.optInt("overrideby", -1);
			EnrolledUser overrideby = null;
			if (iduser != -1) {
				overrideby = enrolledUsers.getById(iduser);
			}
			boolean valueused = status.optBoolean("valueused");
			ActivityCompletion activity = new ActivityCompletion(state, timecompleted, tracking, overrideby, valueused);

			courseModule.getActivitiesCompletion()
					.put(enrolledUsers.getById(userid), activity);

		}
	}

	/**
	 * Crea los grade item y su jerarquia de niveles, not using at the moment
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE} y
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 * 
	 * @param courseid id del curso
	 * @return lista de grade item
	 * @throws IOException si no se ha conectado con moodle
	 */
	public static List<GradeItem> createGradeItems(int courseid) throws IOException {

		JSONObject jsonObject = getJSONObjectResponse(new GradereportUserGetGradesTable(courseid));
		List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);

		jsonObject = getJSONObjectResponse(new GradereportUserGetGradeItems(courseid));
		setBasicAttributes(gradeItems, jsonObject);
		setEnrolledUserGrades(gradeItems, jsonObject);
		updateToOriginalGradeItem(gradeItems);

		return gradeItems;
	}

	/**
	 * Actualiza los grade item
	 * 
	 * @param gradeItems las de grade item
	 */
	private static void updateToOriginalGradeItem(List<GradeItem> gradeItems) {
		for (GradeItem gradeItem : gradeItems) {
			GradeItem original = CONTROLLER.getDataBase()
					.getGradeItems()
					.getById(gradeItem.getId());
			CONTROLLER.getDataBase()
					.getActualCourse()
					.addGradeItem(original);

			if (gradeItem.getFather() != null) {
				GradeItem originalFather = CONTROLLER.getDataBase()
						.getGradeItems()
						.getById(gradeItem.getFather()
								.getId());
				original.setFather(originalFather);
			}
			List<GradeItem> originalChildren = new ArrayList<>();
			for (GradeItem child : gradeItem.getChildren()) {
				originalChildren.add(CONTROLLER.getDataBase()
						.getGradeItems()
						.getById(child.getId()));
			}
			original.setChildren(originalChildren);

			original.setItemname(gradeItem.getItemname());
			original.setLevel(gradeItem.getLevel());
			original.setWeightraw(gradeItem.getWeightraw());
			original.setGraderaw(gradeItem.getGraderaw());
			original.setGrademax(gradeItem.getGrademax());
			original.setGrademin(gradeItem.getGrademin());

		}
	}

	/**
	 * Crea la jearquia de padres e hijos de los grade item
	 * 
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE}
	 * @return lista de grade item
	 */
	private static List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = jsonObject.getJSONArray("tables")
				.getJSONObject(0);

		int maxDepth = table.getInt("maxdepth") + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray("tabledata");

		List<GradeItem> gradeItems = new ArrayList<>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject item = tabledata.optJSONObject(i); // linea del gradereport

			if (item == null) // grade item no visible
				continue;

			JSONObject itemname = item.getJSONObject("itemname");
			int nivel = getNivel(itemname.getString("class"));

			Document content = Jsoup.parseBodyFragment(itemname.getString("content"));

			GradeItem gradeItem = new GradeItem();

			gradeItem.setLevel(nivel);
			gradeItem.setItemname(content.text());
			Element element;
			if ((element = content.selectFirst("i")) != null && element.className()
					.equals(FOLDER_ICON)) {
				gradeItem.setItemname(content.text());
				categories[nivel] = gradeItem;
				setFatherAndChildren(categories, nivel, gradeItem);

			} else if (categories[nivel] != null) {

				gradeItems.add(categories[nivel]);
				categories[nivel] = null;

			} else {

				gradeItems.add(gradeItem);
				setFatherAndChildren(categories, nivel, gradeItem);
			}

		}

		return gradeItems;
	}

	public static List<Course> searchCourse(JSONObject object) throws IOException {
		JSONArray coursesArray = object.getJSONArray("courses");
		SubDataBase<Course> dataBaseCourse = CONTROLLER.getDataBase()
				.getCourses();
		SubDataBase<CourseCategory> dataBaseCategory = CONTROLLER.getDataBase()
				.getCourseCategories();
		List<Course> list = new ArrayList<>();
		for (int i = 0; i < coursesArray.length(); ++i) {
			JSONObject jsObject = coursesArray.getJSONObject(i);
			Course course = dataBaseCourse.getById(jsObject.getInt("id"));
			course.setFullName(jsObject.getString(FULLNAME));
			course.setShortName(jsObject.optString(SHORTNAME));
			course.setSummary(jsObject.optString("summary"));
			course.setSummaryformat(DescriptionFormat.get(jsObject.optInt("summaryformat")));

			CourseCategory category = dataBaseCategory.getById(jsObject.getInt("categoryid"));
			category.setName(jsObject.getString("categoryname"));
			course.setCourseCategory(category);
			list.add(course);
		}
		createCourseAdministrationOptions(list);
		return list;
	}

	public static void createCourseAdministrationOptions(Collection<Course> courses) throws IOException {
		if (courses == null || courses.isEmpty()) {
			return;
		}
		List<Integer> courseids = courses.stream()
				.map(Course::getId)
				.collect(Collectors.toList());

		SubDataBase<Course> dataBaseCourse = CONTROLLER.getDataBase()
				.getCourses();
		JSONArray jsonArray = getJSONObjectResponse(new CoreCourseGetUserAdministrationOptions(courseids))
				.getJSONArray("courses");
		findPermission(jsonArray, dataBaseCourse, "reports", Course::setReportAccess);

		jsonArray = getJSONObjectResponse(new CoreCourseGetUserNavigationOptions(courseids)).getJSONArray("courses");
		findPermission(jsonArray, dataBaseCourse, "grades", Course::setGradeItemAccess);

	}

	public static void findPermission(JSONArray jsonArray, SubDataBase<Course> dataBaseCourse, String permission,
			BiConsumer<Course, Boolean> consumer) {
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Course course = dataBaseCourse.getById(jsonObject.getInt("id"));
			course.setCourseAccess(true);
			JSONArray options = jsonObject.getJSONArray("options");
			for (int j = 0; j < options.length(); ++j) {
				JSONObject option = options.getJSONObject(j);

				if (permission.equals(option.getString("name"))) {
					consumer.accept(course, option.getBoolean("available"));
				}
			}
		}

	}

	/**
	 * Inicializa los atributos basicos del grade item
	 * 
	 * @param gradeItems lista de grade item
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private static void setBasicAttributes(List<GradeItem> gradeItems, JSONObject jsonObject) {

		JSONObject usergrade = jsonObject.getJSONArray("usergrades")
				.getJSONObject(0);

		JSONArray gradeitems = usergrade.getJSONArray("gradeitems");

		if (gradeitems.length() != gradeItems.size()) {
			LOGGER.error(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño {} "
							+ "y de la funcion gradereport_user_get_grades_table se obtiene:{} ",
					gradeitems.length(), gradeItems.size());
			throw new IllegalStateException(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño"
							+ gradeitems.length() + "y de la funcion gradereport_user_get_grades_table se obtiene: "
							+ gradeItems.size());
		}

		for (int i = 0; i < gradeitems.length(); i++) {
			JSONObject gradeitem = gradeitems.getJSONObject(i);
			GradeItem gradeItem = gradeItems.get(i);

			gradeItem.setId(gradeitem.getInt("id"));

			CONTROLLER.getDataBase()
					.getGradeItems()
					.putIfAbsent(gradeItem.getId(), gradeItem);

			String itemtype = gradeitem.getString("itemtype");
			ModuleType moduleType;

			if ("mod".equals(itemtype)) {
				CourseModule module = CONTROLLER.getDataBase()
						.getModules()
						.getById(gradeitem.getInt("cmid"));
				gradeItem.setModule(module);
				moduleType = ModuleType.get(gradeitem.getString("itemmodule"));

			} else if ("course".equals(itemtype)) {
				moduleType = ModuleType.CATEGORY;

			} else {
				moduleType = ModuleType.get(itemtype);
			}
			gradeItem.setItemModule(moduleType);
			gradeItem.setWeightraw(gradeitem.optDouble("weightraw"));

			gradeItem.setGrademin(gradeitem.optDouble("grademin"));
			gradeItem.setGrademax(gradeitem.optDouble("grademax"));

		}

	}

	/**
	 * Añade las calificaciones a los usuarios.
	 * 
	 * @param gradeItems gradeitems
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private static void setEnrolledUserGrades(List<GradeItem> gradeItems, JSONObject jsonObject) {
		JSONArray usergrades = jsonObject.getJSONArray("usergrades");

		for (int i = 0; i < usergrades.length(); i++) {

			JSONObject usergrade = usergrades.getJSONObject(i);

			EnrolledUser enrolledUser = CONTROLLER.getDataBase()
					.getUsers()
					.getById(usergrade.getInt("userid"));

			JSONArray gradeitems = usergrade.getJSONArray("gradeitems");
			for (int j = 0; j < gradeitems.length(); j++) {
				JSONObject gradeitem = gradeitems.getJSONObject(j);
				GradeItem gradeItem = gradeItems.get(j);

				gradeItem.addUserGrade(enrolledUser, gradeitem.optDouble("graderaw"));
			}
		}

	}

	/**
	 * Crea la jerarquia de padre e hijo
	 * 
	 * @param categories grade item de tipo categoria
	 * @param nivel      nivel de jerarquia
	 * @param gradeItem  grade item
	 */
	protected static void setFatherAndChildren(GradeItem[] categories, int nivel, GradeItem gradeItem) {
		if (nivel > 1) {
			GradeItem padre = categories[nivel - 1];
			gradeItem.setFather(padre);
			padre.addChildren(gradeItem);
		}
	}

	/**
	 * Busca el nivel jerarquico del grade item dentro del calificador. Por ejemplo
	 * "level1 levelodd oddd1 b1b b1t column-itemname", devolvería 1.
	 * 
	 * @param stringClass el string del key "class" de "itemname"
	 * @return el nivel
	 */
	protected static int getNivel(String stringClass) {
		Matcher matcher = NIVEL.matcher(stringClass);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		throw new IllegalStateException("No se encuentra el nivel en " + stringClass
				+ ", probablemente haya cambiado el formato de las tablas.");
	}

	private CreatorUBUGradesController() {
		throw new UnsupportedOperationException();
	}

}
