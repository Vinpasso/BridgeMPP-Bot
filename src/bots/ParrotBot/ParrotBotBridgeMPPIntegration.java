package bots.ParrotBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ParrotBotBridgeMPPIntegration extends Bot {

	Map<String, ParrotBot> parrots;
	List<String> doneParrots;

	public ParrotBotBridgeMPPIntegration() {
		parrots = new HashMap<String, ParrotBot>();
		doneParrots = new ArrayList<String>();
	}

	@Override
	public void initializeBot() {
	}

	@Override
	public void messageRecieved(Message message) {
		try {
			String msg = message.message;
			String[] msgWords = msg.split(" ");
			StringBuilder strBuilder = new StringBuilder("");

			if (msgWords[0].equals("?parrot") && msgWords.length >= 2) {
				if (msgWords[1].equals("kill") && parrots.size() > 0 && msgWords.length >= 3) {
					try {
						if (msgWords[2].equals("all")) {
							for (ParrotBot parrot : parrots.values()) {
								parrot.kill();
							}
						} else {
							parrots.get(msgWords[2]).kill();
						}
					} catch (Exception e) {
						parrots.get(0).kill();
					}
				}

				if (msgWords[1].equals("buy")) {
					ParrotBot parrot = msgWords.length >= 3 ? new ParrotBot(msgWords[2]) : new ParrotBot();
					parrots.put(parrot.getName(), parrot);
					strBuilder.append("bought new parrot with name: " + parrot.getName() + "\n");
				}
				if (msgWords[1].equals("feed") && msgWords.length >= 3) {
					ParrotBot parrot = parrots.get(msgWords[2]);
					if (parrot != null) {
						parrot.feed();
					}
				}
			}

			for (ParrotBot parrot : parrots.values()) {
				parrot.updateParrot();
				String parrotStatus = parrot.getStatus();
				if (parrotStatus != null) {
					strBuilder.append(parrotStatus).append("\n");
				}
				if (parrot.isDone()) {
					doneParrots.add(parrot.getName());
				}
			}
			for (String doneParrot : doneParrots) {
				parrots.remove(doneParrot);
			}
			for (ParrotBot parrot : parrots.values()) {
				String parrotMessage = parrot.processSplitMessage(msgWords);
				if (parrotMessage != null) {
					strBuilder.append(parrot.getName()).append(": ").append(parrotMessage).append("\n");
				}
			}
			if (!strBuilder.equals("")) {
				sendMessage(new Message("Parrots", strBuilder.toString()));
			}

		} catch (Exception e) {

		}
	}
}
