package bots.ParrotBot;

import java.util.ArrayList;
import java.util.List;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ParrotBotBridgeMPPIntegration extends Bot{

	List<ParrotBot> parrots;
	
	ParrotBotBridgeMPPIntegration(){
		parrots = new ArrayList<ParrotBot>();
	}
	
	@Override
	public void initializeBot() {
		parrots.add(new ParrotBot());
	}

	@Override
	public void messageRecieved(Message message) {
		String msg = message.message;
		String[] msgWords = msg.split(" ");
		
		if(msgWords[1].equals("?parrot") && msgWords.length >= 2){
			if(msgWords[2].equals("kill") && parrots.size() > 0){
					try{
						parrots.remove(Integer.parseInt(msgWords[3]));
					}
					catch(Exception e){
						parrots.remove(0);
					}
					
			}
			if(msgWords[2].equals("buy")){
				parrots.add(new ParrotBot());
			}
		}
		
		for(ParrotBot parrot : parrots){
			parrot.processSplitMessage(msgWords);
		}
	}

	
	
}
