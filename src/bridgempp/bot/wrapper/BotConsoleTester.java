package bridgempp.bot.wrapper;

import java.util.Scanner;




import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class BotConsoleTester {

	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter Bot FQCN:");
		try {
			ConsoleBot bot = (ConsoleBot) Class.forName(scanner.nextLine()).newInstance();
			bot.initializeBot();
			System.out.println("Bot loaded");
			while(true)
			{
				bot.messageRecieved(new Message("TESTGROUP", scanner.nextLine(), "Plain Text"));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
	public static abstract class ConsoleBot extends Bot
	{
		public void sendMessage(Message message)
		{
			System.out.println("CONSOLE BOT" + message.toComplexString());
		}
	}
	
}
