package bots.NewsBot;

import bots.NewsBot.logger.ErrorLogger;
import bots.NewsBot.news.NewsInterpreter;
import bridgempp.bot.wrapper.BotWrapper;

public class Wrapper extends bridgempp.bot.wrapper.BotWrapper.Bot {
	private NewsInterpreter ni;

	public Wrapper() {
		ni = new NewsInterpreter();
		initializeBot();
	}

	public String evaluateMessage(String msg) {
		if (!msg.startsWith("?")) {
			return null;
		}
		int commandEnd = msg.indexOf(" ");
		String command;
		String args = null;
		if (commandEnd > 0) {
			command = msg.substring(1, commandEnd);
			args = msg.substring(commandEnd+1);
		} else {
			command = msg.substring(1);
		}

		switch (command) {
			case "reload":
				initializeBot();
				return "Successfully reloaded.";
			case "news":
				return ni.getAnswer(args);
		}
		return null;
	}

	@Override
	public void initializeBot() {
		ErrorLogger.init();
		ni.init();
	}

	@Override
	public void messageRecieved(BotWrapper.Message message) {
		String botResponse = evaluateMessage(message.message);
		if (botResponse != null) {
			sendMessage(new BotWrapper.Message(message.target, botResponse));
		}
	}
}
