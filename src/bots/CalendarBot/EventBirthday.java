package bots.CalendarBot;

public class EventBirthday extends Event {
	
	public EventBirthday (String wat, int date, int repeat, int remind, int currentDate, int firstYear) {
		super(wat, date, repeat, remind, currentDate, firstYear);
	}
	
	@Override
	public String toStringRepeat (int firstYear) {
		return new WishChooser().toString() + "\nCalendarBot wuenscht " + wat + " alles Gute zum Geburtstag!\n" + toStringBirthday(firstYear);
	}
	
	@Override
	public String toStringRemind (int firstYear) {
		return (remind == 1440 ? "Morgen: " : CalDateFormat.minToDate(date, firstYear) + ": ") + toStringBirthday(firstYear);
	}
	
	private String toStringBirthday(int firstYear) {
		int currentYear = CurrentDate.getYear();
		return wat + ((wat.charAt(wat.length() - 1) != 's' || wat.charAt(wat.length() - 1) != 'x') ? "s " : "\' ")
		+ (currentYear - CalDateFormat.minToDateSplitted(date, firstYear)[2]) + ". Geburtstag";
	}
}
