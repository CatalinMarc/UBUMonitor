package webservice;

/**
 * Clase que recoge las funciones de servicios web a utilizar.
 * 
 * @author Claudia Mart�nez Herrero
 * @version 1.0
 *
 */
public class MoodleOptions {
	
	private MoodleOptions() {
	    throw new IllegalStateException("Clase de utilidad");
	}
	
	/**
	 * Devuelve token de usuario
	 */
	public static final String SERVICIO_WEB_MOODLE = "moodle_mobile_app";
	/**
	 * Devuelve id de usuario y atributos
	 */
	public static final String OBTENER_INFO_USUARIO = "core_user_get_users_by_field";
	/**
	 * Devuelve los cursos en los que est� matriculado el usuario
	 */
	public static final String OBTENER_CURSOS = "core_enrol_get_users_courses";
	/**
	 * Devuelve los usuarios matriculados en un curso
	 */
	public static final String OBTENER_USUARIOS_MATRICULADOS = "core_enrol_get_enrolled_users";
	/**
	 * Devuelve todas las categor�as y elementos evaluables de un curso
	 */
	public static final String OBTENER_TABLA_NOTAS = "gradereport_user_get_grades_table";
	/**
	 * Devuelve informaci�n sobre los assignments (tareas) que hay en un curso
	 */
	public static final String OBTENER_ASSIGNMENTS = "mod_assign_get_assignments";
	/**
	 * Devuelve informaci�n sobre los quizs (cuestionarios) que hay en un curso
	 */
	public static final String OBTENER_QUIZZES = "mod_quiz_get_quizzes_by_courses";
	/**
	 * Devuelve las notas de todos los elementos del calificador (Moodel 3.3)
	 */
	public static final String OBTENER_NOTAS_ALUMNO = "gradereport_user_get_grade_items";
	/**
	 * Devuelve informaci�n sobre una lecci�n (Moodel 3.3)
	 */
	public static final String OBTENER_INFO_LECCION = "mod_lesson_get_lesson";	
	/**
	 * Devuelve informaci�n sobre una escala.
	 */
	public static final String OBTENER_ESCALA = "core_competency_get_scale_values";	
}
