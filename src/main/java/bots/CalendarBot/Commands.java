package bots.CalendarBot;

public class Commands {
	private final static Commands INSTANCE = new Commands ();
	private final String prefix = "?cal ";
	private final Command[] commands = {
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
		setDelAllCal(),
		setVersion()
	};
	private final Command[] hiddenCommands = {
		setReset(),
		setNo(),
		setYes()
	};
	
	private Commands () {}
	
	public static Commands getInstance () {
	    return Commands.INSTANCE;
	}
	
	private Command setHelp () {
		return new Command (
				new String[] {"help", "?", "hilfe", "aiuto", "ayuda", "ivastehnix", "auxilium"},
				"",
				"Shows all available commands. Type \"" + prefix + "%command% help \" for more information"
			);
	}
	
	private Command setPrintDate () {
		return new Command (
				new String[] {"date"},
				"",
				"Shows the current date including time"
			);
	}
	
	private Command setPrintTime () {
		return new Command (
				new String[] {"time"},
				"",
				"Shows the current time"
			);
	}
	
	private Command setAlertson () {
		return new Command (
				new String[] {"alertson"},
				"",
				"Sets the alerts on"
			);
	}
	
	private Command setAlertsoff () {
		return new Command (
				new String[] {"alertsoff"},
				"",
				"Sets the alerts off"
			);
	}
	
	private Command setCreateCal () {
		return new Command (
				new String[] {"calcr"},
				"{name} {repeat} {remind}",
				"Creates new Calendar\nname:\tName of Calendar\n"
				 + setRepeat() + "\n" 
				 + setRemind()
			);
	}
	
	private Command setListCal () {
		return new Command(
				new String[] {"calls"},
				"",
				"Lists all calendars"
			);
	}
	
	private Command setDelCal () {
		return new Command (
				new String[] {"caldel"},
				"{name}",
				"Delets the named calendar\n"
				+ "name:\tName of Calendar"
			);
	}
	
	private Command setCreateEvent () {
		return new Command (
				new String[] {"eventcr"},
				"{calendar} {description} {dd.mm.yyyy | dd.mm.yyyy-hh:mm | dateInMin} [repeat remind]",
				"Creates new event\n"
				+ "calendar:\tName of calendar in which the event should be created\n"
				+ "description:\tDescription of event\n"
				+ setRepeat() + "\n"
				+ setRemind()
			);
	}
	
	private Command setListEvents () {
		return new Command (
				new String[] {"eventls"},
				"[calendar] {all | next | dd.mm.yyyy | mm.yyyy | yyyy}",
				"Lists all events\n"
				+ "calendar:\tName of calendar of which all events should be shown"
			);
	}
	
	private Command setDelEvent () {
		return new Command (
				new String[] {"eventdel"},
				"{calendar} {name | number}",
				"Deletes Event\n"
				+ "calendar:\tName of calendar in which the event should be deleted\n"
				+ "name:\tDescription of event\n"
				+ "number:\tNumber of event" 
			);
	}
	
	private Command setDelPast () {
		return new Command (
				new String[] {"eventpastdel"},
				"",
				"Deletes all past events without repeat"
			);
	}
	
	private Command setDelPastOn () {
		return new Command (
				new String[] {"autodelpaston"},
				"",
				"Activates the automatic deleting of past events without repeat"
			);
	}
	
	private Command setDelPastOff () {
		return new Command (
				new String[] {"autodelpastoff"},
				"",
				"Deactivates the automatic deleting of past events without repeat"
			);
	}
	
	private Command setLunarPhase () {
		return new Command (
				new String[] {"lunar"},
				"[ger]",
				"shows current lunar Phase\n"
				+ "ger: Shows it in German"
		);
	}
	
	private Command setDelAllCal () {
		return new Command (
				new String[] {"deleteallcalendars"},
				"",
				"Deletes all calendars"
		);
	}
	
	private Command setVersion () {
		return new Command (
				new String[] {"version"},
				"",
				"shows version of CalendarBot"
		);
	}
	
	private String setRepeat () {
		return "repeat:\tNumber of days the event will repeat\n"
				+ "Available parameters:\n"
				+ "0 = off\n" 
				+ "%number%d = number of days\n"
				+ "%number%w = number of weeks\n"
				+ "%number%m = number of months \n"
				+ "&number%y = number of years";
	}
	
	private String setRemind () {
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
	
	//hidden commands
	private Command setReset () {
		return new Command (
			new String[] {"reset"},
			"",
			"resets calendar"
			);
	}
	
	private Command setYes () {
		return new Command (
			new String[] {"yes"},
			"",
			""
			);
	}
	
	private Command setNo () {
		return new Command (
			new String[] {"no"},
			"",
			""
			);
	}
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * 
	 * @param index
	 * @return {@code Command} at {@code index} INCLUDING hiddenCommands
	 */
	public Command getCommand (int index) {
		if (index >= 0 && index < 100) {
			return commands[index];
		}
		else {
			return hiddenCommands[index - 100];
		}
	}
			
	/**
	 * 
	 * @return all commands (excluding hiddenCommands) as String with parameters and NO description
	 */
	public String commandsToString () {
		String cmds = "";
		for (int i = 0; i < commands.length; i++) {
			cmds = cmds + commands[i].commandToString();
		}
		return cmds;
	}
	
	/**
	 * 
	 * @param index
	 * @return Command (INCLUDING hiddenCommands) at {@code index} as String with parameters and description
	 */
	public String commandToString (int index) {
		return getCommand(index).toString();
	}
	
	public int getIndexForCommand (String cmd) {
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].isCommand(cmd)) return i;
		}		
		for (int i = 0; i < hiddenCommands.length; i++) {
			if (hiddenCommands[i].isCommand(cmd)) return (100 + i);
		}
		return -1;
	}
	
	/**
	 * 
	 * @author Bernie
	 *
	 */
	public class Command {
		private String[] command;
		private String parameter;
		private String description;
		
		public Command (String[] command, String parameter, String description) {
			this.command = command;
			this.parameter = parameter;
			this.description = description;
		}
		
		/**
		 * 
		 * @return ONLY first element
		 */
		public String getCommand () {
			return command[0];
		}
		
		@Override
		public String toString() {
			return command[0] + " " + parameter + "\n" + description;
		}
		
		/**
		 * 
		 * @return command as String with parameters
		 */
		public String commandToString () {
			return command[0] + " " + parameter + "\n";
		}
		
		public boolean isCommand (String cmd) {
			for (int i = 0; i < command.length; i++) {
				if (cmd.equals(command[i])) return true;
			}
			return false;
		}
	}
	
}
