package bots.MVVBot;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.wrapper.Message;

@MetaClass(triggerPrefix = "?mvv ", helpTopic = "Christian")
public class MVVBot {

	@MetaMethod(trigger = "route ", helpTopic = "Any message will trigger a check whether Custom Parrots wish to reply to the message")
	public static Message checkRoute(String from, String to)
	{
		return new Message("", "Test", MessageFormat.PLAIN_TEXT);
	}
	
}
