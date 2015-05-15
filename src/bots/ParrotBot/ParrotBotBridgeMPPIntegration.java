package bots.ParrotBot;

import java.util.regex.Pattern;

import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

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
	public void messageReceived(Message message) {
		StringBuilder strBuilder = new StringBuilder();
		try {
			String msg = message.getMessage().trim();
			strBuilder = new StringBuilder();

			if(msg.length() > 10 && isBuyCommand.matcher(msg).find()){
				cage.addParrot(msg.substring(11));
			}
			else if(msg.length() > 13 && isFeedCommand.matcher(msg).find()){
				cage.feedParrot(msg.substring(13));
			}
			else if(msg.length() > 13 && isKillCommand.matcher(msg).find()){
				cage.killParrot(msg.substring(13));
			}

			cage.updateParrots();
			strBuilder.append(cage.getStatus());
			strBuilder.append(cage.processMessage(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!strBuilder.equals("")) {
			sendMessage(new Message("Parrots", strBuilder.toString(),"XHTML"));
		}
	}
}
