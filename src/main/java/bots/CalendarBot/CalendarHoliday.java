package bots.CalendarBot;

/**
 * 
 * @author Bernie
 *
 */
public class CalendarHoliday extends Calendar {
	public CalendarHoliday(int firstYear, String filepath) {
		super("Holidays", firstYear, filepath, 0, 0, true);
		load();
	}
	
	@Override
	protected void load() {
		int dateEaster = dateEaster();
		int dateAdvent1 = dateAdvent1();
		//static official holidays
		insert(new EventHoliday("Neujahr", "CalendarBot wünscht ein schoenes neues Jahr " + (CurrentDate.getYear() + 1), dateToMin("01.01.", "00:00", true),360, firstYear, defaultTumtum));
		insert(new EventHoliday("Heilige Drei König", "", dateToMin("06.01."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("Maifeiertag", "", dateToMin("01.05."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("Mariae Himmelfahrt", "", dateToMin("15.08."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("Tag der deutschen Einheit", "", dateToMin("03.10."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("Allerheiligen", "", dateToMin("01.11."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("1. Weihnachtsfeiertag", "", dateToMin("25.12."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("2. Weihnachtsfeiertag", "", dateToMin("26.12."), 360, firstYear, defaultTumtum));
		
		//dynamic official holidays
		insert(new EventHoliday("Ostersonntag", "CalendarBot wünscht frohe Ostern", dateEaster, firstYear, defaultTumtum));
		insert(new EventHoliday("Ostermontag", "", dateEaster + 1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Karfreitag", "", dateEaster - 2*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Christi Himmelfahrt", "", dateEaster + 39*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Pfingstsonntag", "CalendarBot wünscht frohe Pfingsten", dateEaster + 49*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Pfingstmontag", "", dateEaster + 50*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Fronleichnam", "", dateEaster + 60*1440, firstYear, defaultTumtum));
		
		//static unofficial
		insert(new EventHoliday("Valentinstag", "(inoffiziell)", dateToMin("14.02."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Europatag (des Europarates)", "(inoffiziell)", dateToMin("05.05."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Europatag (der Europäischen Union)", "(inoffiziell)", dateToMin("09.05."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Allerseelen", "(inoffiziell)", dateToMin("02.11."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Nikolaus", "(inoffiziell)", dateToMin("06.12."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Heiligabend", "(inoffiziell)\n ... dann steht das Christkind vor der Tür.\nCalendarBot wünscht Frohe Weihnachten", dateToMin("24.12."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Silvester", "(inoffiziell)\nCalendarBot wünscht einen guten Rutsch ins Jahr " + (CurrentDate.getYear()+1), dateToMin("31.12."), 360, firstYear, defaultTumtum));
		insert(new EventHoliday("Faschingsanfang", "(inoffiziell)", dateToMin("11.11.", "11:11", true), 360, firstYear, defaultTumtum));		

		
		//dynamic unofficial
		insert(new EventHoliday("Unsinngier Donnerstag", "(inoffiziell)", dateEaster - 52*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Rosenmontag", "(inoffiziell)", dateEaster - 48*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Fastnacht", "(inoffiziell)", dateEaster - 47*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Aschermittwoch", "(inoffiziell)", dateEaster - 46*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Gründonnerstag", "(inoffiziell)", dateEaster - 3*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Karsamstag", "(inoffiziell)", dateEaster - 1*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Palmsonntag", "(inoffiziell)\nWer zuletzt aufsteht ist der Palmesel! Schreibe \"Esel\" wenn du aufgestanden bist", dateEaster - 7*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Muttertag", "(inoffiziell)", dateByWeekday("08.05.", "Sunday"), 0, 10080, firstYear, defaultTumtum));
		insert(new EventHoliday("Erntedankfest", "(inoffiziell)", dateByWeekday("1.10.", "Sunday"), firstYear, defaultTumtum));
		insert(new EventHoliday("1. Advent", "(inoffiziell)\nAdvent Advent ein Lichtlein brennt. Erst eins, ...", dateAdvent1, firstYear, defaultTumtum));
		insert(new EventHoliday("2. Advent", "(inoffiziell)\n ... dann zwei, ...", dateAdvent1 + 7*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("3. Advent", "(inoffiziell)\n ... dann drei, ...", dateAdvent1 + 14*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("4. Advent", "(inoffiziell)\n ... dann vier, ...", dateAdvent1 + 21*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Volkstrauertag", "(inoffiziell)", dateAdvent1 - 14*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Buß- und Bettag", "(inoffiziell)", dateAdvent1 - 11*1440, firstYear, defaultTumtum));
		insert(new EventHoliday("Totensonntag", "(inoffiziell)", dateAdvent1 - 7*1440, firstYear, defaultTumtum));
		
		//tum
		//insert(new EventHoliday("Dies Academicus", "(inoffiziell)", dateToMin("03.12."), 360, firstYear, defaultTumtum));		
		
		//Italian
		insert(new EventHoliday("Festa Nazionale", "(inoffiziell)\nItalienischer Nazionalfeiertag", dateToMin("17.03."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Anniversario della Liberazione", "(inoffiziell)\nTag der Befreiung Italiens", dateToMin("25.04."), 360, firstYear, defaultTumtum));		
		insert(new EventHoliday("Festa della Repubblica", "(inoffiziell)\nTag der Republik (Italiens)", dateToMin("02.06."), 360, firstYear, defaultTumtum));		


	}
	
	/**
	 * 
	 * @return date of Easter of Easter of this year
	 */
	private int dateEaster () {
		boolean nextYear = false;
		int date;
		do {
			date = dateToMin("20.03.", "00:00", nextYear);
			nextYear = true;
			while((int) (new LunarPhase(firstYear, 0, (double) (date)).getDayOfLunarPhase()) != 15 ) {
				date++;				
			}
			date = dateByWeekday(date, "Sunday");
		} while ((date + 61*1440) < CurrentDate.getDateInMin(firstYear)); // date of Easter+61, otherwise all coming events depending on Easter will be skipped
		return date + 8*60;
	}
	
	/**
	 * 
	 * @return date of 1st Advent of this year
	 */
	private int dateAdvent1 () {
		return dateByWeekday (dateToMin("27.11.", "08:00", false), "Sunday");
		//date of 1st Advent had to be this year, otherwise all coming events depending on the 1st Advent will be skipped
	}
	
	/**
	 * sets date to the next weekday (year of date)
	 * @param date
	 * @param weekday
	 * @return
	 */
	private int dateByWeekday(int date, String weekday) {
		while(!CalDateFormat.minToWeekday(date, firstYear).equals(weekday)) {
			date++;
		}
		return date;
	}
	
	/**
	 * sets date to the next weekday (year of next repeat)
	 * @param date
	 * @param weekday
	 * @return
	 */
	private int dateByWeekday(String date, String weekday) {
		boolean nextDate = false;
		int min;
		do {
			min = dateToMin(date, "00:00", nextDate);
			nextDate = true;
			while(!CalDateFormat.minToWeekday(min, firstYear).equals(weekday)) {
				min++;
			}
		} while (min < CurrentDate.getDateInMin(firstYear));
		return min + 8*60;
	}

	
	@Override
	protected boolean save() {
		return true;
	}
	
	@Override
	public boolean deleteCalendar() {
		//users should'nt be allowed to delete this calendar
		return false;
	}
	
	@Override
	public boolean add (String wat, int date, int repeat, int remind) {
		//users should'nt be allowed to add events
		return false;
	}
	
	@Override
	public boolean add (String wat, int date) {
		return false;//add(wat, date, defaultRepeat, defaultRemind);
	}
	
	@Override
	public boolean removeEvent (int index) {
		//users should'nt be allowed to remove events
		return false;
	}
	
	@Override
	public boolean removeEvent (String name) {
		//users should'nt be allowed to remove events
		return false;
	}
	
	@Override
	public boolean removeAllEvents () {
		//users should'nt be allowed to remove events
		return false;
	}
	
	/**
	 * 
	 * @param date
	 * @param time
	 * @param nextDate true: return next repeat of this date, false: return date of this year
	 * @return
	 */
	private int dateToMin (String date, String time, boolean nextDate) {
		int min = CalDateFormat.dateToMin(date + CurrentDate.getYear() + " " + time, firstYear);
		if ((min > CurrentDate.getDateInMin(firstYear)) || !nextDate) {
			return min;
		}
		else {
			return CalDateFormat.dateToMin(date + (CurrentDate.getYear()+1) + " " + time, firstYear);
		}
	}
	
	private int dateToMin (String date) {
		return dateToMin (date, "08:00", true);
	}
}
