package bots.CalendarBot;


import java.io.IOException;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * 
 * @author Bernie
 *
 */
public class RunCommand {
	private String[] command;
	private LinkedList<Calendar> calendars;
	private int firstYear;
	private String filepath;
	static boolean isCmdDeleteAll = false;
	
	public RunCommand (LinkedList<Calendar> calendars, int firstYear, String filepath) {
		this.calendars = calendars;
		this.firstYear = firstYear;
		this.filepath = filepath;
	}
	
	public void runNewCommand (String cmd) {
		this.command = cmd.split(" ");
		if (command.length > 0) {
			command[0] = command[0].toLowerCase();
		}
		
		//help
		if (CommandSyntax.equalsHelp(command[0])) {
			if (!isParamHelp(0)) {
				printMessage(CommandSyntax.commandsWithParamToString());
			}
		}
		//printDate
		else if (command[0].equals(CommandSyntax.getCommands()[1])) {
			if (!isParamHelp(1)) {
				printMessage(CurrentDate.getDateWTime());
			}
		}
		//printTime
		else if (command[0].equals(CommandSyntax.getCommands()[2])) {
			if (!isParamHelp(2)) {
				printMessage(CurrentDate.getTime());
			}
		}
		//alertson
		else if (command[0].equals(CommandSyntax.getCommands()[3])) {
			if (!isParamHelp(3)) {
				CalendarBot.alertson();
			}
		}
		//alertsoff
		else if (command[0].equals(CommandSyntax.getCommands()[4])) {
			if (!isParamHelp(4)) {
				CalendarBot.alertsoff();
			}
		}
		//createCal
		else if (command[0].equals(CommandSyntax.getCommands()[5])) {
			if (!isParamHelp(5)) {
				cmdCalcr();
			}
		}
		//ListCal
		else if (command[0].equals(CommandSyntax.getCommands()[6])) {
			if (!isParamHelp(6)) {
				cmdCalls();
			}
		}
		//delCal
		else if (command[0].equals(CommandSyntax.getCommands()[7])) {			
			if (!isParamHelp(7)) {
				cmdCaldel();
			}
		}
		//createEvent
		else if (command[0].equals(CommandSyntax.getCommands()[8])) {			
			if (!isParamHelp(8)) {
				cmdEventcr();			
			}
		}
		//listEvents
		else if (command[0].equals(CommandSyntax.getCommands()[9])) {
			if (!isParamHelp(9)) {
				cmdEventls();
			}
		}
		//delEvent
		else if (command[0].equals(CommandSyntax.getCommands()[10])) {
			if (!isParamHelp(10)) {
				cmdEventdel();
			}
		}
		//autoDel
		else if (command[0].equals(CommandSyntax.getCommands()[11])) {
			if (!isParamHelp(11)) {
				cmdAutoDel();
			}
		}
		//autoDelOn
		else if (command[0].equals(CommandSyntax.getCommands()[12])) {
			if (!isParamHelp(12)) {
				CalendarBot.eventsPastAutoDelOn = true;
			}
		}
		//autoDelOff
		else if (command[0].equals(CommandSyntax.getCommands()[13])) {
			if (!isParamHelp(13)) {
				CalendarBot.eventsPastAutoDelOn = false;
			}
		}
		//lunarPhase
		else if (command[0].equals(CommandSyntax.getCommands()[14])) {
			if (!isParamHelp(14)) {
				cmdLunar();
			}
		}
		//deleteAllCal
		else if (command[0].equals(CommandSyntax.getCommands()[15])) {
			if (!isParamHelp(15)) {
				isCmdDeleteAll = true;
				printMessage("Are you sure to delete all calenders and events?");
			}
		}
		//deleteAllYes
		else if (command[0].equals("yes") && isCmdDeleteAll) {
			cmdDeleteAllYes();
		}
		//version
		else if (command[0].equals("version")) {
			printMessage(CalendarBot.version);
		}
		//reset
		else if (command[0].equals("reset")) {
					
		}
		//no
		else if (command[0].equals("no")) {
			
		}
		//unknown command
		else {
			printMessage("Unknown Command " + command[0] + "\nType " + CommandSyntax.getCommands()[0] + " for more information");
		}
	}
	


