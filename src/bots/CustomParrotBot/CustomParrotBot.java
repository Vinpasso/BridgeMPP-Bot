package bots.CustomParrotBot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.wrapper.Bot;
import bridgempp.util.log.Log;

/**
 * Annotation to change Bot Prefix and Bot general help topic Bot reacts to
 * Strings starting with ?PLZ <method> <parameters>
 */
@MetaClass(triggerPrefix = "", helpTopic = "The Parrot cage for custom Parrots")
public class CustomParrotBot {
	private Hashtable<String, CustomParrot> table;
	private ScriptEngine scriptEngine;
	private Bot metaBot;

	public void initializeBot(Bot metaBot) {
		scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.metaBot = metaBot;
		if (metaBot.properties.containsKey("parrotlist")) {
			try {
				table = decodeList(metaBot.properties.get("parrotlist")
						.toString());
			} catch (Exception e) {
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
		while(objectInputStream.available() > 0)
		{
			CustomParrot parrot = (CustomParrot)objectInputStream.readObject();
			list.put(parrot.name, parrot);
		}
		objectInputStream.close();
		return list;
	}

	private String encodeList(Hashtable<String, CustomParrot> list) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		Enumeration<CustomParrot> elements = list.elements();
		while(elements.hasMoreElements())
		{
			objectOutputStream.writeObject(elements.nextElement());
		}
		objectOutputStream.close();
		return Base64.getEncoder().encodeToString(
				byteArrayOutputStream.toByteArray());
	}

	@MetaMethod(trigger = "", helpTopic = "Process the custom Parrots")
	public String processParrots(String message) {
		String result = "";
		Enumeration<CustomParrot> elements = table.elements();
		while (elements.hasMoreElements()) {
			CustomParrot parrot = elements.nextElement();
			if(!parrot.active)
			{
				continue;
			}
			scriptEngine.put("parrotName", parrot.name);
			scriptEngine.put("message", message);
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
			}
		}
		return result.trim();
	}

	/**
	 * @return The Bot's message result
	 */
	@MetaMethod(trigger = "?parrot custom new ", helpTopic = "Create a new Custom Parrot")
	public String newCustomParrot(String name, String condition,
			String operation) {
		CustomParrot parrot = new CustomParrot(name, condition, operation);
		table.put(parrot.name, parrot);
		saveList();
		return "Created new custom Parrot: " + parrot.name;
	}
	
	@MetaMethod(trigger = "?parrot custom mute ", helpTopic = "Mute a Custom Parrot")
	public String muteCustomParrot(String name)
	{
		table.get(name).active = false;
		saveList();
		return "Parrot " + name + " muted.";
	}
	
	@MetaMethod(trigger = "?parrot custom unmute ", helpTopic = "Unmute a Custom Parrot")
	public String unmuteCustomParrot(String name)
	{
		table.get(name).active = true;
		saveList();
		return "Parrot " + name + " unmuted.";
	}

	private void saveList() {
		try {
			metaBot.properties.put("parrotlist", encodeList(table));
			metaBot.saveProperties();
		} catch (Exception e) {
			Log.log(Level.SEVERE, "Failed to save List!", e);
		}
	}

	class CustomParrot implements Serializable {
		private static final long serialVersionUID = -1431976079784905003L;
		/**
		 * 
		 */
		boolean active;
		String condition;
		String operation;
		String name;

		public CustomParrot(String name, String condition, String operation) {
			this(true, name, condition, operation);
		}
		
		public CustomParrot(boolean active, String name, String condition, String operation)
		{
			this.name = name;
			this.condition = condition;
			this.operation = operation;
			this.active = true;
		}
		
		public String toString()
		{
			return "Parrot: " + name + ": " + condition + ": " + operation;
		}
	}
}
