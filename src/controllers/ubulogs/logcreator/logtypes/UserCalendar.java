package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' created the event '' with id ''.
 * The user with id '' deleted the event '' with id ''.
 * The user with id '' updated the event '' with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCalendar extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserCalendar instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCalendar() {
	}

	/**
	 * Return a singleton instance of UserCalendar.
	 */
	public static UserCalendar getInstance() {
		if (instance == null) {
			instance = new UserCalendar();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO core_calendar_get_calendar_events
	}

}
