package bridgempp.bot.metawrapper;

@MetaClass(triggerPrefix="?PLZ ", helpTopic="PLZ give more help PLZ")
public class MetaAnnotateTest {
	
	@MetaMethod(trigger="", helpTopic="PLZ String as Parameter PLZ")
	public String giveMeMoarPlz(@MetaParameter(helpTopic="INPUT PLZ") String input)
	{
		return input.replaceAll(" [a-z]*? ", " PLZ ");
	}
}
