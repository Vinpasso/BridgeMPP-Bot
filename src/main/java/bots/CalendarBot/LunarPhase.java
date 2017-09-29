package bots.CalendarBot;


/**
 * 
 * @author Bernie
 *
 */
public class LunarPhase {
	private final double firstNewMoon;
	private final double daysLunarPhase = 29.530588;
	/**
	 * date in days
	 */
	private double date;
	int firstYear;
	int lang;
	
	/**
	 * 
	 * @param firstYear
	 * @param lang language: 0: English, 1: German
	 */
	public LunarPhase (int firstYear, int lang, double date) {
		this.firstYear = firstYear;
		this.firstNewMoon = CalDateFormat.dateToMin("07.01.1970 20:55", firstYear) / 1440;
		this.date = date / 1440;
		this.lang = lang;
	}
	
	/**
	 * 
	 * {@code date} = currentDate
	 * @param firstYear
	 * @param lang
	 */
	public LunarPhase (int firstYear, int lang) {
		this(firstYear, lang, (CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear) / 1440));
	}
	
	/**
	 * {@code lang} = 0
	 * @param firstYear
	 */
	public LunarPhase (int firstYear) {
		this(firstYear, 0);
	}
	
	/**
	 * 
	 * @return
	 */
	public  double getDayOfLunarPhase () {
		return (date - firstNewMoon) % daysLunarPhase;
	}
	
	/**
	 * 
	 * @param dayOfLunarPhae
	 * @param lang languages: 0: English, 1: German
	 * @return
	 */
	public String getLunarPhase () {
		//Englisch
		if (lang == 0) {
			switch ((int) (getDayOfLunarPhase())) {
			case 29:
			case 0:
				return "New Moon";
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				return "Waxing crescent moon";
			case 7:
			case 8:
				return "First quarter moon";
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				return "Waxing gibbous moon";
			case 14:
			case 15:
				return "Full moon";
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
				return "Waning gibbous moon";
			case 22:
				return "Last quarter moon";
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
				return "Waning crescent moon";			
				default:
					return "Lost moon";
			}
		}
		//German
		if (lang == 1) {
			switch ((int) (getDayOfLunarPhase())) {
			case 29:
			case 0:
				return "Neumond";
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				return "Zunehmender Sichelmond";
			case 7:
			case 8:
				return "Erstes Viertel";
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				return "Zunehmender Mond";
			case 14:
			case 15:
				return "Vollmond";
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
				return "Abnehmender Mond";
			case 22:
				return "Letztes Viertel";
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
				return "Abnehmender Sichelmond";			
				default:
					return "Mond verloren";
			}
		}
		else return null;
	}
	
	@Override
	public String toString() {
		switch (lang) {
		case 0:
			return  "Age of the moon: " + ((int) (getDayOfLunarPhase())) + " days\nLunar Phase: " + getLunarPhase();
		case 1:
			return "Alter der Mondphase: " + ((int) (getDayOfLunarPhase())) + " Tage\nMondphase: " + getLunarPhase();
			default:
				return null;
		}
	}
	
}






