package bots.CalendarBot;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Birthday Calendar
 * @author Bernie
 *
 */
public class CalendarBirthday extends Calendar {
	public CalendarBirthday(int firstYear, String filepath) {
		super("Birthdays", firstYear, filepath, 360, 10080, true);
	}
	
	public CalendarBirthday () {
		super("Birthdays", 0, null, 0, 0, false);
	}
	
	@Override
	public boolean add (String wat, int date, int repeat, int remind) {
		boolean added = false;
		EventBirthday event = new EventBirthday(wat, date + 480, defaultRepeat, defaultRemind, firstYear, defaultTumtum);
		added = insert(event);
		added = added && save();
		load();
		return added;
	}
	
	
	@Override
	public boolean add (String name, int date) {
		return add(name, date, defaultRepeat, defaultRemind);
	}
	
	@Override
	protected void load() {
		String nameLoad = name;
		if (!new File(filepath + nameLoad + ".properties").exists()) nameLoad = "birthday";
		try {
			events = new ArrayList<>();
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(filepath + nameLoad + ".properties");
			prop.load(fis);
			fis.close();
			int size = Integer.parseInt(prop.getProperty("size"));
			for (int i = 0; i < size; i++) {
				String[] value = prop.getProperty("" + i).split(" ");
				insert(new EventBirthday(value[1], Integer.parseInt(value[0]), Integer.parseInt(value[2]), Integer.parseInt(value[3]), firstYear, defaultTumtum));
			}
			loaded = true;
		} catch (Exception e) {
			loaded = false;
			CalendarBot.printMessage(ErrorMessages.eventsNotLoadError(this), false);
		}		
	}
}
