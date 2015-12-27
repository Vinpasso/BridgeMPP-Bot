package bots.CalendarBot;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Bernie
 *
 */
public class RunCommand {
	private Commands commands = Commands.getInstance();
	private ArrayList<Calendar> calendars;
	private final int firstYear;
	private final String filepath;
	private boolean isCmdDeleteAll = false;
	
	public RunCommand (ArrayList<Calendar> calendars, int firstYear, String filepath) {
		this.calendars = calendars;
		this.firstYear = firstYear;
		this.filepath = filepath;
	}
	
	public void runNewCommand (String command) {
		String[] cmd = command.split(" ");
		if (cmd.length > 0) {
			cmd[0] = cmd[0].toLowerCase();
		}		
		int indexCommand = commands.getIndexForCommand(cmd[0]);
		
		// test if not "yes"
		if (indexCommand != 102) {
			isCmdDeleteAll = false;
		}
		// tests if parameter is help
		if (cmd.length > 1 && commands.getCommand(0).isCommand(cmd[1].toLowerCase())) {
			if (indexCommand >= 0) {
				printMessage(commands.commandToString(indexCommand));
			}
		}
		else {
			switch (indexCommand) {
			case 0:
				// help
				printMessage(commands.commandsToString()
				+ "Type \"" + commands.getPrefix() + "%command% " + commands.getCommand(0).getCommand() + "\" for more information");
				break;
			case 1:
				//printDate
				printMessage(CurrentDate.getDateWTime());
				break;
			case 2:
				//printTime
				printMessage(CurrentDate.getTime());
				break;
			case 3:
				//alertson
				CalendarBot.alertson();
				break;
			case 4:
				//alertsoff
				CalendarBot.alertsoff();
				break;
			case 5:
				//createCal
				cmdCalcr(cmd);
				break;
			case 6:
				//listCal
				cmdCalls();
				break;
			case 7:
				//delCal
				cmdCaldel(cmd);
				break;
			case 8:
				//createEvent
				cmdEventcr(cmd);
				break;
			case 9:
				//listEvents
				cmdEventls(cmd);
				break;
			case 10:
				//delEvent
				cmdEventdel(cmd);
				break;
			case 11:
				//autodel
				cmdAutoDel();
				break;
			case 12:
				//autoDelOn
				CalendarBot.eventsPastAutoDelOn = true;
				break;
			case 13:
				//autoDelOff
				CalendarBot.eventsPastAutoDelOn = false;
				break;
			case 14:
				//lunar
				cmdLunar(cmd);
				break;
			case 15:
				//deleteAllCalendars (-param = no)
				isCmdDeleteAll = true;
				printMessage("Are you sure to delete all calenders and events?");
				break;
			case 16:
				//version
				printMessage("CalendarBot Version " + CalendarBot.VERSION);
				break;
			case 17:
				//caledit
				cmdCalEdit(cmd);
				break;
			case 18:
				//alerts
				printMessage("Alerts are " + (CalendarBot.getAlertson() ? "on" : "off"));
				break;
			case 100:
				//reset
			case 101:
				//no
				break;
			case 102:
				//yes
				if (isCmdDeleteAll) cmdDeleteAllYes();
				break;
				default:
					//unknown command
					printMessage("Unknown Command " + cmd[0] + "\nType \"" + commands.getPrefix() + commands.getCommand(0).getCommand() + "\" for more information");
					break;
			
			}
		}		
	}
	


