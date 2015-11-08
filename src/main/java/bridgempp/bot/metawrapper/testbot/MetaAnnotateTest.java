package bridgempp.bot.metawrapper.testbot;

import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.metawrapper.MetaParameter;


/**
 * Annotation to change Bot Prefix and Bot general help topic
 * Bot reacts to Strings starting with ?PLZ <method> <parameters>
 */
@MetaClass(triggerPrefix="?PLZ ", helpTopic="PLZ give more help PLZ")
public class MetaAnnotateTest {
	
	/**
	 * @param input The request passed to the Bot
	 * @return The Bot's message result
	 */
	@MetaMethod(trigger="", helpTopic="PLZ String as Parameter PLZ")
	public String giveMeMoarPlz(@MetaParameter(helpTopic="INPUT PLZ") String input)
	{
		return input.replaceAll(" [a-z]*? ", " PLZ ");
	}
}