	/**
	 * 
	 */
	private void cmdCalcr () {
		int remind;
		int repeat;
		try {
			repeat = convertRepeat(command[2]);
			remind = convertRemind(command[3]);
			if (!existsCalendar(new Calendar(command[1], firstYear, filepath, repeat, remind))) {
				calendars.add(new Calendar(command[1], firstYear, filepath, repeat, remind));
				printMessage("Created Calendar " + command[1]);
			} 
			else {
				printMessage("This calendar exists already");
			}
		} catch (Exception e) {
			errorSyntax(5);
		}
	}
	
	/**
	 * 
	 */
	private void cmdCalls () {
		String calall = "";
		for (int i = 0; i < calendars.size(); i++) {
			calall = calall + "\n" + calendars.get(i).toString();
		}
		printMessage(calall);
	}
	
	/**
	 * 
	 */
	private void cmdCaldel () {
		boolean deleted = false;
		try {
			for (int i = 0; i < calendars.size(); i++) {
				if (calendars.get(i).equals(new Calendar(command[1], 0, ""))) {
					String name = calendars.get(i).getName();
					if(!calendars.get(i).removeAll()) throw new IOException();
					if(!calendars.get(i).delete()) throw new IOException();
					calendars.remove(i);
					deleted = true;
					printMessage("Deleted Calendar " + name);
					break;
				}
			}
			if (!deleted) errorCalNotFound();
		} catch (IOException ie) {
			errorFailed();
		}
		catch (Exception e) {
			errorSyntax(7);
		}
	}
	
	/**
	 * 
	 */
	private void cmdEventcr () {
		boolean create = true;
		boolean created = false;
		try {
			//check if calendar exists
			Calendar cal = getCalendarByName(command[1]);
			if (cal == null) {
				errorCalNotFound();
				create = false;
			}					
			//check if date is correct
			int date;
			try {
				date = Integer.parseInt(command[3]);
			} 
			catch (NumberFormatException e) {
				if (command[3].length() >= 12) {
					command[3] = command[3].substring(0, 10) + " " + command[3].substring(11, 16);
				}
				else {
					command[3] = command[3].substring(0, 10) + " 00:00";
				}
				date = CalDateFormat.dateToMin(command[3], firstYear);
			}
			//check for repeat remind
			if (command.length >= 5 && create) {
				int repeat = convertRepeat(command[4]);
				int remind = convertRemind(command[5]);
				if (repeat == -1 || remind == -1) 
					throw new IllegalArgumentException();
				created = cal.add(command[2], date, repeat, remind);				
			}
			else if (create) {
				created = cal.add(command[2], date);
			}
			//print status message
			if (created) {
				printMessage("Created Event " + command[2]);
			}
			else { 
				errorFailed("Possibly the event exists already");
			}
		} 
		catch (Exception e) {
			errorSyntax(8);
		}
	}
	
