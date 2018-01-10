package model;

/**
 * Clase Lesson (Leci�n). Implementar en versiones futuras.
 * 
 * @author F�lix Nogal Santamar�a
 * @version 1.0
 *
 */
public class Lesson extends Activity {

	private static final long serialVersionUID = 1L;
	private String password;
	private int maxAnswers;
	private int maxAttempts;
	private int timeLimit; // 0 indica que no hay tiempo limite
	private int deadLine; // 0 indica que no hay fecha limite
	private boolean retake; // indica si se puede repetir la lecci�n
	
	/**
	 * Consturctor de una Lecci�n(Lesson).
	 * 
	 * @param itemName
	 * 		El nombre de la lecci�n.
	 * @param type
	 * 		El tipo.
	 * @param weight
	 * 		El peso.
	 * @param minRange
	 * 		El rango m�nimo.
	 * @param maxRange
	 * 		El rango m�ximo.
	 */
	public Lesson(String itemName, String type, float weight, String minRange, String maxRange) {
		super(itemName, type, weight, minRange, maxRange);
	}



	/**
	 * Devuelve la contrase�a de la lecci�n.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}



	/**
	 * Modifica la contrase�a de la lecci�n.
	 * @param password
	 * 		La contrase�a.
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	/**
	 * Devuelve el n�mero m�ximo de respuestas.
	 * 
	 * @return the maxAnswers
	 */
	public int getMaxAnswers() {
		return maxAnswers;
	}



	/**
	 * Modifica el n�mero m�ximo de respuestas.
	 * @param maxAnswers
	 * 		El n�mero m�ximo de respuestas.
	 */
	public void setMaxAnswers(int maxAnswers) {
		this.maxAnswers = maxAnswers;
	}



	/**
	 * Devuelve el numero maximo de intentos.
	 * 
	 * @return the maxAttempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}



	/**
	 * Modifica el n�mero m�ximo de intentos.
	 * @param maxAttempts
	 * 		El n�mero m�ximo de intentos.
	 */
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}



	/**
	 * Devuelve 1 si la lecci�n tiene tiempo l�mite y 0 si no.
	 * 
	 * @return the timeLimit
	 */
	public int getTimeLimit() {
		return timeLimit;
	}



	/**
	 * Modifica el tiempo l�mite de la lecci�n.
	 * 
	 * @param timeLimit
	 * 		El tiempo l�mite de la lecci�n.
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}



	/**
	 * Devuelve 0 si la lecci�n tiene deadline y 0 si no.
	 *
	 * @return the deadLine
	 */
	public int getDeadLine() {
		return deadLine;
	}



	/**
	 * Modifica la deadLine de la lecci�n.
	 * 
	 * @param deadLine
	 * 		El deadLine.
	 */
	public void setDeadLine(int deadLine) {
		this.deadLine = deadLine;
	}



	/**
	 * Devuelve si la lecci�n se puede repetir o no.
	 * 
	 * @return the retake
	 */
	public boolean isRetake() {
		return retake;
	}



	/**
	 * Modifica si la leccion se puede repetir o no.
	 * 
	 * @param retake
	 * 		True si se pude, False si no se puede.
	 * 	
	 */
	public void setRetake(boolean retake) {
		this.retake = retake;
	}

}
