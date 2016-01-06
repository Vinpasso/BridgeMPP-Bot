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
	protected void load()
	{
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

		}
	}

	/**
	 * Can't possibly save Events to an Internet calendar
	 * Pretend that it gets saved anyway
	 * @return
	 */
	protected boolean save()
	{
		return true;
	}

	/**
	 * Can't possibly delete from an Internet calendar
	 * Pretend that it gets deleted anyway
	 * @return
	 */
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
	 * @param event
	 * @return
	 */
	protected boolean existsEvent(Event event)
	{
		boolean gibtsscho = false;
		for (int i = 0; i < events.size(); i++)
		{
			gibtsscho = gibtsscho || events.get(i).getWat().equals(event.getWat());
		}
		return gibtsscho;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public boolean removeEvent(int index)
	{
		return false;
	}

	public boolean removeEvent(String name)
	{
		return false;
	}

	public boolean removeAllEvents()
	{
		return false;
	}

	public boolean removePastEvents()
	{
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public Event[] getEvents()
	{
		return events.toArray(new Event[events.size()]);
	}

	/**
	 * 
	 * @param currentDate
	 * @return next event after {@code currentDate} or empty array if there is
	 *         no next date
	 */
	public Event[] getNext(int currentDate)
	{
		ArrayList<Event> nextEvents = new ArrayList<>();
		for (int i = 0; i < events.size(); i++)
		{
			if (events.get(i).getNextRepeat() > currentDate)
			{
				if (nextEvents.size() > 0)
				{
					if (nextEvents.get(0).getNextRepeat() == events.get(i).getNextRepeat())
					{
						nextEvents.add(events.get(i));
					} else if (nextEvents.get(0).getNextRepeat() > events.get(i).getNextRepeat())
					{
						nextEvents = new ArrayList<>();
						nextEvents.add(events.get(i));
					}
				} else
				{
					nextEvents.add(events.get(i));
				}
			}
		}
		try
		{
			return nextEvents.toArray(new Event[nextEvents.size()]);
		} catch (Exception e)
		{
			return new Event[] {};
		}
	}

	/**
	 * 
	 * @param event
	 */
	protected void insert(Event event)
	{
		for (int i = 0; i < events.size(); i++)
		{
			if (event.getDate() < events.get(i).getDate())
			{
				events.add(i, event);
				return;
			}
		}
		events.add(event);
	}

	public String getName()
	{
		return name;
	}

	public void setDefaultTumtum(boolean defaultTumtum)
	{
		this.defaultTumtum = defaultTumtum;
	}

	public void setDefaultRepeat(int defaultRepeat)
	{
		this.defaultRepeat = defaultRepeat;
	}

	public void setDefaultRemind(int defaultRemind)
	{
		this.defaultRemind = defaultRemind;
	}

	@Override
	public String toString()
	{
		return name + " " + defaultRepeat + " " + defaultRemind + " " + defaultTumtum;
	}

	public String toStringList()
	{
		return name + ":\t repeat: " + Event.repeatToString(defaultRepeat) + ", remind: " + Event.remindToString(defaultRemind) + ", tumtum-Chat: " + (defaultTumtum ? "on" : "off");
	}

	@Override
	public boolean equals(Object object)
	{
		Calendar calendar = (Calendar) (object);
		return (calendar.getName().toLowerCase().equals(this.name.toLowerCase()));
	}
}
