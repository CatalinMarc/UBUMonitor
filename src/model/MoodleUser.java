package model;

import java.time.Instant;
import java.util.ArrayList;

import java.util.List;

import javafx.scene.image.Image;

/**
 * Clase para el usuario logeado en la aplicación
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MoodleUser {
	private int id;
	private String userName;
	private String fullName;
	private String email;
	private Instant firstAccess;
	private Instant lastAccess;
	private String city;
	private String country;
	private Image userPhoto;
	private List<Course> courses;
	private String timezone;

	/**
	 * Constructor de MoodleUser sin parámetros
	 */
	public MoodleUser() {
		this.setId(0);
		this.courses = new ArrayList<>();
	}

	/**
	 * Constructor de MoodleUser con parámetros
	 * 
	 * @param id
	 *            id del usuario logueado
	 * @param userName
	 *            nombre de usuario
	 * @param fullName
	 *            nombre completo
	 * @param eMail
	 *            correo electrónico
	 * @param firstAccess
	 *            fecha de primer acceso
	 * @param lastAccess
	 *            fecha de último acceso
	 * @param city
	 *            ciudad
	 * @param country
	 *            país
	 */
	public MoodleUser(int id, String userName, String fullName, String eMail, Instant firstAccess, Instant lastAccess,
			String city, String country,String timezone) {
		this.setId(id);
		this.setUserName(userName);
		this.setFullName(fullName);
		this.setEmail(eMail);
		this.setFirstAccess(firstAccess);
		this.setLastAccess(lastAccess);
		this.setCity(city);
		this.setCountry(country);
		this.setTimezone(timezone);
		this.courses = new ArrayList<>();
	}

	/**
	 * Devuelve el id del usuario
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del usuario.
	 * 
	 * @param id
	 * 		El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre de usuario
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Modifica el nombre del usuario.
	 * 
	 * @param userName
	 * 		El nombre de usuario.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Devuelve el nombre completo del usuario
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Modifica el nombre completo del usuario.
	 * 
	 * @param fullName
	 * 		El nombre completo.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Devuelve el email del usuario
	 * 
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Modifica el email del usuario.
	 * 
	 * @param email
	 * 		EL email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Devuelve la fecha de primer acceso
	 * 
	 * @return firstAccess
	 */
	public Instant getFirstAccess() {
		return this.firstAccess;
	}

	/**
	 * Modifica la fecha de primer acceso.
	 * 
	 * @param firstAccess
	 * 		La fecha de primer acceso.
	 */
	public void setFirstAccess(Instant firstAccess) {
		this.firstAccess = firstAccess;
	}

	/**
	 * Devuelve la fecha de último acceso
	 * 
	 * @return lastAccess
	 */
	public Instant getLastAccess() {
		return this.lastAccess;
	}

	/**
	 * Modifica la fecha de último acceso.
	 * 
	 * @param lastAccess
	 * 		La fecha de último acceso.
	 */
	public void setLastAccess(Instant lastAccess) {
		this.lastAccess = lastAccess;
	}

	/**
	 * Devuelve la ciudad del usuario
	 * 
	 * @return city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Modifica la ciudad del usuario.
	 * 
	 * @param city
	 * 		La ciudad.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Devuelve el país
	 * 
	 * @return country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Modifica el país del usuario.
	 * 
	 * @param country
	 * 		El país.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the userPhoto
	 */
	public Image getUserPhoto() {
		return userPhoto;
	}

	/**
	 * @param userPhoto the userPhoto to set
	 */
	public void setUserPhoto(Image userPhoto) {
		this.userPhoto = userPhoto;
	}

	/**
	 * Devuelve la lista de cursos que en los que está atriculado el usuario
	 * 
	 * @return lista de cursos
	 */
	public List<Course> getCourses() {
		return this.courses;
	}

	/**
	 * Modifica la lista de cursos en los que está matriculado el usuario.
	 * 
	 * @param courses
	 * 		La lista de cursos.
	 */
	public void setCourses(List<Course> courses) {
		this.courses=courses;
	}

	public void setTimezone(String timezone) {
		this.timezone=timezone;
		
	}
	public String getTimezone() {
		return this.timezone;
	}

}
