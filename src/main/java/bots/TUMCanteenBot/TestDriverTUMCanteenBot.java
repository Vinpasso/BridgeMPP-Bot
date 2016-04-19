package bots.TUMCanteenBot;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Message;

public class TestDriverTUMCanteenBot {
	
	private static final String[] testCases = {
		"some text", "?command 123", "?", "", " ", "?listcanteens", "?listcanteens garbagetext123123??",
		"?canteen 422", "?canteen", "?canteen ", "?canteen b", "?canteen    422"
	};
	
	public static void main(String[] args) {
		TUMCanteenBot bot = new TUMCanteenBot();
		bot.initializeBot();
		
		int i = 1;
		for (String s : testCases) {
			System.out.println(i++ + ": " + s);
			Message message = new Message("GROUP", s, MessageFormat.PLAIN_TEXT);
			bot.messageReceived(message);
			System.out.println();
		}
		
		bot.deinitializeBot();
	}
	
}
