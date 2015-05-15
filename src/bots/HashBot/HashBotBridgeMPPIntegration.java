package bots.HashBot;

import java.util.regex.Pattern;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class HashBotBridgeMPPIntegration extends Bot{

	public static final Pattern hashPattern = Pattern.compile("\\A\\?hash ");
	public static final Pattern disablePattern = Pattern.compile("\\A\\?hash disable");
	public static final Pattern enablePattern = Pattern.compile("\\A\\?hash enable");
	public static final Pattern setHashTagChancePattern = Pattern.compile("\\A\\?hash chance ");
	
	private HashBot hashBot;
	private boolean hashTags;
	
	@Override
	public void initializeBot() {
		hashBot = new HashBot();
		hashTags = true;
	}

	@Override
	public void messageReceived(Message message) {
		
		String response = null;
		if(hashPattern.matcher(message.getMessage()).find()){
			if(enablePattern.matcher(message.getMessage()).find()){
				response = "enabled HashTagging";
			}
			else if(disablePattern.matcher(message.getMessage()).find()){
				hashTags = false;
				response = "disabled HashTagging";
			}
			else if(setHashTagChancePattern.matcher(message.getMessage()).find()){
				String[] splitString = message.getMessage().split(" ");
				if(splitString.length == 3){
					try{
					int hashChance = Integer.parseInt(splitString[2]);
					if(hashChance >= 0 && hashChance <= 100){
						hashBot.setHashChance(100-hashChance);
						response = "Set chance to:  " + hashChance;
					}
					} catch(Exception e){
						response = "Invalid chance count";
					}
				}
			}
			else{
				String[] splitString = message.getMessage().split(" ");
				if(splitString.length == 3){
					response = hashBot.generateHash(splitString[1], splitString[2]);
				}
			}
		}
		else if(hashTags){
			response = hashBot.generateHashTags(message.getMessage());
		}
		
		if(response != null){
			sendMessage(new Message("HashBot", response, MessageFormat.XHTML));
		}
	}

	
	
	
}
