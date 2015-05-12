package bots.CalcBot;

import bots.CalcBot.calc.CalculationInterpreter;
import bots.CalcBot.logger.ErrorLogger;
import bridgempp.bot.wrapper.BotWrapper;

public class Wrapper extends bridgempp.bot.wrapper.BotWrapper.Bot {
	private CalculationInterpreter ci;

	public Wrapper() {
		ci = new CalculationInterpreter();
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
			case "calc":
				return ci.getAnswer(args);
		}
		return null;
	}

	@Override
	public void initializeBot() {
		ErrorLogger.init();
		ci.init();
	}

	@Override
	public void messageReceived(BotWrapper.Message message) {
		String botResponse = evaluateMessage(message.getMessage());
		if (botResponse != null) {
			sendMessage(new BotWrapper.Message(message.getTarget(), botResponse));
		}
	}
}
