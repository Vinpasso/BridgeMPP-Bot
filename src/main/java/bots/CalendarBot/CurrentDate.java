package bots.CalendarBot;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @author Bernie
 *
 */
public class CurrentDate {
	private static GregorianCalendar newCal () {
		return new GregorianCalendar(TimeZone.getTimeZone("CET"),Locale.GERMAN);
	}
	
	public static int getYear () {
		return newCal().get(Calendar.YEAR);
	}
	
	public static int getMonth () {
		return newCal().get(Calendar.MONTH) + 1;
	}
	
	public static int getDay () {		
		return newCal().get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getWeekday () {		
		return newCal().get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getHour () {		
		return newCal().get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute () {		
		return newCal().get(Calendar.MINUTE);
	}
	
	public static int getSecond () {		
		return newCal().get(Calendar.SECOND);
	}
	
	public static String getTime () {
		int hour = getHour();
		int minute = getMinute();
	    int second = getSecond();
		return "" + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
	}
	
	public static String getDate () {
		int day = getDay();
		int month = getMonth();
		int year = getYear();
		return "" + (day < 10 ? "0" + day : day) + "." + (month < 10 ? "0" + month : month) + "." + (year < 1000 ? "0" + (year < 100 ? "0" + (year < 10 ? "0" : "") : "") : "") + year;
	}
	
	public static String getDateWTime () {
		return getDate() + " " + getTime();
	}
}
