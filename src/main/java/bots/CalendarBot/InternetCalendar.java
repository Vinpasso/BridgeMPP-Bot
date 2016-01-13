package bots.CalendarBot;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class InternetCalendar extends Calendar
{
	/**
	 * 
	 * @param name
	 * @param firstYear
	 * @param filepath
	 */
	public InternetCalendar(String name, int firstYear, String filepath)
	{
		this(name, firstYear, filepath, -1, -1, false);
	}

	/**
	 * 
	 * @param name
	 * @param firstYear
	 * @param filepath
	 * @param defaultRepeat
	 * @param defaultRemind
	 */
	public InternetCalendar(String name, int firstYear, String filepath, int defaultRepeat, int defaultRemind, boolean defaultTumtum)
	{
		super(name, firstYear, filepath, defaultRepeat, defaultRemind, defaultTumtum);
		load();
	}

	/**
	 * loads saved events from an internet calendar
	 */
	@Override
	protected void load()
	{
		events = new ArrayList<>();
		try
		{
			String internetcalendar = IOUtils.toString(new URL(name).openConnection().getInputStream(), "UTF-8");
			//Nobody likes Windows Newlines anyway
			internetcalendar = internetcalendar.replaceAll("\\r\\n", "\n");
			Matcher eventregex = Pattern.compile("BEGIN:VEVENT.*?SUMMARY:(.+?)(?=\\n\\S).*?DTSTART;[^\\n].*?(\\d\\d\\d\\d)(\\d\\d)(\\d\\d)T{0,1}(\\d{0,2})(\\d{0,2}).*?END:VEVENT", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(internetcalendar);
			while (eventregex.find())
			{
				insert(new Event(eventregex.group(1).replaceAll("\\n\\s", ""), CalDateFormat.dateToMin(eventregex.group(4)+"."+eventregex.group(3)+"."+eventregex.group(2) + " " + ((eventregex.group(5).length() > 0)?eventregex.group(5):"00") + ":" + ((eventregex.group(6).length() > 0)?eventregex.group(6):"00"), firstYear), defaultRepeat, defaultRemind, firstYear, defaultTumtum));
			}
		} catch (Exception e)
		{
			CalendarBot.printMessage(ErrorMessages.eventsNotLoadError(this), false);
		}
	}

	/**
	 * Can't possibly save Events to an Internet calendar
	 * Pretend that it gets saved anyway
	 * @return
	 */
	@Override
	protected boolean save()
	{
		return true;
	}

	/**
	 * Can't possibly delete from an Internet calendar
	 * Pretend that it gets deleted anyway
	 * @return
	 */
	@Override
	public boolean deleteCalendar()
	{
		return true;
	}

	/**
	 * Can't possibly add Events from an Internet calendar
	 * 
	 * @param wat
	 * @param date
	 * @param repeat
	 * @param remind
	 */
	@Override
	public boolean add(String wat, int date, int repeat, int remind)
	{
		return false;
	}

	/**
	 * 
	 * @param wat
	 * @param date
	 * @return
	 */
	public boolean add(String wat, int date)
	{
		return add(wat, date, defaultRepeat, defaultRemind);
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	@Override
	public boolean removeEvent(int index)
	{
		return false;
	}

	@Override
	public boolean removeEvent(String name)
	{
		return false;
	}

	/**
	 * Can't possibly remove Events from an Internet calendar
	 * However method has to return true otherwise calendar cannot be removed
	 */
	public boolean removeAllEvents()
	{
		return true;
	}

	@Override
	/**
	 * Can't possibly remove Events from an Internet calendar
	 * 
	 */
	public boolean removePastEvents()
	{
		return true;
	}
}
