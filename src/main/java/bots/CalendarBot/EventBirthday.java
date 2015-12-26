package bots.CalendarBot;

public class EventBirthday extends Event {
	
	public EventBirthday (String wat, int date, int repeat, int remind, int currentDate, int firstYear) {
		super(wat, date, repeat, remind, currentDate, firstYear);
	}
	
	@Override
	public String toStringList (boolean next) {
		return CalDateFormat.minToDate((next ? nextRepeat : date), firstYear) + ": " + watWithS() + " Geburtstag, repeat: " + repeatToString() + ", remind: " + remindToString();
	}
	
	@Override
	public String toStringRepeat () {
		return new BirthdayWishes().getWish(age()) + "\nCalendarBot wuenscht " + wat + " alles Gute zum Geburtstag!\n" + toStringBirthday() + "\nshow me birthday cake" + wat;
	}
	
	@Override
	public String toStringRemind () {
		return (remind == 1440 ? "Morgen: " : CalDateFormat.minToDate(date, firstYear) + ": ") + toStringBirthday();
	}
	
	private String toStringBirthday() {
		return watWithS()
		+ age() + ". Geburtstag";
	}
	private String watWithS () {
		return wat + ((wat.charAt(wat.length() - 1) != 's' && wat.charAt(wat.length() - 1) != 'x') ? "s " : "\' ");
	}
	
	private int age () {
		
		return (CurrentDate.getYear() - CalDateFormat.minToDateSplitted(date, firstYear)[2]);
	}
}
