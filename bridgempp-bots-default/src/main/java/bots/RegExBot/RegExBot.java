package bots.RegExBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class RegExBot extends Bot {

	private Pattern pattern;

	@Override
	public void initializeBot() {
		pattern = Pattern.compile("\\s*(.+?)\\s+(.+)");
	}

	@Override
	public void messageReceived(Message message) {
		if (message.getMessage().toLowerCase().indexOf("?regex") > -1) {
			String[] regexTriggers = message.getMessage().split("\\?regex");
			for (int i = 1; i < regexTriggers.length; i++) {
				String triggerMessage = regexTriggers[i];
				try {
					Matcher matcher = pattern.matcher(triggerMessage);
					if (!matcher.find()) {
						sendMessage(new Message(
								message.getGroup(),
								"Not enough Arguments: Usage ?regex <regex> <matchstring>. The Regex can not contain Whitespaces",
								MessageFormat.PLAIN_TEXT));
						continue;
					}
					Pattern thisPattern = Pattern.compile(matcher.group(1));
					Matcher thisMatcher = thisPattern.matcher(matcher.group(2));
					while (thisMatcher.find()) {
						sendMessage(new Message(message.getGroup(),
								thisMatcher.group(), MessageFormat.PLAIN_TEXT));
					}
				} catch (PatternSyntaxException e) {
					sendMessage(new Message(message.getGroup(),
							"Invalid Regex Syntax:\n" + e.getMessage(),
							MessageFormat.PLAIN_TEXT));
				}
			}
		}
	}

}
