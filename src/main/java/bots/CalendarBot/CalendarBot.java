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
	public final static String VERSION = "2.1.2";
	private static CalendarBot instance;
	public static boolean eventsPastAutoDelOn = false;
	private static boolean alertson = true;
	private final int firstYear = 1970;
	private final String filepath = "calendarbot/";
	private final Commands commands = Commands.getInstance();
	private RunCommand runCmd;
	public static Reminder reminder;
	private ArrayList<Calendar> calendars;
	
	@Override
	public void initializeBot () {
		instance = this;
		if (!new File(filepath).exists()) {
			new File(filepath).mkdir();
		}
		
		calendars = new ArrayList<>();
		File file = new File(filepath + "calendarbot" + ".properties");
		if (file.exists()) {
			if (!loadCalendars()) {
				printMessage(ErrorMessages.calNotLoadError(), false);
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
	
	/**
	 * 
	 * @param msg
	 * @param tumtum -true: Message.group = tumtum, -false: Message.group = tumspam
	 */
	public static void printMessage (String msg, boolean tumtum) {
		instance.sendMessage(new Message(tumtum ? "tumtum" : "tumspam", msg, MessageFormat.PLAIN_TEXT));
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
				//downward compatible (adds false to String)
				String[] value = (prop.getProperty("" + i) + " false").split(" ");
				if (value[0].equals("birthday")) continue;
				if (value[0].startsWith("http://"))
				{
					calendars.add(new InternetCalendar(value[0], firstYear, filepath, Integer.parseInt(value[1]), Integer.parseInt(value[2]), Boolean.parseBoolean(value[3])));
					continue;
				}
				calendars.add(new Calendar(value[0], firstYear, filepath, Integer.parseInt(value[1]), Integer.parseInt(value[2]), Boolean.parseBoolean(value[3])));
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
				printMessage(ErrorMessages.calNotLoadError(), false);
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
	
	public static boolean getAlertson () {
		return alertson;
	}

	
	
}
