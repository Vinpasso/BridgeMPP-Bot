package bots.ChatterBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class ChatterBot extends Bot {

	private ChatterBotFactory factory;
	private List<ChatterSessionWrapper> bots;
	private Map<String,ChatterSessionWrapper> names;

	@Override
	public void initializeBot() {
		factory = new ChatterBotFactory();
		bots = new ArrayList<>();
		names = new HashMap<>();
	}

	@Override
	public void messageReceived(Message message) {
		try {
		String msgText = message.getMessage();
		ChatterSessionWrapper wrap = null;
		if(msgText.startsWith("?chatter")){
			if(msgText.startsWith("?chatter add Cleverbot ")){
				wrap = 	new ChatterSessionWrapper(true,factory.create(ChatterBotType.CLEVERBOT).createSession(),msgText.substring(23));
			}
			if(msgText.startsWith("?chatter add Pandorabot ")){
				String pandorabotId = msgText.split(" ")[3];
				wrap = 	new ChatterSessionWrapper(true,factory.create(ChatterBotType.PANDORABOTS,pandorabotId).createSession(), msgText.substring(26 + pandorabotId.length()-1));
			}
			if(msgText.startsWith("?chatter mute ")){
				names.get(msgText.substring(14)).active = false;
			}
			if(msgText.startsWith("?chatter unmute ")){
				names.get(msgText.substring(16)).active = true;
			}
			if(msgText.startsWith("?chatter remove ")){
				ChatterSessionWrapper remWrapper = names.get(msgText.substring(16));
				bots.remove(remWrapper);
				names.remove(msgText.substring(16));
			}
			if(msgText.startsWith("?chatter help")){
				StringBuilder strb = new StringBuilder();
				strb.append("?chatter add Cleverbot <name> - adds a Cleverbot instance named <name>\n");
				strb.append("?chatter add Pandorabot <ID> <name> - adds the Pandorabot instance <ID> with name <ID>\n");
				strb.append("?chatter mute <name> - mutes <name>");
				strb.append("?chatter unmute <name> - unmutes <name>");
				strb.append("?chatter remove <name> - removes <name>");
			}
		}
		else{
			StringBuilder strb = new StringBuilder();
			for(ChatterSessionWrapper wrapper : bots){
				if(wrapper.active){
					strb.append(wrapper.name).append(": ").append(wrapper.session.think(msgText)).append("\n");
				}
				}
			if(strb.length() >= 1){
			sendMessage(new Message(message.getGroup(), strb.toString().trim(), "Plaintext"));
			}
		}
		if(wrap != null && !wrap.name.trim().isEmpty() && !names.containsKey(wrap.name)){
			bots.add(wrap);
			names.put(wrap.name, wrap);
		}
		}	 catch (Exception e) {
		e.printStackTrace();
	}
	}
}

