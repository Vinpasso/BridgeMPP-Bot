package bots.CalendarBot;

public class EventBirthday extends Event {
	
	public EventBirthday (String wat, int date, int repeat, int remind, int firstYear, boolean tumtum) {
		super(wat, date, repeat, remind, firstYear, tumtum);
	}
	
	@Override
	public String toStringList (boolean next) {
		return CalDateFormat.minToDate((next ? nextRepeat : date), firstYear) + ": " + watWithS() + " Geburtstag, repeat: " + repeatToString(repeat) + ", remind: " + remindToString(remind);
	}
	
	@Override
	public String toStringRepeat () {
		return new BirthdayWishes().getWish(age()) + "\nCalendarBot wuenscht " + wat + " alles Gute zum Geburtstag!\n" + toStringBirthday();
	}
	
	@Override
	public String toStringRemind () {
		return "Reminder: " + CalDateFormat.minToWeekday(nextRepeat, firstYear) + " " + CalDateFormat.minToDate(nextRepeat, firstYear) + ": " + toStringBirthday();
	}
	
	private String toStringBirthday() {
		return watWithS() + age() + ". Geburtstag";
	}
	
	private String watWithS () {
		return wat + ((wat.charAt(wat.length() - 1) != 's' && wat.charAt(wat.length() - 1) != 'x') ? "s " : "\' ");
	}
	
	private int age () {		
		return (CurrentDate.getYear() - CalDateFormat.minToDateSplitted(date, firstYear)[2]);
	}
}
