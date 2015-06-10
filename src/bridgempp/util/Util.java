package bridgempp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.time.Duration;


public class Util {

	private static SimpleDateFormat standardFormat = new SimpleDateFormat("dd MMM yyyy G HH:mm:ss zzzz");

	{
		standardFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static String currentTimeAndDate()
	{
		return standardFormat.format(new Date(System.currentTimeMillis()));
	}

	public static String timeDeltaNow(long birthday) {
		return timeDelta(birthday, System.currentTimeMillis());
	}
		
		
	public static String timeDelta(long then, long now)
	{
		Duration duration = Duration.ofMillis(now-then);
		return duration.toDays() + " days " + duration.toHours() + " hours " + duration.toMinutes() + " minutes " + duration.getSeconds() + " seconds";
	}
	
}
