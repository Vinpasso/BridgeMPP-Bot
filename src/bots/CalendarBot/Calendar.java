package bots.CalendarBot;

import java.util.LinkedList;
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
	protected LinkedList<Event> events;
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
		events = new LinkedList<Event>();
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
			events = new LinkedList<Event>();
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
	public boolean delete() {
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
	 * @param date - format dd.mm.yyyy
	 * @param time - format hh:mm
	 * @param repeat
	 * @param remind
	 * @return
	 */
	public boolean add (String wat, String date, String time, int repeat, int remind) {
		try {
			return add(wat, CalDateFormat.dateToMin(date + " " + time, firstYear), repeat, remind);
		} 
		catch (Exception e) {
			return false;
		}
		
		
	}
	
	/**
	 * 
	 * @param wat
	 * @param date - format: ddmmyyyy
	 * @param time - format: hhmm
	 * @return
	 */
	public boolean add (String wat, String date, String time) {
		return add(wat, date, time, DefaultRepeat, DefaultRemind);
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
	public boolean remove (int index) {
		boolean removed = false;
		if (events.size() > index) {
			events.remove(index);
			removed = save();
			load();
		}
		return removed;
	}
	
	public boolean remove (String name) {
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
	
	public boolean removeAll () {
		boolean removed = false;
		events = new LinkedList<Event>();
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
	 * @return next event after {@code currentDate} or null if there is no next date
	 */
	public Event[] getNext (int currentDate) {
		LinkedList<Event> nextEvents = new LinkedList<Event>();
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).getNextRepeat() > currentDate) {
				if ((nextEvents.size() > 0 && nextEvents.get(0).getNextRepeat() == events.get(i).getNextRepeat()) || nextEvents.size() == 0) {
					nextEvents.add(events.get(i));
				}
			}
		}
		try {
			return nextEvents.toArray(new Event[nextEvents.size()]);		
		} catch (Exception e) {
			return null;
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