	/**
	 * 
	 */
	private void cmdEventls () {
		boolean existsCal = true;
		try {			
			Calendar cal = getCalendarByName(command[1]);
			if (command.length >= 3) {
				//check if calendar exists				
				if (cal == null) {
					existsCal = false;				
				}
			}
			else {
				cal = null;
			}			
			String msg = "";
			
			// no cal + all
			if (cal == null && command[1].equals("all")) {				
				for (int i = 0; i < calendars.size(); i++) {
					msg = msg + "Calendar \"" + calendars.get(i).getName() + "\":\n";
					for (int j = 0; j < calendars.get(i).getEvents().length; j++) {
						msg = msg + j + ": " + calendars.get(i).getEvents()[j].toStringList(firstYear) + "\n";
					}
				}
			}
			//cal + all
			else if (cal != null && command[2].equals("all")) {
				msg = msg + "Calendar \"" + cal.getName() + "\":\n";
				for (int j = 0; j < cal.getEvents().length; j++) {
					msg = msg + j + ": " + cal.getEvents()[j].toStringList(firstYear) + "\n";
				}
			}
			
			//no cal + next
			else if (cal == null && command[1].equals("next")) {
				int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
				Event[] events = null;
				for (int i = 0; i < calendars.size(); i++) {
					if (calendars.get(i).getNext(date) != null && calendars.get(i).getNext(date).length > 0) {
						Event[] tmpEvents = calendars.get(i).getNext(date);
						if (events == null) {
							events = tmpEvents;
						}					
						else if (events[0].getNextRepeat() == tmpEvents[0].getNextRepeat()) {
							int length = events.length;
							events = Arrays.copyOf(events, events.length + tmpEvents.length);
							System.arraycopy(tmpEvents, 0, events, length, tmpEvents.length);
						}
						else if (events[0].getNextRepeat() > tmpEvents[0].getNextRepeat()) {
							events = tmpEvents;
						}
					}
				}
				for (int i = 0; i < events.length; i++) {
					msg = msg + events[i].toStringList(firstYear) + "\n";					
				}
			}
			
			//cal + next
			else if (cal != null && command[2].equals("next")) {
				int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
				Event[] events = cal.getNext(date);
				for (int i = 0; i < events.length; i++) {
					msg = msg + events[i].toStringList(firstYear) + "\n";					
				}
			}
			
			//date
			else {
				int indexDate = cal == null ? 1 : 2;
				int start, end;
				if (command[indexDate].length() == 10) {
					command[indexDate] = command[indexDate] + " 00:00";
					start = CalDateFormat.dateToMin(command[indexDate], firstYear);
					end = start + 1440;
				}
				else if (command[indexDate].length() == 7) {
					int tmpYear = Integer.parseInt(command[indexDate].substring(3, 7));
					int tmpMonth = Integer.parseInt(command[indexDate].substring(0, 2));
					start = CalDateFormat.dateToMin("01." + tmpMonth + "." + tmpYear + " 00:00", firstYear);
					tmpMonth++;
					if (tmpMonth == 13) {
						tmpMonth = 1;
						tmpYear++;
					}
					end = CalDateFormat.dateToMin("01." + tmpMonth + "." + tmpYear + " 00:00", firstYear);
				}
				else if (command[indexDate].length() == 4) {
					int tmpYear = Integer.parseInt(command[indexDate]);
					start = CalDateFormat.dateToMin("01.01." + (tmpYear) + " 00:00", firstYear);
					end = CalDateFormat.dateToMin("01.01." + (tmpYear + 1) + " 00:00", firstYear);
				}
				else {
					throw new IllegalArgumentException();
				}
				
				//no cal
				if (cal == null) {
					for (int i = 0; i < calendars.size(); i++) {
						for (int j = 0; j < calendars.get(i).getEvents().length; j++) {
							if (calendars.get(i).getEvents()[j].getDate() >= start && calendars.get(i).getEvents()[j].getDate() < end) {
								msg = msg + calendars.get(i).getEvents()[j].toStringList(firstYear) + "\n";
							}
						}
					}
				}
				
				//cal
				else {
					for (int j = 0; j < cal.getEvents().length; j++) {
						if (cal.getEvents()[j].getDate() >= start && cal.getEvents()[j].getDate() < end) {
							msg = msg + cal.getEvents()[j].toStringList(firstYear) + "\n";
						}
					}
				}
			}
			printMessage(msg);
		} 
		//no event found || calendar not found
		catch (NullPointerException ne) {			
			if (!existsCal) errorCalNotFound();
		} 
		//to less parameters given
		catch (ArrayIndexOutOfBoundsException ae) {
			errorSyntax(9);
		}
		//wrong parameters given 
		catch (IllegalArgumentException ie) {
			if (!existsCal) errorCalNotFound();
			else errorSyntax(9);
		}
		//else
		catch (Exception e) {
			errorUnkown();
		}
	}
	
