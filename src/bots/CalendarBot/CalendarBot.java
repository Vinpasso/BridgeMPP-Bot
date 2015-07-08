package bots.CalendarBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
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
	static String version = "1.0.7";
	LinkedList<Calendar> calendars;
	RunCommand runCmd;
	Reminder reminder;
	int firstYear;
	String filepath;
	static boolean eventsPastAutoDelOn;
	private static boolean alertson;
	private static String messageGroup;
	String command;
	
	private static CalendarBot instance;
	
	@Override
	public void initializeBot () {
		instance = this;
		command = null;
		filepath = "../calendarbot/";
		if (!new File(filepath).exists()) {
			new File(filepath).mkdir();
		}
		eventsPastAutoDelOn = false;
		firstYear = 1970;
		
		calendars = new LinkedList<Calendar>();
		File file = new File(filepath + "calendarbot" + ".properties");
		if (file.exists()) {
			if (!loadCalendars()) {
				printMessage("Error: Could not load Calendars\nType \"reset\" to try again");
			}
		}
		calendars.add(new CalendarBirthday(firstYear, filepath));
		
		runCmd = new RunCommand(calendars, firstYear, filepath);
		reminder = new Reminder(calendars.toArray(new Calendar[calendars.size()]), firstYear, alertson);
		reminder.start();
	}
	
	@Override
	public void messageReceived(Message message) {
		//set command
		command = message.getPlainTextMessage();
		
		if (command.toLowerCase().startsWith(CommandSyntax.getPrefix())) {			
			command = command.substring(CommandSyntax.getPrefix().length());
			
			//set messageGroup
			messageGroup = message.getGroup();
					
			//set isCmdDeleteAll
			if (!command.toLowerCase().split(" ")[0].equals("yes")) {
				RunCommand.isCmdDeleteAll = false;
			}
			
			//send message
			runCmd.runNewCommand(command);
			
			//autoDelOld
			if (eventsPastAutoDelOn) {
				runCmd.cmdAutoDel();
			}
			//
			reset();
		}
	}
	
	public static void printMessage (String msg) {
		instance.sendMessage(new Message(messageGroup, msg, MessageFormat.PLAIN_TEXT));
	}
	
	/**
	 * loads saved calendars from property file
	 */
	private boolean loadCalendars() {
		try {			
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(filepath + "calendarbot" + ".properties");
			prop.load(fis);
			eventsPastAutoDelOn = Boolean.parseBoolean(prop.getProperty("eventsPastAutoDelOn"));
			alertson = Boolean.parseBoolean(prop.getProperty("alertson"));
			messageGroup = prop.getProperty("messageGroup");
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
			properties.setProperty("messageGroup", "" + messageGroup);
			properties.setProperty("eventsPastAutoDelOn", "" + eventsPastAutoDelOn);
			properties.setProperty("alertson", "" + alertson);
			properties.setProperty("size", "" + calendars.size());
			properties.store(fos, "");
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void reset () {
		if (!saveCalendars()) {
			if (!saveCalendars()) {
				printMessage("Error: Could not save calendars! Changes may be gone");
			}
		}
		reminder.interrupt();
		initializeBot();
	}
	
	public static void alertson() {
		alertson = true;
	}
	
	public static void alertsoff() {
		alertson = false;
	}

	
	
}
