package bots.CalendarBot;

public class CommandSyntax {
	static String prefix = "?cal ";
	static final String[][] commands = {
		setHelp(),
		setPrintDate(),
		setPrintTime(),
		setAlertson(),
		setAlertsoff(),
		setCreateCal(),
		setListCal(),
		setDelCal(),
		setCreateEvent(),
		setListEvents(),
		setDelEvent(),
		setDelPast(),
		setDelPastOn(),
		setDelPastOff(),
		setLunarPhase(),
		setDelAllCal()
	};
	
	private static String[] setHelp () {
		return new String[] {
				"help",
				"",
				"Shows all available commands"
			};
	}
	
	private static String[] setPrintDate () {
		return new String[] {
				"date",
				"",
				"Shows the current date including time"
			};
	}
	
	private static String[] setPrintTime () {
		return new String[] {
				"time",
				"",
				"Shows the current time"
			};
	}
	
	private static String[] setAlertson () {
		return new String[] {
				"alertson",
				"",
				"Sets the alerts on"
			};
	}
	
	private static String[] setAlertsoff () {
		return new String[] {
				"alertsoff",
				"",
				"Sets the alerts off"
			};
	}
	
	private static String[] setCreateCal () {
		return new String[] {
				"calcr",
				"{name} {repeat} {remind}",
				"Creates new Calendar\nname:\tName of Calendar\n"
				 + setRepeat() + "\n" 
				 + setRemind()
			};
	}
	
	private static String[] setListCal () {
		return new String[] {
				"calls",
				"",
				"Lists all calendars"
			};
	}
	
	private static String[] setDelCal () {
		return new String[] {
				"caldel",
				"{name}",
				"Delets the named calendar\n"
				+ "name:\tName of Calendar"
			};
	}
	
	private static String[] setCreateEvent () {
		return new String[] {
				"eventcr",
				"{calendar} {description} {dateInMin | dd.mm.yyyy | dd.mm.yyyy-hh:mm} [repeat remind]",
				"Creates new event\n"
				+ "calendar:\tName of calendar in which the event should be created\n"
				+ "description:\tDescription of event\n"
				+ setRepeat() + "\n"
				+ setRemind()
			};
	}
	
	private static String[] setListEvents () {
		return new String[] {
				"eventls",
				"[calendar] {all | next | dd.mm.yyyy | mm.yyyy | yyyy}",
				"Lists all events\n"
				+ "calendar:\tName of calendar of which all events should be shown"
			};
	}
	
	private static String[] setDelEvent () {
		return new String[] {
				"eventdel",
				"{calendar} {name | number}",
				"Deletes Event\n"
				+ "calendar:\tName of calendar in which the event should be deleted\n"
				+ "name:\tDescription of event\n"
				+ "number:\tNumber of event" 
			};
	}
	
	private static String[] setDelPast () {
		return new String[] {
				"eventpastdel",
				"",
				"Deletes all past events without repeat"
			};
	}
	
	private static String[] setDelPastOn () {
		return new String[] {
				"autodelpaston",
				"",
				"Activates the automatic deleting of past events without repeat"
			};
	}
	
	private static String[] setDelPastOff () {
		return new String[] {
				"autodelpastoff",
				"",
				"Deactivates the automatic deleting of past events without repeat"
			};
	}
	
	private static String[] setLunarPhase () {
		return new String[] {
				"lunar",
				"[ger]",
				"shows current lunar Phase\n"
				+ "ger: Shows it in German"
		};
	}
	
	private static String[] setDelAllCal () {
		return new String[] {
				"deleteallcalendars",
				"",
				"Deletes all calendars"
			};
	}
	
	private static String setRepeat () {
		return "repeat:\tNumber of days the event will repeat\n"
				+ "Available parameters:\n"
				+ "0 = off\n" 
				+ "%number%d = number of days\n"
				+ "%number%w = number of weeks\n"
				+ "%number%m = number of months \n"
				+ "&number%y = number of years";
	}
	
	private static String setRemind () {
		return "remind:\tNumber of minutes the event will be remind before starting\n"
				+ "Available parameters:\n"
				+ "0 = off\n"
				+ "%number%n = number of minutes\n"
				+ "%number%h = number of hours\n"
				+ "%number%d = number of days\n"
				+ "%number%w = number of weeks\n"
				+ "%number%m = number of months \n"
				+ "&number%y = number of years";
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static String[] getCommands() {
		String[] cmd = new String[commands.length];
		for (int i = 0; i < cmd.length; i++) {
			cmd[i] = commands[i][0];
		}
		return cmd;
	}
	
	public static String[] getParameters () {
		String[] cmd = new String[commands.length];
		for (int i = 0; i < cmd.length; i++) {
			cmd[i] = commands[i][1];
		}
		return cmd;
	}
	
	public static String[] getHelps () {
		String[] cmd = new String[commands.length];
		for (int i = 0; i < cmd.length; i++) {
			cmd[i] = commands[i][2];
		}
		return cmd;
	}
	
	public static String[][] getCommandsWithParamHelp () {
		return commands;
	}
	
	
	public static String[] getCommandWithParamHelp (int index) {
		return commands[index];
	}
	
	public static String commandsToString () {
		String[] tmp = getCommands();
		String cmds = "";
		for (int i = 0; i < tmp.length - 2; i++) {
			cmds = cmds + tmp[i] + "\n";
		}
		cmds = cmds + tmp[tmp.length - 2];
		return cmds;
	}
	
	public static String commandsWithParamToString () {
		String[] commands = getCommands();
		String[] parameters = getParameters();
		String cmds = "";
		for (int i = 0; i < commands.length - 2; i++) {
			cmds = cmds + commands[i] + " " + parameters[i] + "\n";
		}
		cmds = cmds + commands[commands.length - 2] + " " + parameters[parameters.length - 1];
		return cmds;
	}
	
	public static String commandWithParamHelpToString (int index) {
		String[] command = getCommandWithParamHelp(index);
		return command[0] + " " + command[1] + "\n" + command[2];
	}
	
	public static boolean equalsHelp(String cmd) {
		switch (cmd.toLowerCase()) {
		case "help":
		case "?":
		case "hilfe":
		case "aiuto":
		case "ayuda":
		case "ivastehnix":
		case "auxilium":
			return true;
		}
		return false;
	}
	
}
