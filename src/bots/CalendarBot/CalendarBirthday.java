package bots.CalendarBot;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Birthday Calendar
 * @author Bernie
 *
 */
public class CalendarBirthday extends Calendar{

	public CalendarBirthday(int firstYear, String filepath) {
		super("birthday", firstYear, filepath, 360, 1440);
	}
	
	public CalendarBirthday () {
		super("birthday", 0, null, 0, 0);
	}
	
	@Override
	public boolean add (String wat, int date, int repeat, int remind) {
		boolean added = false;
		EventBirthday event = new EventBirthday(wat, date + 720, DefaultRepeat, DefaultRemind, CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear) , firstYear);
		if (!existsEvent(event)) {
			insert(event);
			added = save();
		}
		load();
		return added;
	}
	
	@Override
	public boolean add (String wat, String date, String time, int repeat, int remind) {
		boolean added = false;
		try {
			added = add(wat, CalDateFormat.dateToMin(date + " 12:00", firstYear), DefaultRepeat, DefaultRemind);
		} 
		catch (Exception e) {
			return false;
		}
		
		return added;
	}
	
	@Override
	public boolean add (String wat, String date, String time) {
		return add(wat, date, time, DefaultRepeat, DefaultRemind);
	}
	
	@Override
	public boolean add (String name, int date) {
		return add(name, date, DefaultRepeat, DefaultRemind);
	}
	
	@Override
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
				insert(new EventBirthday(value[1], Integer.parseInt(value[0]), Integer.parseInt(value[2]), Integer.parseInt(value[3]), CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear) , firstYear));
			}
		} catch (Exception e) {
			
		}		
	}
}
