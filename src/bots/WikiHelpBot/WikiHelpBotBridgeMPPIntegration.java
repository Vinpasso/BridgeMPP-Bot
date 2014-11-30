package bots.WikiHelpBot;

import bridgempp.bot.wrapper.BotWrapper.Message;

/**
 * 
 * @author Ediacarium
 *
 *         The WikiHelpBot integration class for BridgeMPP
 *
 */
public class WikiHelpBotBridgeMPPIntegration extends bridgempp.bot.wrapper.BotWrapper.Bot {

	public static final String Name = "WikiHelpBot";

	WikiHelpBot helpBot;

	public WikiHelpBotBridgeMPPIntegration() {
	}

	@Override
	public void initializeBot() {
		this.helpBot = new WikiHelpBot("de");
	}

	@Override
	public void messageRecieved(bridgempp.bot.wrapper.BotWrapper.Message message) {
		try {
			String wikiWis = helpBot.getWikiBotWisdom(message.getMessage());
			if (wikiWis != null) {
				sendMessage(new Message("WikiBot", wikiWis));
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

}