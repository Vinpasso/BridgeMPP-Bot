package bots.ASCIIBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class ASCIIBot extends Bot {

	Pattern largeText = Pattern.compile("\\?ascii large (.*)");
	
	@Override
	public void initializeBot() {
	}

	@Override
	public void messageReceived(Message message) {
		String original = message.getPlainTextMessage();
		Matcher largeTextMatcher = largeText.matcher(original);
		if(largeTextMatcher.find())
		{
			largeText(largeTextMatcher.group(1), message);
		}
	}

	private void largeText(String text, Message message) {
		sendMessage(new Message(message.getGroup(), "<p/><span style='font-size:72'>"+text+"</span>", MessageFormat.XHTML));
	}

}