	/**
	 * 
	 */
	private void cmdCalcr (String[] command) {
		try {
			int repeat = convertRepeat(command[2]);
			int remind = convertRemind(command[3]);
			boolean tumtum = (command[4].charAt(0) == 49) ? true : false; 
			Calendar newCalendar = new Calendar(command[1], firstYear, filepath, repeat, remind, tumtum);
			if (!existsCalendar(newCalendar)) {
				calendars.add(newCalendar);
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
			calall = calall + "\n" + calendars.get(i).toStringList();
		}
		printMessage(calall);
	}
	
	/**
	 * 
	 */
	private void cmdCaldel (String[] command) {
		try {
			for (int i = 0; i < calendars.size(); i++) {
				if (calendars.get(i).getName().toLowerCase().equals(command[1].toLowerCase())) {
					command[1] = calendars.get(i).getName();
					if(!calendars.get(i).removeAllEvents()) throw new IOException();
					if(!calendars.get(i).deleteCalendar()) throw new IOException();
					calendars.remove(i);
					printMessage("Deleted Calendar " + command[1]);
					return;
				}
			}
			errorCalNotFound();
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
	private void cmdCalEdit (String[] command) {
		//check for parameters
		if (command.length < 3) {
			errorSyntax(17);
			return;
		}		
		//check whether the calendar exists
		Calendar cal = getCalendarByName(command[1]);
		if (cal == null) {
			errorCalNotFound();
			return;
		}		
		//set TumTum
		cal.setDefaultTumtum((command[2].charAt(0) == 49) ? true : false);		
		//set repeat
		if (command.length >= 4) {
			int repeat = convertRepeat(command[3]);
			if (repeat == -1) {
				errorSyntax(17);
				return;
			}
			cal.setDefaultRepeat(repeat);
		}
		//set remind
		if (command.length >= 5) {
			int remind = convertRemind(command[4]);
			if (remind == -1) {
				errorSyntax(17);
				return;
			}
			cal.setDefaultRemind(remind);
		}
		//print status
		printMessage("Changed Calendar \"" + cal + "\" into: \n" + cal.toStringList());
	}
	
	/**
	 * 
	 */
	private void cmdEventcr (String[] command) {
		boolean created = false;
		try {
			//check if calendar exists
			Calendar cal = getCalendarByName(command[1]);
			if (cal == null) {
				errorCalNotFound();
				return;
			}					
			//check if date is correct
			int date;
			try {
				//date in min since firstYear
				date = Integer.parseInt(command[3]);
			} 
			catch (NumberFormatException e) {
				//dateFormat: dd.mm.yyyy
				if (command[3].length() == 10) {
					command[3] = command[3].substring(0, 10) + " 00:00";
				}
				//dateFormat: dd.mm.yyyy-hh:mm
				else {
					command[3] = command[3].substring(0, 10) + " " + command[3].substring(11, 16);
				}
				//throws IllegalArgumentException for wrong dateFormat/not existing date
				date = CalDateFormat.dateToMin(command[3], firstYear);
			}
			//check for repeat remind
			if (command.length >= 5) {
				int repeat = convertRepeat(command[4]);
				//throws IndexOutOfBoundsException if command.length = 5 < 6
				int remind = convertRemind(command[5]);
				if (repeat == -1 || remind == -1) 
					throw new IllegalArgumentException();
				created = cal.add(command[2], date, repeat, remind);			
			}
			else {
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
	private void cmdEventls (String[] command) {
		try {			
			Calendar cal = getCalendarByName(command[1]);
			if (command.length >= 3) {
				//check if calendar exists				
				if (cal == null) {
					errorCalNotFound();
					return;				
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
						msg = msg + j + ": " + calendars.get(i).getEvents()[j].toStringList(false) + "\n";
					}
				}
			}
			//cal + all
			else if (cal != null && command[2].equals("all")) {
				msg = "Calendar \"" + cal.getName() + "\":\n";
				for (int j = 0; j < cal.getEvents().length; j++) {
					msg = msg + j + ": " + cal.getEvents()[j].toStringList(false) + "\n";
				}
			}
			
			//no cal + next
			else if (cal == null && command[1].equals("next")) {
				int dateNow = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
				Event[] events = null;
				for (int i = 0; i < calendars.size(); i++) {
					Event[] tmpEvents = calendars.get(i).getNext(dateNow);
					if (tmpEvents.length > 0) {						
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
					msg = msg + events[i].toStringList(true) + "\n";					
				}
			}
			
			//cal + next
			else if (cal != null && command[2].equals("next")) {
				int dateNow = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
				Event[] events = cal.getNext(dateNow);
				for (int i = 0; i < events.length; i++) {
					msg = msg + events[i].toStringList(true) + "\n";					
				}
			}
			
			//date
			else {
				//dateFormat: dd.mm.yyyy
				int indexDate = cal == null ? 1 : 2;
				int start, end;
				if (command[indexDate].length() == 10) {
					command[indexDate] = command[indexDate] + " 00:00";
					start = CalDateFormat.dateToMin(command[indexDate], firstYear);
					end = start + 1440;
				}
				//dateFormat: mm.yyyy
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
				//dateFormat yyyy
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
								msg = msg + calendars.get(i).getEvents()[j].toStringList(true) + "\n";
							}
						}
					}
				}
				
				//cal
				else {
					for (int j = 0; j < cal.getEvents().length; j++) {
						if (cal.getEvents()[j].getDate() >= start && cal.getEvents()[j].getDate() < end) {
							msg = msg + cal.getEvents()[j].toStringList(true) + "\n";
						}
					}
				}
			}
			printMessage(msg);
		} 
		//no event found 
		catch (NullPointerException ne) {
		} 
		//to less parameters given | wrong parameters given
		catch (ArrayIndexOutOfBoundsException | IllegalArgumentException aie) {
			errorSyntax(9);
		}
		//else
		catch (Exception e) {
			errorUnkown();
		}
	}
	
	/**
	 * 
	 */
	private void cmdEventdel(String[] command) {
		try {
			Calendar cal = getCalendarByName(command[1]);
			boolean deleted = false;
			int number;
			
			try {
				number = Integer.parseInt(command[2]);
			} catch (NumberFormatException e) {
				number = -1;
			}
			
			//delete by number
			if (number >= 0) {
				deleted = cal.removeEvent(number);
			}
			//delete by name
			else {
				deleted = cal.removeEvent(command[2]);
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
	
	private void cmdLunar(String[] command) {
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
			removed = removed && calendars.get(i).removeAllEvents();
			removed = removed && calendars.get(i).deleteCalendar();
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
	 * prints SyntaxError
	 * @param indexCmd
	 */
	private void errorSyntax (int indexCmd) {
		String msg = "Syntax Error!\nType \"" + commands.getPrefix() + commands.getCommand(indexCmd).getCommand() + " " + commands.getCommand(0).getCommand() + "\" for more information";
		printMessage(msg);
	}
	
	private void errorCalNotFound () {
		printMessage("Error: Calendar not found!");
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
		CalendarBot.printMessage(msg, false);
	}
	
	/**
	 * Checks if {@code calendar} exists
	 * @param calendar
	 * @return
	 */
	private boolean existsCalendar (Calendar calendar) {
		boolean equals = false;
		for (int i = 0; i < calendars.size(); i++) {
			equals = equals || calendars.get(i).equals(calendar);			
		}
		return equals;
	}
	
	/**
	 * 
	 * @param name
	 * @return calendar with name {@code name} or null if calendar not exists
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
				return number * 518400;
			default:
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
