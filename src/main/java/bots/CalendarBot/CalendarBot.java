package bots.CalendarBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

/**
 * 
 * @author Bernie
 *
 */
public class CalendarBot extends Bot {
	public final static String VERSION = "1.1.2";
	private static CalendarBot INSTANCE;
	public static boolean eventsPastAutoDelOn = false;
	private static boolean alertson = true;
	private final int firstYear = 1970;
	private final String filepath = "calendarbot/";
	private final Commands commands = Commands.getInstance();
	private RunCommand runCmd;
	private Reminder reminder;
	private ArrayList<Calendar> calendars;
	
	@Override
	public void initializeBot () {
		INSTANCE = this;
		if (!new File(filepath).exists()) {
			new File(filepath).mkdir();
		}
		
		calendars = new ArrayList<Calendar>();
		File file = new File(filepath + "calendarbot" + ".properties");
		if (file.exists()) {
			if (!loadCalendars()) {
				printMessageTumSpam("Error: Could not load Calendars\nType \"" + commands.getPrefix() + commands.getCommand(100) + "\" to try again");
			}
		}
		calendars.add(new CalendarBirthday(firstYear, filepath));
		
		runCmd = new RunCommand(calendars, firstYear, filepath);
		reminder = new Reminder(calendars.toArray(new Calendar[calendars.size()]), firstYear, alertson);
		reminder.scheduleNextReminder();
	}
	
	@Override
	public void messageReceived(Message message) {
		//set command
		String command = message.getPlainTextMessage();
		
		if (command.toLowerCase().startsWith(commands.getPrefix())) {			
			command = command.substring(commands.getPrefix().length());
			
			//send message
			runCmd.runNewCommand(command);
			
			//autoDelOld
			if (eventsPastAutoDelOn) {
				runCmd.cmdAutoDel();
			}
			//save and load changes
			reset();
		}
	}
	
	public static void printMessageTumSpam (String msg) {
		INSTANCE.sendMessage(new Message("tumspam", msg, MessageFormat.PLAIN_TEXT));
	}
	
	/**
	 * loads saved calendars from property file
	 */
	private boolean loadCalendars() {
		try {			
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(filepath + "calendarbot" + ".properties");
			prop.load(fis);
			fis.close();
			eventsPastAutoDelOn = Boolean.parseBoolean(prop.getProperty("eventsPastAutoDelOn"));
			alertson = Boolean.parseBoolean(prop.getProperty("alertson"));
			int size = Integer.parseInt(prop.getProperty("size"));
			for (int i = 0; i < size; i++) {
				String[] value = prop.getProperty("" + i).split(" ");
				if (value[0].equals("birthday")) continue;
				calendars.add(new Calendar(value[0], firstYear, filepath, Integer.parseInt(value[1]), Integer.parseInt(value[2])));
			}
			return true;
		} catch (Exception e) {
			return false;
		}		
	}
	
	/**
	 * save calendars to property file
	 * @return
	 */
	private boolean saveCalendars() {
		try {
			Properties properties = new Properties();
			FileOutputStream fos = new FileOutputStream(new File(filepath + "calendarbot" + ".properties"));
			for (int i = 0; i < calendars.size(); i++) {
				properties.setProperty("" + i, calendars.get(i).toString());
			}
			properties.setProperty("eventsPastAutoDelOn", "" + eventsPastAutoDelOn);
			properties.setProperty("alertson", "" + alertson);
			properties.setProperty("size", "" + calendars.size());
			properties.store(fos, "");
			fos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void reset () {
		if (!saveCalendars()) {
			if (!saveCalendars()) {
				printMessageTumSpam("Error: Could not save calendars! Changes may be gone");
			}
		}
//		reminder.interrupt();
		initializeBot();
	}
	
	public static void alertson() {
		alertson = true;
	}
	
	public static void alertsoff() {
		alertson = false;
	}

	
	
}
