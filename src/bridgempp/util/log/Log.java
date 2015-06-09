package bridgempp.util.log;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Log {

	public static void log(Level level, String message)
	{
		Logger.getLogger(getCallingClass().getName()).log(level, message);
	}
	
	public static void log(Level level, String message, Throwable cause)
	{
		Logger.getLogger(getCallingClass().getName()).log(level, message, cause);
	}
	
	public static void log(Level level, Exception e)
	{
		Logger.getLogger(getCallingClass().getName()).log(level, "Unknown Exception has occured", e);
	}
	
	private static Class<?> getCallingClass()
	{
		return Thread.currentThread().getStackTrace()[3].getClass();
	}
	
}
