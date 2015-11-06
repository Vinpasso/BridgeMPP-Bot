package bridgempp.bot.wrapper;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.metawrapper.MetaWrapper;

public class BotConsoleTester
{

	public static void main(String[] args) throws IOException
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter Bot FQCN:");
		try
		{
			Bot bot = (Bot) Class.forName(scanner.nextLine()).newInstance();
			bot.properties = new Properties();
			bot.name = "Test Bot";
			bot.configFile = "config.txt";
			if (bot instanceof MetaWrapper)
			{
				System.out.println("Please enter the Meta Class FQCN:");
				bot.properties.put("metaclass", scanner.nextLine());
			}
			bot.initializeBot();
			System.out.println("Bot loaded");
			while (true)
			{
				bot.messageReceived(new Message("TESTGROUP", "Console Sender", "Console Receiver", scanner.nextLine(), MessageFormat.PLAIN_TEXT));
				System.out.println("Bot Execution Completed");
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		scanner.close();
	}

}
