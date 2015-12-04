package bots.ParrsonRuntimeEnvironment;

import bridgempp.bot.database.PersistenceManager;
import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

@MetaClass(triggerPrefix = "", helpTopic = "Runtime Environment for executing Scripts in Java Script Engines")
public class ParrsonRuntimeEnvironmentBot
{
	private Bot bot;
	
	public void initializeBot(Bot metaBot)
	{
		this.bot = metaBot;
	}
	
	@MetaMethod(trigger="", helpTopic = "Receives incomming Messages to process them")
	public void messageReceived(Message message)
	{
		if(message.getPlainTextMessage().startsWith("?"))
		{
			return;
		}
		String interactive = InteractiveManager.messageReceived(message);
		if(interactive != null)
		{
			replyMessage(interactive, message);
		}
	}
	
	@MetaMethod(trigger ="?pre environment new ", helpTopic = "Create a new Runtime Environment")
	public String cmdNewRuntime(String name, String language, boolean persistent)
	{
		BindingContext context = new BindingContext(persistent);
		ScriptEngineEnvironment environment = PersistenceManager.getForCurrentThread().getFromPrimaryKey(ScriptEngineEnvironment.class, language);
		if(environment == null)
		{
			environment = new ScriptEngineEnvironment(language);
		}
		Runtime runtime = new Runtime(name, environment, context);
		PersistenceManager.getForCurrentThread().updateState(runtime);
		return "Created a new Runtime with name: " + name + " and language " + language;
	}
	
	@MetaMethod(trigger ="?pre environment delete ", helpTopic = "Delete an existing Runtime Environment and it's Bindings")
	public String cmdDeleteRuntime(String runtimeName)
	{
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, runtimeName);
		if(runtime == null)
		{
			return "Delete Failure: Runtime not found";
		}
		PersistenceManager.getForCurrentThread().removeState(runtime);
		return "Runtime " + runtime.toString() + " shredded.";
	}
	
	@MetaMethod(trigger ="?pre interactive ", helpTopic = "Enables interactivity for the Message Author and given Runtime")
	public String cmdInteractive(String runtimeName, Message message)
	{
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, runtimeName);
		if(runtime == null)
		{
			return "Interactive Failure: Runtime not found";
		}
		InteractiveManager.addInteractivity(message.getSender(), runtime);
		return "Welcome to " + runtime.toString() + ". Ready to Serve";
	}
	
	@MetaMethod(trigger ="?pre noninteractive", helpTopic = "Disables interactivity for the Message Author")
	public String cmdNonInteractive(Message message)
	{
		Runtime runtime = InteractiveManager.removeInteractive(message.getSender());
		if(runtime == null)
		{
			return "Interactive Failure: Sender is not in interactive Mode";
		}
		return "Thank you for using " + runtime.toString() + ". Hope to serve you soon.";
	}

	public void replyMessage(String sendMessage, Message message)
	{
		bot.sendMessage(new Message(message.getGroup(), sendMessage, MessageFormat.PLAIN_TEXT));
	}
}
