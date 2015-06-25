package bots.CalendarBot;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 * @author Bernie
 *
 */
public class CurrentDate {
	private static GregorianCalendar date;
	public CurrentDate () {
		
	}	
	
	public static int getYear () {
		date = new GregorianCalendar();
		return date.get(Calendar.YEAR);
	}
	
	public static int getMonth () {
		date = new GregorianCalendar();
		return date.get(Calendar.MONTH) + 1;
	}
	
	public static int getDay () {
		date = new GregorianCalendar();
		return date.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getWeekday () {
		date = new GregorianCalendar();
		return date.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getHour () {
		date = new GregorianCalendar();
		return date.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute () {
		date = new GregorianCalendar();
		return date.get(Calendar.MINUTE);
	}
	
	public static int getSecond () {
		date = new GregorianCalendar();
		return date.get(Calendar.SECOND);
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
