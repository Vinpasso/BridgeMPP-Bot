package bots.CalendarBot;

public class EventBirthday extends Event {
	
	public EventBirthday (String wat, int date, int repeat, int remind, int currentDate, int firstYear) {
		super(wat, date, repeat, remind, currentDate, firstYear);
	}
	
	@Override
	public String toStringList (int firstYear) {
		return CalDateFormat.minToDate(date, firstYear) + ": " + watWithS() + " Geburtstag, repeat: " + repeatToString() + ", remind: " + remindToString();
	}
	
	@Override
	public String toStringRepeat (int firstYear) {
		return new WishChooser().toString() + "\nCalendarBot wuenscht " + wat + " alles Gute zum Geburtstag!\n" + toStringBirthday(firstYear) + "\nshow me birthday cake" + wat;
	}
	
	@Override
	public String toStringRemind (int firstYear) {
		return (remind == 1440 ? "Morgen: " : CalDateFormat.minToDate(date, firstYear) + ": ") + toStringBirthday(firstYear);
	}
	
	private String toStringBirthday(int firstYear) {
		int currentYear = CurrentDate.getYear();
		return watWithS()
		+ (currentYear - CalDateFormat.minToDateSplitted(date, firstYear)[2]) + ". Geburtstag";
	}
	private String watWithS () {
		return wat + ((wat.charAt(wat.length() - 1) != 's' && wat.charAt(wat.length() - 1) != 'x') ? "s " : "\' ");
	}
	
	
	private String toStringShowMe (int firstYear) {
		int currentYear = CurrentDate.getYear();
		return "show me " + (currentYear - CalDateFormat.minToDateSplitted(date, firstYear)[2]) + " birthday";
	}
}
