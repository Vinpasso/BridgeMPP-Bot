package bots.TUMCanteenBot;

import java.util.Calendar;
import java.util.TimeZone;

public class CanteenTimeHelper {
	
	public static final TimeZone CET_ZONE = TimeZone.getTimeZone("CET");
	
	public static long getMillisecondsUntilTomorrow(int hour, int minutes, int seconds) {
		Calendar calendar = getNow();
		calendar.roll(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);
		return calendar.getTimeInMillis() - System.currentTimeMillis();
	}
	
	public static Calendar getNow() {
		return Calendar.getInstance(CET_ZONE);
	}
	
	public static Calendar getToday() {
		Calendar calendar = getNow();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
}