	/**
	 * 
	 */
	private void cmdEventdel() {
		try {
			Calendar cal = getCalendarByName(command[1]);
			boolean deleted = false;
			int number;
			
			try {
				number = Integer.parseInt(command[2]);
			} catch (Exception e) {
				number = -1;
			}
			
			if (number >= 0) {
				deleted = cal.remove(number);
			}
			else {
				deleted = cal.remove(command[2]);
			}
			//print status msg
			if (deleted) {
				printMessage("Deleted Event successfully");
			}
			else {
				errorFailed();
			}
				
		}
		// calendar not found
		catch (NullPointerException ne) {
			errorCalNotFound();
		}
		catch (Exception e) {
			errorSyntax(10);
		}		
	}
	
	public void cmdAutoDel() {
		for (int i = 0; i < calendars.size(); i++) {
			calendars.get(i).removePastEvents();
		}
	}
	
	private void cmdLunar() {
		int lang = 0;
		try {
			if (command[1].toLowerCase().equals("ger")) {
				lang = 1;
			}
		} catch (Exception e) {
			
		}
		printMessage(new LunarPhase(firstYear, lang).toString());
		printMessage("show me " + (new LunarPhase(firstYear).getLunarPhase()));
	}
	
	/**
	 * 
	 */
	private void cmdDeleteAllYes() {
		boolean removed = true;
		for (int i = 0; i < calendars.size(); i++) {
			removed = calendars.get(i).removeAll() && removed;
			removed = calendars.get(i).delete() && removed;
		}
		
		while (calendars.size() > 0) {
			calendars.remove(0);
		}
		
		if (removed) {
			printMessage("Deleted all calendars");
		}
		else {
			errorFailed();
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	private boolean isParamHelp (int index) {
		try {
			if (CommandSyntax.equalsHelp(command[1])) {
				printMessage(CommandSyntax.commandWithParamHelpToString(index));
				return true;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	/**
	 * prints SyntaxError
	 * @param indexCmd
	 */
	private void errorSyntax (int indexCmd) {
		String msg = "Syntax Error!\nType \"" + CommandSyntax.getCommands()[indexCmd] + " " + CommandSyntax.getCommands()[0] + "\" for more information";
		printMessage(msg);
	}
	
	private void errorCalNotFound () {
		printMessage("Calendar not found!");
	}
	
	private void errorUnkown () {
		printMessage("Unknown Error!");
	}
	
	private void errorFailed (String msg) {
		printMessage("Error: Operation failed!\n" + msg);
	}
	
	private void errorFailed () {
		printMessage("Error: Operation failed!");
	}
	
	/**
	 * 
	 * @param msg
	 */
	private void printMessage (String msg) {
		CalendarBot.printMessage(msg);
	}
	
	/**
	 * Checks if {@code calendar} exists
	 * @param calendar
	 * @return
	 */
	private boolean existsCalendar (Calendar calendar) {
		boolean equals = false;
		for (int i = 0; i < calendars.size(); i++) {
			equals = calendars.get(i).equals(calendar) || equals;			
		}
		return equals;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private Calendar getCalendarByName (String name) {
		for (int i = 0; i < calendars.size(); i++) {
			if (calendars.get(i).getName().toLowerCase().equals(name.toLowerCase())) {
				return calendars.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param repRem
	 * @return number of days or -1 if wrong syntax
	 */
	private int convertRepeat (String repeat) {
		try {
			if (repeat.equals("0"))
				return 0;
			int number = Integer.parseInt(repeat.substring(0, repeat.length() - 1));
			switch (repeat.charAt(repeat.length() - 1)) {
			case 'd':
				return number;
			case 'w':
				return number * 7;
			case 'm':
				return number * 30;
			case 'y':
				return number * 360;
			default:
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 
	 * @param remind
	 * @return
	 */
	private int convertRemind (String remind) {
		try {
			if (remind.equals("0"))
				return 0;
			int number = Integer.parseInt(remind.substring(0, remind.length() - 1));
			switch (remind.charAt(remind.length() - 1)) {
			case 'n':
				return number;
			case 'h':
				return number * 60;
			case 'd':
				return number * 1440;
			case 'w':
				return number * 10080;
			case 'm':
				return number * 43200;
			case 'y':
				return number * 525600;
			default:
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
