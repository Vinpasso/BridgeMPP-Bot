package bridgempp.util;

import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgempp.bot.wrapper.Message;

import java.time.Duration;


public class Util {

	private static SimpleDateFormat standardFormat = new SimpleDateFormat("dd MMMM yyyy G HH:mm:ss zzzz");

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
		return duration.toDays() + " days " + duration.toHours() % 24 + " hours " + duration.toMinutes() % 60 + " minutes " + duration.getSeconds() % 60 + " seconds";
	}
	
	public static Object[] parseParametersCommandLineStyle(Parameter[] parameters, String message, Message bridgemppMessage)
	{
		Class<?>[] classes = Arrays.stream(parameters).map(e -> e.getType()).toArray(l -> new Class<?>[l]);
		return parseParametersCommandLineStyle(classes, message, bridgemppMessage);
	}
	
	public static Object[] parseParametersCommandLineStyle(Class<?>[] parameters, String message, Message bridgemppMessage)
	{
		if (parameters.length == 0)
		{
			return new Object[0];
		}
		if (parameters.length == 1 && parameters[0].equals(Message.class))
		{
			return new Object[] { bridgemppMessage };
		}
		String[] splittedString = splitCommandLine(message);
		Object[] parameterObjects = new Object[parameters.length];
		int splittedProgress = 0;
		for (int i = 0; i < parameterObjects.length; i++)
		{
			if(parameters[i].equals(Message.class))
			{
				parameterObjects[i] = bridgemppMessage;
				continue;
			}
			if(splittedProgress >= splittedString.length)
			{
				return null;
			}
			switch (parameters[i].getName())
			{
				case "java.lang.String":
					parameterObjects[i] = splittedString[splittedProgress];
					break;
				case "boolean":
					parameterObjects[i] = Boolean.parseBoolean(splittedString[splittedProgress]);
					break;
				case "int":
					parameterObjects[i] = Integer.parseInt(splittedString[splittedProgress]);
					break;
				case "double":
					parameterObjects[i] = Double.parseDouble(splittedString[splittedProgress]);
					break;
				case "float":
					parameterObjects[i] = Float.parseFloat(splittedString[splittedProgress]);
					break;
				default:
					parameterObjects[i] = splittedString[splittedProgress];
					break;
			}
			splittedProgress++;
		}
		if(splittedProgress != splittedString.length)
		{
			return null;
		}
		return parameterObjects;
	}
	
	public static String[] splitCommandLine(String message)
	{
		message = message + " ";
		LinkedList<String> list = new LinkedList<>();
		char[] characters = message.toCharArray();
		char delimiter = 0;
		int startSequence = 0;
		for (int i = 0; i < characters.length; i++)
		{
			if (delimiter == 0)
			{
				if (Character.isWhitespace(characters[i]))
				{
					continue;
				}
				if (characters[i] == '\'' || characters[i] == '\"')
				{
					startSequence = i;
					delimiter = characters[i];
				} else
				{
					startSequence = i - 1;
					delimiter = ' ';
				}
			} else
			{
				if (characters[i] == delimiter && characters[i - 1] != '\\')
				{
					if (startSequence + 1 > i - 1)
					{
						list.add("");
					} else
					{
						list.add(message.substring(startSequence + 1, i).replace("\\" + delimiter, "" + delimiter));
					}
					delimiter = 0;
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private static Pattern shellParser = Pattern.compile("((\"|')?(.*?)(?<!\\\\)\\2|((?:\\S|(?<=\\\\)\\s)+))", Pattern.DOTALL);
	
	public static String[] parseStringCommandLineStyle(String parameters)
	{
		LinkedList<String> results = new LinkedList<>();
		Matcher matcher = shellParser.matcher(parameters);
		while(matcher.find())
		{
			if(matcher.group(4) != null)
			{
				//No Quotes
				results.add(matcher.group(4).replaceAll("\\(.)", "$1"));
			}
			else
			{
				//Quoted String
				results.add(matcher.group(3).replaceAll("\\(.)", "$1"));
			}
		}
		return results.toArray(new String[results.size()]);
	}

	
}
