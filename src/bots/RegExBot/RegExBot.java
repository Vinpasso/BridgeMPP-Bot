package bots.RegExBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class RegExBot extends Bot {

	private Pattern pattern;
	
	@Override
	public void initializeBot() {
		pattern = Pattern.compile("(.+)\\s+?(.+)\\s+?(.+)");
	}

	@Override
	public void messageRecieved(Message message) {
		if(message.getMessage().toLowerCase().startsWith("?regex"))
		{
			Matcher matcher = pattern.matcher(message.getMessage());
			if(matcher.groupCount() < 3)
			{
				sendMessage(new Message(message.getGroup(), "Not enough Arguments: Usage ?regex <regex> <matchstring>", "Plain Text"));
				return;
			}
			Pattern thisPattern = Pattern.compile(matcher.group(2));
			Matcher thisMatcher = thisPattern.matcher(matcher.group(3));
			while(thisMatcher.find())
			{
				sendMessage(new Message(message.getGroup(), thisMatcher.group(), "Plain Text"));
			}
		}
	}

}
