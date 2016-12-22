package bridgempp.bot.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

import bridgempp.bot.database.PersistenceManager;
import bridgempp.bot.metawrapper.MetaWrapper;
import bridgempp.data.DataManager;
import bridgempp.message.MessageBuilder;
import bridgempp.util.Log;

public class BotConsoleTester
{

	public static void main(String[] args) throws IOException
	{
		Scanner scanner = new Scanner(System.in);
		PersistenceManager.loadFactory();
		Schedule.startExecutorService();
		Properties lastUsed = getLastUsed();
		String botclass = lastUsed.getProperty("BotFQCN");
		System.out.println("Please enter Bot FQCN (Default: " + botclass + "):");
		try
		{
			String input = scanner.nextLine();
			Bot bot = (Bot) Class.forName((input.length() == 0) ? botclass : input).newInstance();
			lastUsed.put("BotFQCN", (input.length() == 0) ? botclass : input);
			bot.properties = new Properties();
			bot.name = "Test Bot";
			bot.configFile = "config.txt";
			if (bot instanceof MetaWrapper)
			{
				String metaClass = lastUsed.getProperty("MetaFQCN");
				System.out.println("Please enter the Meta Class FQCN (Default: " + metaClass + "):");
				String metaInput = scanner.nextLine();
				bot.properties.put("metaclass", (metaInput.length() == 0) ? metaClass : metaInput);
				lastUsed.put("MetaFQCN", (metaInput.length() == 0) ? metaClass : metaInput);
			}
			saveLastUsed(lastUsed);
			bot.initializeBot();
			System.out.println("Bot loaded");
			while (true)
			{
				bot.messageReceived(new MessageBuilder(DataManager.getUserForIdentifier("Console"), DataManager.getEndpointForIdentifier("Console")).addPlainTextBody(scanner.nextLine()).build());
				System.out.println("Bot Execution Completed");
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		scanner.close();
	}

	private static Properties getLastUsed()
	{
		File memoryFile = new File("lastrun.txt");
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(memoryFile));
		} catch (Exception e)
		{
		}
		return properties;
	}

	private static void saveLastUsed(Properties properties)
	{
		File memoryFile = new File("lastrun.txt");
		try
		{
			properties.store(new FileOutputStream(memoryFile), "Last Used Bot");
		} catch (Exception e)
		{
			Log.log(Level.WARNING, "Could not save Last Used Properties");
		}
	}

}
