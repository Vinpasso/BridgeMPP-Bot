package bots.ParrsonRuntimeEnvironment;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

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

	@MetaMethod(trigger = "", helpTopic = "Receives incomming Messages to process them")
	public void messageReceived(Message message)
	{
		if (message.getPlainTextMessage().startsWith("?"))
		{
			return;
		}
		String interactive = InteractiveManager.messageReceived(message);
		if (interactive != null)
		{
			replyMessage(interactive, message);
		}
	}

	@MetaMethod(trigger = "?pre environment new ", helpTopic = "Create a new Runtime Environment")
	public String cmdNewRuntime(String name, String language, boolean persistent)
	{
		BindingContext context = new BindingContext(persistent);
		ScriptEngineEnvironment environment = PersistenceManager.getForCurrentThread().getFromPrimaryKey(ScriptEngineEnvironment.class, language);
		if (environment == null)
		{
			environment = new ScriptEngineEnvironment(language);
		}
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, name);
		if (runtime != null)
		{
			return "Create Failure: Runtime already exists";
		}
		runtime = new Runtime(name, environment, context);
		PersistenceManager.getForCurrentThread().updateState(runtime);
		return "Created a new Runtime with name: " + name + " and language " + language;
	}

	@MetaMethod(trigger = "?pre environment delete ", helpTopic = "Delete an existing Runtime Environment and it's Bindings")
	public String cmdDeleteRuntime(String runtimeName)
	{
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, runtimeName);
		if (runtime == null)
		{
			return "Delete Failure: Runtime not found";
		}
		PersistenceManager.getForCurrentThread().removeState(runtime);
		return "Runtime " + runtime.toString() + " shredded.";
	}

	@MetaMethod(trigger = "?pre exec ", helpTopic = "Execute a statement in the provided runtime.")
	public String cmdExecRuntime(String runtimeName, String command)
	{
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, runtimeName);
		if (runtime == null)
		{
			return "Exec: Runtime not found";
		}
		try
		{
			Object result = runtime.execute(command);
			if (result == null)
			{
				return "No Output";
			}
			return result.toString();
		} catch (ScriptException e)
		{
			return "Encountered Exception: " + e.toString();
		}
	}

	@MetaMethod(trigger = "?pre interactive ", helpTopic = "Enables interactivity for the Message Author and given Runtime")
	public String cmdInteractive(String runtimeName, Message message)
	{
		Runtime runtime = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Runtime.class, runtimeName);
		if (runtime == null)
		{
			return "Interactive Failure: Runtime not found";
		}
		InteractiveManager.addInteractivity(message.getSender(), runtime);
		return "Welcome to " + runtime.toString() + ". Ready to Serve";
	}

	@MetaMethod(trigger = "?pre noninteractive", helpTopic = "Disables interactivity for the Message Author")
	public String cmdNonInteractive(Message message)
	{
		Runtime runtime = InteractiveManager.removeInteractive(message.getSender());
		if (runtime == null)
		{
			return "Interactive Failure: Sender is not in interactive Mode";
		}
		return "Thank you for using " + runtime.toString() + ". Hope to serve you soon.";
	}
	
	@MetaMethod(trigger = "?pre list runtimes", helpTopic = "List loaded Parrson Runtime Environments")
	public String listRuntimes()
	{
		Collection<Runtime> runtimes = PersistenceManager.getForCurrentThread().getAll(Runtime.class);
		StringBuilder listResult = new StringBuilder();
		runtimes.forEach(r -> { listResult.append(r.getName() + " (" + r.getLanguage() + ")\n"); });
		return "Listing currently loaded Parrson Runtime Environments:\n" + listResult.toString() + "Listed " + runtimes.size() + " Runtimes";
	}

	@MetaMethod(trigger = "?pre list languages", helpTopic = "Disables interactivity for the Message Author")
	public String cmdListLanguages(Message message)
	{
		String result = "Parrson Runtime Environment serves in the following languages:\n";
		List<ScriptEngineFactory> factories = ParrsonRuntimeManager.listEngineFactories();
		Iterator<ScriptEngineFactory> iterator = factories.iterator();
		while (iterator.hasNext())
		{
			ScriptEngineFactory engineFactory = iterator.next();
			result += engineFactory.getLanguageName() + " (" + engineFactory.getLanguageVersion() + "): " + engineFactory.getEngineName() + " (" + engineFactory.getEngineVersion() + ")\n";
		}
		result += "Additional languages may be added by placing the Scripting Jar on the Classpath";
		return result;
	}

	public void replyMessage(String sendMessage, Message message)
	{
		bot.sendMessage(new Message(message.getGroup(), sendMessage, MessageFormat.PLAIN_TEXT));
	}
}
