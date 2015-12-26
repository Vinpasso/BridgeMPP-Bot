package bots.CalendarBot;

import java.util.ArrayList;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 
 * @author Bernie
 *
 */
public class Calendar {
	protected String name;
	protected int firstYear;
	protected String filepath;
	protected ArrayList<Event> events;
	protected int DefaultRepeat, DefaultRemind;
	
	/**
	 * 
	 * @param name
	 * @param firstYear
	 * @param filepath
	 */
	public Calendar(String name, int firstYear, String filepath) {
		this(name, firstYear, filepath, -1, -1);
	}
	
	/**
	 * 
	 * @param name
	 * @param firstYear
	 * @param filepath
	 * @param DefaultRepeat
	 * @param DefaultRemind
	 */
	public Calendar(String name, int firstYear, String filepath, int DefaultRepeat, int DefaultRemind) {
		this.name = name;
		this.firstYear = firstYear;
		this.filepath = filepath;
		this.DefaultRemind = DefaultRemind;
		this.DefaultRepeat = DefaultRepeat;
		events = new ArrayList<>();
		File file = new File(filepath + name + ".properties");
		if (file.exists()) {
			load();
		}
	}
	
	/**
	 * loads saved events from properties file
	 */
	protected void load() {
		try {
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(filepath + name + ".properties");
			prop.load(fis);
			fis.close();
			int size = Integer.parseInt(prop.getProperty("size"));
			for (int i = 0; i < size; i++) {
				String[] value = prop.getProperty("" + i).split(" ");
				insert(new Event(value[1], Integer.parseInt(value[0]), Integer.parseInt(value[2]), Integer.parseInt(value[3]), CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear) , firstYear));
			}
		} catch (Exception e) {
			
		}		
	}
	
	/**
	 * save events to properties file
	 * @return
	 */
	protected boolean save() {
		try {
			Properties properties = new Properties();
			for (int i = 0; i < events.size(); i++) {
				properties.setProperty("" + i, events.get(i).toString());
			}
			properties.setProperty("size", "" + events.size());
			FileOutputStream fos = new FileOutputStream(filepath + name + ".properties");
			properties.store(fos, "");
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * deletes properties file
	 * @return
	 */
	public boolean deleteCalendar() {
		try {
			return new File(filepath + name + ".properties").delete();
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @param wat
	 * @param date
	 * @param repeat
	 * @param remind
	 */
	public boolean add (String wat, int date, int repeat, int remind) {
		boolean added = false;
		Event event = new Event(wat, date, repeat, remind, CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear) , firstYear);
		if (!existsEvent(event)) {
			insert(event);
			added = save();
		}
		load();
		return added;
	}
	
	/**
	 * 
	 * @param wat
	 * @param date
	 * @return
	 */
	public boolean add (String wat, int date) {
		return add(wat, date, DefaultRepeat, DefaultRemind);
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected boolean existsEvent (Event event) {
		boolean gibtsscho = false;
		for (int i = 0; i < events.size(); i++) {
			gibtsscho = gibtsscho || events.get(i).getWat().equals(event.getWat());
		}
		return gibtsscho;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public boolean removeEvent (int index) {
		boolean removed = false;
		if (events.size() > index) {
			events.remove(index);
			removed = save();
			load();
		}
		return removed;
	}
	
	public boolean removeEvent (String name) {
		boolean removed = false;
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getWat().equals(name)) {
				events.remove(i);
				removed = save();
				load();
			}
		}
		return removed;
	}
	
	public boolean removeAllEvents () {
		boolean removed = false;
		events = new ArrayList<>();
		removed = save();
		load();
		return removed;
	}
	
	public boolean removePastEvents () {
		boolean removed = false;
		int currentDate = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getNextRepeat() < currentDate) {
				events.remove(i);
				i--;
			}
		}
		removed = save();
		load();
		return removed;
	}
	
	/**
	 * 
	 * @return
	 */
	public Event[] getEvents() {
		return events.toArray(new Event[events.size()]);
	}
	
	
	/**
	 * 
	 * @param currentDate
	 * @return next event after {@code currentDate} or empty array if there is no next date
	 */
	public Event[] getNext (int currentDate) {
		ArrayList<Event> nextEvents = new ArrayList<>();
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getNextRepeat() > currentDate) {
				if (nextEvents.size() > 0) {
					if (nextEvents.get(0).getNextRepeat() == events.get(i).getNextRepeat()) {
						nextEvents.add(events.get(i));
					}
					else if (nextEvents.get(0).getNextRepeat() > events.get(i).getNextRepeat()) {
						nextEvents = new ArrayList<>();
						nextEvents.add(events.get(i));
					}
				}
				else {
					nextEvents.add(events.get(i));
				}
			}
		}
		try {
			return nextEvents.toArray(new Event[nextEvents.size()]);		
		} catch (Exception e) {
			return new Event[] {};
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void insert (Event event) {
		for (int i = 0; i < events.size(); i++) {
			if (event.getDate() < events.get(i).getDate()) {
				events.add(i, event);
				return;
			}			
		}
		events.add(event);
	}
	
	
	public String getName () {
		return name;
	}
	
	@Override
	public String toString() {
		return name + " " + DefaultRepeat + " " + DefaultRemind;
	}
	
	@Override
	public boolean equals(Object object) {
		Calendar calendar = (Calendar) (object);
		return (calendar.getName().toLowerCase().equals(this.name.toLowerCase()));
	}
}
