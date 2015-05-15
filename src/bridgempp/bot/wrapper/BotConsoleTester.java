package bridgempp.bot.wrapper;

import java.util.Scanner;

import bridgempp.bot.messageformat.MessageFormat;






public class BotConsoleTester {

	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter Bot FQCN:");
		try {
			Bot bot = (Bot) Class.forName(scanner.nextLine()).newInstance();
			bot.initializeBot();
			System.out.println("Bot loaded");
			while(true)
			{
				bot.messageReceived(new Message("TESTGROUP", scanner.nextLine(), MessageFormat.PLAIN_TEXT));
				System.out.println("Bot Execution Completed");
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
}
