package bots.ParrotBot;

import java.util.regex.Pattern;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ParrotBotBridgeMPPIntegration extends Bot {

	ParrotCage cage;
	
	public static Pattern isKillCommand = Pattern.compile("\\A\\?parrot kill");
	public static Pattern isFeedCommand = Pattern.compile("\\A\\?parrot feed");
	public static Pattern isBuyCommand =  Pattern.compile("\\A\\?parrot buy");

	public ParrotBotBridgeMPPIntegration() {
		cage = new ParrotCage();
	}

	@Override
	public void initializeBot() {
	}

	@Override
	public void messageRecieved(Message message) {
		try {
			String msg = message.getMessage().trim();
			String[] msgWords = msg.split(" ");
			StringBuilder strBuilder = new StringBuilder();

			if(msg.length() > 12 && isBuyCommand.matcher(msg).find()){
				cage.addParrot(msg.substring(12));
			}
			else if(msg.length() > 13 && isFeedCommand.matcher(msg).find()){
				cage.feedParrot(msg.substring(13));
			}
			else if(msg.length() > 13 && isKillCommand.matcher(msg).find()){
				cage.killParrot(msg.substring(13));
			}

			cage.updateParrots();
			strBuilder.append(cage.getStatus());
			strBuilder.append(cage.processMessage(msgWords));
			if (!strBuilder.equals("")) {
				sendMessage(new Message("Parrots", strBuilder.toString(),"XHTML"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
