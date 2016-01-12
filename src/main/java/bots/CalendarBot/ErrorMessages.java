package bots.CalendarBot;

public class ErrorMessages {
	private static Commands commands = Commands.getInstance();
	
	public static String calNotLoadError () {
		return "Error: Could not load Calendars\nType \"" + commands.getPrefix() + commands.getCommand(100) + "\" to try again";
	}
	
	public static String syntaxError (int indexCmd) {
		return "Syntax Error!\nType \"" + commands.getPrefix() + commands.getCommand(indexCmd).getCommand() + " " + commands.getCommand(0).getCommand() + "\" for more information";
	}
	
	public static String CalNotFoundError () {
		return "Error: Calendar not found!";
	}
	
	public static String unknownError () {
		return "Unknown Error!";
	}
	
	public static String operationFailedError (String msg) {
		return "Error: Operation failed!" + (msg.equals("") ? "" : "\n" + msg);
	}
	
	public static String unknownCommand (String cmd) {
		return "Unknown Command " + cmd + "\nType \"" + commands.getPrefix() + commands.getCommand(0).getCommand() + "\" for more information";
	}
	
	public static String eventsNotLoadError (Calendar calendar) {
		return "Error: Could not load Events of Calendar \"" + calendar.getName() + "\""; 
	}	
}
