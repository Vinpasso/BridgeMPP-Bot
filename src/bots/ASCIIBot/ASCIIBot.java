package bots.ASCIIBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class ASCIIBot extends Bot {

	Pattern largeText = Pattern.compile("\\?ascii large (.*)");
	Pattern styleText = Pattern.compile("\\?ascii style \\\"([^\"]+)\\\" (.*)");
	
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
		Matcher styleTextMatcher = styleText.matcher(original);
		if(styleTextMatcher.find())
		{
			applyStyleMessage(styleTextMatcher.group(1), styleTextMatcher.group(2), message);
		}
	}

	private void largeText(String text, Message message) {
		sendMessage(new Message(message.getGroup(), applyFontStyle("font-size:128px", text), MessageFormat.XHTML));
	}
	
	private void applyStyleMessage(String style, String text, Message message)
	{
		sendMessage(new Message(message.getGroup(), applyFontStyle(style, text), MessageFormat.XHTML));
	}
	
	private String applyFontStyle(String style, String content)
	{
		return "<p/><span style='" + style + "'>"+content+"</span>";
	}

}
