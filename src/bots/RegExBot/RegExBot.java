package bots.RegExBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class RegExBot extends Bot {

	private Pattern pattern;
	
	@Override
	public void initializeBot() {
		pattern = Pattern.compile("\\?regex\\s+?(.+?)\\s+?(.+)");
	}

	@Override
	public void messageReceived(Message message) {
		if(message.getMessage().toLowerCase().indexOf("?regex") > -1)
		{
			Matcher matcher = pattern.matcher(message.getMessage());
			matcher.find();
			if(matcher.groupCount() < 3)
			{
				sendMessage(new Message(message.getGroup(), "Not enough Arguments: Usage ?regex <regex> <matchstring>", MessageFormat.PLAIN_TEXT));
				return;
			}
			Pattern thisPattern = Pattern.compile(matcher.group(2));
			Matcher thisMatcher = thisPattern.matcher(matcher.group(3));
			while(thisMatcher.find())
			{
				sendMessage(new Message(message.getGroup(), thisMatcher.group(), MessageFormat.PLAIN_TEXT));
			}
		}
	}

}
