package bridgempp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import bridgempp.bot.wrapper.Bot;


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
	
	public static void log(Level level, String message, Bot bot)
	{
		Logger.getLogger(getCallingClass().getName()).log(level, bot.name + ": " + message);
	}
	
	public static void wrapperLog(Level level, String message)
	{
		Logger.getLogger(getCallingClass().getName()).log(level, "Bot Wrapper: " + message);
	}	
}
