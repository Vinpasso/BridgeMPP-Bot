package bots.ParrotBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ParrotBotBridgeMPPIntegration extends Bot {

	ParrotCage cage;

	public ParrotBotBridgeMPPIntegration() {
		cage = new ParrotCage();
	}

	@Override
	public void initializeBot() {
	}

	@Override
	public void messageRecieved(Message message) {
		try {
			String msg = message.getMessage();
			String[] msgWords = msg.split(" ");
			StringBuilder strBuilder = new StringBuilder();

			if (msgWords[0].equals("?parrot") && msgWords.length >= 2) {
				if (msgWords[1].equals("kill") && msgWords.length >= 3) {
					cage.killParrot(msgWords[2]);
				}

				if (msgWords[1].equals("buy")) {
					ParrotBot newParrot = msgWords.length < 3 ? cage.addParrot(null) : cage.addParrot(msgWords[2]);
					if (newParrot != null) {
						strBuilder.append("bought new parrot with name: " + newParrot.getName() + "\n");
					}
				}
				if (msgWords[1].equals("feed") && msgWords.length >= 3) {
					cage.feedParrot(msgWords[2]);
				}
			}

			cage.updateParrots();
			strBuilder.append(cage.getStatus());
			strBuilder.append(cage.processMessage(msgWords));
			if (!strBuilder.equals("")) {
				sendMessage(new Message("Parrots", strBuilder.toString()));
			}

		} catch (Exception e) {

		}
	}
}
