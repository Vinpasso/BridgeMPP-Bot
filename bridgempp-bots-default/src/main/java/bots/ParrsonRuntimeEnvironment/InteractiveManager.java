package bots.ParrsonRuntimeEnvironment;

import java.util.HashMap;

import javax.script.ScriptException;

import bridgempp.bot.wrapper.Message;

public class InteractiveManager
{
	private static HashMap<String, Runtime> interactiveSessions = new HashMap<>();

	public static String messageReceived(Message message)
	{
		if (interactiveSessions.containsKey(message.getSender()))
		{
			try
			{
				Runtime runtime = interactiveSessions.get(message.getSender());
				Object result = runtime.execute(message.getPlainTextMessage());
				return result.toString();
			} catch (ScriptException e)
			{
				return "Script Error: " + e.getLocalizedMessage();
			}
		}
		return null;
	}

	public static void addInteractivity(String sender, Runtime runtime)
	{
		interactiveSessions.put(sender, runtime);
	}

	public static Runtime removeInteractive(String sender)
	{
		return interactiveSessions.remove(sender);
	}

}
