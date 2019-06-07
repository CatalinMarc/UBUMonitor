package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * "The user with id '' viewed the user log report for the user with id ''."
 * @author Yi Peng Ji
 *
 */
public class UserAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserAffected.
	 */
	private static UserAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAffected() {
	}

	/**
	 * Devuelve la instancia única de UserAffected.
	 * @return instancia singleton
	 */
	public static UserAffected getInstance() {
		if (instance == null) {
			instance = new UserAffected();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setAffectedUserById(log, ids.get(1));
	}

}
