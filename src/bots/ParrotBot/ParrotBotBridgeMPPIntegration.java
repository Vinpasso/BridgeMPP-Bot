package bots.ParrotBot;

import java.util.HashMap;
import java.util.Map;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ParrotBotBridgeMPPIntegration extends Bot{

	Map<String,ParrotBot> parrots;
	
	ParrotBotBridgeMPPIntegration(){
		parrots = new HashMap<String,ParrotBot>();
	}
	
	@Override
	public void initializeBot() {
	}

	@Override
	public void messageRecieved(Message message) {
		String msg = message.message;
		String[] msgWords = msg.split(" ");
		StringBuilder strBuilder = new StringBuilder();
		
		if(msgWords[0].equals("?parrot") && msgWords.length >= 2){
			if(msgWords[1].equals("kill") && parrots.size() > 0 && msgWords.length >= 3){
					try{
						parrots.remove(msgWords[2]);
					}
					catch(Exception e){
						parrots.remove(0);
					}
					
			}
			if(msgWords[1].equals("buy")){
				ParrotBot parrot = msgWords.length >= 3 ? new ParrotBot(msgWords[2]) :new ParrotBot();
				parrots.put(parrot.getName(), parrot);
			}
			if(msgWords[1].equals("feed") && msgWords.length >= 3){
				ParrotBot parrot = parrots.get(msgWords[2]);
				if(parrot != null){
					parrot.feed();
				}
			}
		}
		
		for(ParrotBot parrot : parrots.values()){
			parrot.updateParrot();
			String parrotStatus = parrot.getStatus();
			if(parrotStatus != null){
				strBuilder.append(parrotStatus).append("\n");
			}
			
		}
		for(ParrotBot parrot : parrots.values()){
			String parrotMessage = parrot.processSplitMessage(msgWords);
			if(parrotMessage != null){
				strBuilder.append(parrotMessage);
			}
		}
		if(!strBuilder.equals("")){
			sendMessage(new Message("Parrots",strBuilder.toString()));
		}
	}

	
	
}
