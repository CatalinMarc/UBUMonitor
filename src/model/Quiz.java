package model;

import java.util.Date;

/**
 * Clase Quiz (Cuestionario). Implementar en versiones futuras.
 * 
 * @author Claudia Mart�nez Herrero
 * @version 1.0
 *
 */
public class Quiz extends Activity {
	private Date timeOpen;
	private Date timeClose;
	private String password;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de una actividad con todos sus par�metros
	 * 
	 * @param itemName
	 *            nombre de la actividad
	 * @param type
	 *            tipo de actividad
	 * @param weight
	 *            peso
	 * @param minRange
	 *            rango m�nimo de nota
	 * @param maxRange
	 *            rango m�ximo de nota
	 */
	public Quiz(String itemName, String type, float weight, String minRange, String maxRange) {
		super(itemName, type, weight, minRange, maxRange);
	}

	/**
	 * Devuelve la fecha de apertura del cuestionario
	 * 
	 * @return fecha de apertura
	 */
	public Date getTimeOpen() {
		return timeOpen;
	}

	/**
	 * Modifica la fecha de apertura del cuestionario
	 * 
	 * @param timeOpen
	 *            fecha de apertura
	 */
	public void setTimeOpen(Date timeOpen) {
		this.timeOpen = timeOpen;
	}

	/**
	 * Devuelve la fecha de cierre del cuestionario.
	 * 
	 * @return fecha de cierre
	 */
	public Date getTimeClose() {
		return timeClose;
	}

	/**
	 * Modifica la fecha de cierre del cuestionario.
	 * 
	 * @param timeClose
	 * 		Fecha de cierre.
	 */
	public void setTimeClose(Date timeClose) {
		this.timeClose = timeClose;
	}

	/**
	 * Devuelve la contrase�a del cuestionario.
	 * 
	 * @return contrase�a
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifica la contrase�a del cuestionario.
	 * 
	 * @param password
	 * 		La contrase�a.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
