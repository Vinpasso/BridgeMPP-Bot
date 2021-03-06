package bots.CustomParrotBot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.metawrapper.MetaNotifyException;
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

	@MetaMethod(trigger = "", helpTopic = "Any message will trigger a check whether Custom Parrots wish to reply to the message")
	public String processParrots(Message message) {
		String result = "";
		String lowerCaseMessage = message.getPlainTextMessage().toLowerCase();
		Enumeration<CustomParrot> elements = table.elements();
		while (elements.hasMoreElements()) {
			CustomParrot parrot = elements.nextElement();
			if (!parrot.isActive()) {
				continue;
			}
			if(parrot.getReputation() < 0 && ThreadLocalRandom.current().nextInt(-1000, 0) > parrot.getReputation())
			{
				parrot.increaseReputation();
				continue;
			}
			scriptEngine.put("parrotName", parrot.getName());
			scriptEngine.put("author", message.getSender());
			scriptEngine.put("group", message.getGroup());
			scriptEngine.put("message", message.getPlainTextMessage());
			scriptEngine.put("lowerCaseMessage", lowerCaseMessage);
			try {
				String conditionResult = scriptEngine.eval(parrot.getCondition())
						.toString();
				if (!conditionResult.equalsIgnoreCase("true")) {
					parrot.increaseReputation();
					continue;
				}
				Object operationResult = scriptEngine.eval(parrot.getOperation());
				if(operationResult == null)
				{
					parrot.increaseReputation();
					continue;
				}
				String parrotResult = operationResult.toString();
				result += parrot.getName() + ": " + parrotResult + "\n";
				parrot.decreaseReputation(table.size());
			} catch (ScriptException e) {
				result += parrot.getName() + " is rapidly loosing sanity...";
				Log.log(Level.WARNING, "Parrot " + parrot.getName()
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
	@MetaMethod(trigger = "?parrot custom new ", helpTopic = "Create a new Javascript powered Custom Parrot and load it into the Custom Parrot Cage")
	public String newCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot") String name,
			@MetaParameter(helpTopic = "The Javascript code to determine whether a Parrot wants to reply to this message. Should evaluate to either true or false") String condition,
			@MetaParameter(helpTopic = "The Javascript code to formulate a Parrot Message. A String representation of the returned Object will be sent in the message") String operation) {
		CustomParrot parrot = new CustomParrot(name, condition, operation);
		table.put(parrot.getName().toLowerCase(), parrot);
		saveList();
		return "It is " + Util.currentTimeAndDate() + ". Let it be known that Parrot " + parrot.getName() + " has been created";
	}
	
	@MetaMethod(trigger = "?parrot custom delete ", helpTopic = "Remove a Custom Parrot from the Custom Parrot Cage")
	public String removeCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot") String name)
			{
		CustomParrot parrot = table.remove(name.toLowerCase());
		saveList();
		if(parrot == null)
		{
			return "The parrot could not be found while searching the Parrot Cage";
		}
		return "It is " + Util.currentTimeAndDate() + ". Let it be known that Parrot " + parrot.getName() + " has deceased at the age of " + Util.timeDeltaNow(parrot.getBirthday()) + ". Long may he be remembered.";
	}

	@MetaMethod(trigger = "?parrot custom mute ", helpTopic = "Mute a Custom Parrot so that it will no longer respond to messages while it is muted")
	public String muteCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot to Mute") String name) {
		CustomParrot parrot = getParrot(name);
		parrot.setActive(false);
		saveList();
		return "Parrot " + name + " has been gagged.";
	}

	@MetaMethod(trigger = "?parrot custom unmute ", helpTopic = "Unmute a Custom Parrot so that it will resume responding to messages while it is active")
	public String unmuteCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot to Mute") String name) {
		CustomParrot parrot = getParrot(name);
		parrot.setActive(true);
		saveList();
		return "Parrot " + name + " has been ungagged.";
	}
	
	@MetaMethod(trigger = "?parrot custom print ", helpTopic = "Print a custom Parrot's fields. Requires a Parrot Name")
	public String printCustomParrot(
			@MetaParameter(helpTopic = "The name of the Parrot to Print") String name) {
		CustomParrot parrot = getParrot(name);
		return "Parrot " + name + " has been sent to the Printing Press.\n" + parrot.toString();
	}

	private CustomParrot getParrot(String name) {
		CustomParrot customParrot = table.get(name.toLowerCase());
		if(customParrot == null)
		{
			throw new MetaNotifyException("The parrot could not be found while searching the Parrot Cage");
		}
		return customParrot;
	}

	@MetaMethod(trigger = "?parrot custom list", helpTopic = "List all the Parrots, including their Conditions and Operators")
	public String listCustomParrots() {
		StringBuilder builder = new StringBuilder();
		table.forEach((e, v) -> builder.append(v.toString() + "\n"));
		return builder.toString().trim();
	}

	@MetaMethod(trigger = "?parrot custom set name ", helpTopic="Overwrite a Parrots name and set a new one")
	public String setNameCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String oldName, @MetaParameter(helpTopic="The new name of the Parrot")String newName)
	{
		getParrot(oldName);
		CustomParrot parrot = table.remove(oldName.toLowerCase());
		parrot.setName(newName);
		table.put(parrot.getName().toLowerCase(), parrot);
		saveList();
		return "Parrot " + oldName + " is now known as " + newName;
	}

	@MetaMethod(trigger = "?parrot custom set condition ", helpTopic="Overwrite a Parrots condition and set a new one")
	public String setConditionCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String name, @MetaParameter(helpTopic="The new condition of the Parrot")String newcondition)
	{
		CustomParrot parrot = getParrot(name);
		parrot.setCondition(newcondition);
		saveList();
		return "Parrot " + name + " has had brain surgery";
	}
	
	@MetaMethod(trigger = "?parrot custom set operation ", helpTopic="Overwrite a Parrots operation and set a new one")
	public String setOperationCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String name, @MetaParameter(helpTopic="The new operation of the Parrot")String newOperation)
	{
		CustomParrot parrot = getParrot(name);
		parrot.setOperation(newOperation);
		saveList();
		return "Parrot " + name + " has had tongue surgery";
	}
	
	@MetaMethod(trigger = "?parrot custom get operation ", helpTopic="Get a parrot's current operation code")
	public String getOperationCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String name)
	{
		CustomParrot parrot = getParrot(name);
		return "Parrot operation operation " + name + ": " + parrot.getOperation();
	}
	
	@MetaMethod(trigger = "?parrot custom get condition ", helpTopic="Get a parrot's current condition code")
	public String getConditionCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String name)
	{
		CustomParrot parrot = getParrot(name);
		return "Parrot condition code " + name + ": " + parrot.getCondition();
	}

	
	@MetaMethod(trigger = "?parrot custom set nerf ", helpTopic="Set whether the Parrot will be nerfed according to reputation")
	public String setNerfCustomParrot(@MetaParameter(helpTopic="The current name of the Parrot")String name, @MetaParameter(helpTopic="Whether to nerf the parrot when it has low reputation")boolean nerf)
	{
		CustomParrot parrot = getParrot(name);
		parrot.setCanNerf(nerf);
		saveList();
		return "Parrot " + name + (nerf?" will now be nerfed according to reputation":" is now unaffected by reputation");
	}
	
	
	@MetaMethod(trigger = "?parrot custom debug ", helpTopic="Set the debug status of the Parrot Cage (true: Print Errors, false: Ignore Errors")
	public String setDebugStatus(@MetaParameter(helpTopic="The new debug status") boolean debug)
	{
		this.debugParrots = debug;
		return debug?"The bugs feel threatened as they start to feel the effects of the debug spray":"The bugs crawl back under he feathers of their parrot";
	}
	
	@MetaMethod(trigger = "?parrot custom emergency brake", helpTopic="halt all Parrots using setActive in an emergency")
	public String emergencyBrake()
	{
		Enumeration<CustomParrot> enumerator = table.elements();
		while(enumerator.hasMoreElements())
		{
			enumerator.nextElement().setActive(false);
		}
		saveList();
		return "The Parrot Cage screeches to a halt!";
	}
	
	@MetaMethod(trigger = "?parrot custom emergency start", helpTopic="Restart all Parrots using setActive in an emergency")
	public String emergencyStart()
	{
		Enumeration<CustomParrot> enumerator = table.elements();
		while(enumerator.hasMoreElements())
		{
			enumerator.nextElement().setActive(true);
		}
		saveList();
		return "The Parrot Cage signals full steam ahead!";
	}
	
	@MetaMethod(trigger = "?parrot custom emergency nerf", helpTopic="Nerf all Parrots using reputation in an emergency")
	public String emergencyNerf()
	{
		Enumeration<CustomParrot> enumerator = table.elements();
		while(enumerator.hasMoreElements())
		{
			enumerator.nextElement().setCanNerf(true);
		}
		saveList();
		return "The Parrot Cage signals green light for nerf avenue!";
	}
	
	public void optimizeParrots()
	{
		Iterator<CustomParrot> parrots = table.values().iterator();
		while(parrots.hasNext())
		{
			ParrotOptimization.applyOptimizations(parrots.next());
		}
		saveList();
	}
	
	private void saveList() {
		try {
			table.forEach((n, p) -> {
				ParrotOptimization.applyOptimizations(p);
			});
			metaBot.properties.put("parrotlist", encodeList(table));
			metaBot.saveProperties();
		} catch (Exception e) {
			Log.log(Level.SEVERE, "Failed to save List!", e);
		}
	}
	
	private Hashtable<String, CustomParrot> decodeList(String base64String)
			throws IOException, ClassNotFoundException {
		byte[] objectData = Base64.getDecoder().decode(base64String);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(objectData));
		Hashtable<String, CustomParrot> list = new Hashtable<String, CustomParrot>();
		int length = objectInputStream.readInt();
		for(int i= 0; i < length; i++) {
			CustomParrot parrot = (CustomParrot) objectInputStream.readObject();
			list.put(parrot.getName().toLowerCase(), parrot);
		}
		objectInputStream.close();
		return list;
	}

	private String encodeList(Hashtable<String, CustomParrot> list)
			throws IOException {
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
	
	public void deinitializeBot(Bot metaBot)
	{
		saveList();
	}

}
