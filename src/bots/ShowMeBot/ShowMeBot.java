package bots.ShowMeBot;

import java.io.IOException;
import java.net.URL;

import bridgempp.bot.wrapper.BotConsoleTester.ConsoleBot;
import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ShowMeBot extends Bot {

	private String[] triggers = { "zeig mir", "show me" };

	@Override
	public void initializeBot() {

	}

	@Override
	public void messageRecieved(Message message) {
		String query = hasTrigger(message.getMessage());
		if(query == null)
		{
			return;
		}
		query = query.replaceAll("[^a-zA-Z0-9]", "");
		try
		{
			new URL("http://" + query + ".jpg.to").openConnection();
			sendMessage(new Message(message.getGroup(), "<img src=\"http://" + query + ".jpg.to\" alt=\""+query+"\" width=\"320\" height=\"240\"/>", "XHTML"));
		}
		catch(IOException e)
		{
			return;
		}
	}

	public String hasTrigger(String message) {
		message = message.trim().toLowerCase();
		for (int i = 0; i < triggers.length; i++) {
			if (message.contains(triggers[i])) {
				return message.substring(message.indexOf(triggers[i])
						+ triggers[i].length());
			}
		}
		return null;
	}
}
