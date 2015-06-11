package bots.CustomParrotBot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.metawrapper.MetaParameter;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import bridgempp.util.Log;
import bridgempp.util.Util;

/**
 * Annotation to change Bot Prefix and Bot general help topic Bot reacts to
 * Strings starting with ?PLZ <method> <parameters>
 */
@MetaClass(triggerPrefix = "", helpTopic = "The Parrot cage for custom Parrots\nA persistent Parrot Cage powered by JAVASCRIPT Parrots")
public class CustomParrotBot {
	private Hashtable<String, CustomParrot> table;
	private ScriptEngine scriptEngine;
	private Bot metaBot;
	private boolean debugParrots = false;

	public void initializeBot(Bot metaBot) {
		scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.metaBot = metaBot;
		if (metaBot.properties.containsKey("parrotlist")) {
			try {
				table = decodeList(metaBot.properties.get("parrotlist")
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
				table = new Hashtable<>();
			}
		} else {
			table = new Hashtable<>();
		}
	}

	private Hashtable<String, CustomParrot> decodeList(String base64String)
			throws Exception {
		byte[] objectData = Base64.getDecoder().decode(base64String);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(objectData));
		Hashtable<String, CustomParrot> list = new Hashtable<String, CustomParrot>();
		int length = objectInputStream.readInt();
		for(int i= 0; i < length; i++) {
			CustomParrot parrot = (CustomParrot) objectInputStream.readObject();
			list.put(parrot.name, parrot);
		}
		objectInputStream.close();
		return list;
	}

	private String encodeList(Hashtable<String, CustomParrot> list)
			throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		objectOutputStream.writeInt(list.size());
		Enumeration<CustomParrot> elements = list.elements();
		while (elements.hasMoreElements()) {
			objectOutputStream.writeObject(elements.nextElement());
		}
		objectOutputStream.close();
		return Base64.getEncoder().encodeToString(
				byteArrayOutputStream.toByteArray());
	}

	@MetaMethod(trigger = "", helpTopic = "Any message will trigger a check whether Custom Parrots wish to reply to the message")
	public String processParrots(Message message) {
		String result = "";
		Enumeration<CustomParrot> elements = table.elements();
		while (elements.hasMoreElements()) {
			CustomParrot parrot = elements.nextElement();
			if (!parrot.active) {
				continue;
			}
			scriptEngine.put("parrotName", parrot.name);
			scriptEngine.put("author", message.getSender());
			scriptEngine.put("group", message.getGroup());
			scriptEngine.put("message", message.getPlainTextMessage());
			try {
				String conditionResult = scriptEngine.eval(parrot.condition)
						.toString();
				if (!conditionResult.equalsIgnoreCase("true")) {
					continue;
				}
				String parrotResult = scriptEngine.eval(parrot.operation)
						.toString();
				result += parrot.name + ": " + parrotResult + "\n";
			} catch (ScriptException e) {
				result += parrot.name + " is rapidly loosing sanity...";
				Log.log(Level.WARNING, "Parrot " + parrot.name
						+ " Script Failure", e);
				if(debugParrots)
				{
					result += e.toString() + " at " + e.getFileName() + ":" + e.getLineNumber();
				}
			}
		}
		return result.trim();
	}

	/**
	 * @return The Bot's message result
	 */
	@MetaMethod(trigger = "?parrot custom new ", helpTopic = "Create a new Javascrip powered Custom Parrot and load it into the Custom Parrot Cage")
	public String newCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot") String name,
			@MetaParameter(helpTopic = "The Javascript code to determine whether a Parrot wants to reply to this message. Should evaluate to either true or false") String condition,
			@MetaParameter(helpTopic = "The Javascript code to formulate a Parrot Message. A String representation of the returned Object will be sent in the message") String operation) {
		CustomParrot parrot = new CustomParrot(name, condition, operation);
		table.put(parrot.name.toLowerCase(), parrot);
		saveList();
		return "It is " + Util.currentTimeAndDate() + ". Let it be known that Parrot " + parrot.name + " has been created";
	}
	
	@MetaMethod(trigger = "?parrot custom delete ", helpTopic = "Remove a Custom Parrot from the Custom Parrot Cage")
	public String removeCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot") String name)
			{
		CustomParrot parrot = table.remove(name.toLowerCase());
		saveList();
		return "It is " + Util.currentTimeAndDate() + ". Let it be known that Parrot " + parrot.name + " has deceased at the age of " + Util.timeDeltaNow(parrot.birthday) + ". Long may he be remembered.";
	}

	@MetaMethod(trigger = "?parrot custom mute ", helpTopic = "Mute a Custom Parrot so that it will no longer respond to messages while it is muted")
	public String muteCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot to Mute") String name) {
		table.get(name.toLowerCase()).active = false;
		saveList();
		return "Parrot " + name + " has been gagged.";
	}

	@MetaMethod(trigger = "?parrot custom unmute ", helpTopic = "Unmute a Custom Parrot so that it will resume responding to messages while it is active")
	public String unmuteCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot to Mute") String name) {
		table.get(name.toLowerCase()).active = true;
		saveList();
		return "Parrot " + name + " has been ungagged.";
	}

	@MetaMethod(trigger = "?parrot custom list", helpTopic = "List all the Parrots, including their Conditions and Operators")
	public String listCustomParrots() {
		return table.toString();
	}

	@MetaMethod(trigger = "?parrot custom set name ", helpTopic="Overwrite a Parrots name and set a new one")
	public String setNameCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String oldName, @MetaParameter(helpTopic="The new name of the Parrot")String newName)
	{
		CustomParrot parrot = table.remove(oldName.toLowerCase());
		parrot.name = newName;
		table.put(parrot.name.toLowerCase(), parrot);
		saveList();
		return "Parrot " + oldName + " is now known as " + newName;
	}

	@MetaMethod(trigger = "?parrot custom debug ", helpTopic="Set the debug status of the Parrot Cage (true: Print Errors, false: Ignore Errors")
	public String setDebugStatus(@MetaParameter(helpTopic="The new debug status") boolean debug)
	{
		this.debugParrots = debug;
		return debug?"The bugs feel threatened as they start to feel the effects of the debug spray":"The bugs crawl back under he feathers of their parrot";
	}
	
	private void saveList() {
		try {
			metaBot.properties.put("parrotlist", encodeList(table));
			metaBot.saveProperties();
		} catch (Exception e) {
			Log.log(Level.SEVERE, "Failed to save List!", e);
		}
	}
}
