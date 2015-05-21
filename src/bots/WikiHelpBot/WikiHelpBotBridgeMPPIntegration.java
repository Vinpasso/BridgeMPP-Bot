package bots.WikiHelpBot;

import java.util.regex.Pattern;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Message;

/**
 * 
 * @author Ediacarium
 *
 *         The WikiHelpBot integration class for BridgeMPP
 *
 */

public class WikiHelpBotBridgeMPPIntegration extends bridgempp.bot.wrapper.Bot {

	public static final String Name = "WikiHelpBot";
	public static Pattern HTMLBrPattern = Pattern.compile("<br>");

	WikiHelpBot helpBot;

	public WikiHelpBotBridgeMPPIntegration() {
	}

	@Override
	public void initializeBot() {
		this.helpBot = new WikiHelpBot("de");
	}

	@Override
	public void messageReceived(bridgempp.bot.wrapper.Message message) {
		String wikiWis = new String();
		try {
			wikiWis = helpBot.getWikiBotWisdom(message.getPlainTextMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (wikiWis != null) {
			String xhtmlwikiWisdom = HTMLBrPattern.matcher(wikiWis).replaceAll("<br/>");
			sendMessage(new Message("WikiBot", xhtmlwikiWisdom,MessageFormat.XHTML));
		}
	}

}