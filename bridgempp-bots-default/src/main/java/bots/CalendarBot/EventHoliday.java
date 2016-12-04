package bots.CalendarBot;

/**
 * 
 * @author Bernie
 *
 */
public class EventHoliday extends Event {
	protected String message;
	
	/**
	 * 
	 * @param wat description of event
	 * @param message 
	 * @param date date of event (minutes since 01.01.{@code firstYear})
	 * @param repeat number of days to repeat event (-1 = off)
	 * @param remind number of minutes to remind before event starts (-1 = off)
	 * @param currentDate current date as past minutes since 01.01.{@code firstYear} 
	 * @param firstYear 
	 */
	public EventHoliday (String wat, String message, int date, int repeat, int remind, int firstYear, boolean tumtum) {
		super(wat, date, repeat, remind, firstYear, tumtum);
		this.message = message;
	}
	
	public EventHoliday (String wat, String message, int date, int repeat, int firstYear, boolean tumtum) {
		this(wat, message, date, repeat, 0, firstYear, tumtum);
	}
	
	public EventHoliday (String wat, String message, int date, int firstYear, boolean tumtum) {
		this(wat, message, date, 0, 0, firstYear, tumtum);
	}
	
	/**
	 * 
	 * @param firstYear
	 * @return
	 */
	public String toStringRepeat () {
		return "Today " + CalDateFormat.minToDate(nextRepeat, firstYear).substring(11, 16) + ": " + wat + "\n" + message;
	}
}
