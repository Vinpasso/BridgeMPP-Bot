package bots.CustomParrotBot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.istack.internal.logging.Logger;

import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.wrapper.Bot;
import bridgempp.util.log.Log;


/**
 * Annotation to change Bot Prefix and Bot general help topic
 * Bot reacts to Strings starting with ?PLZ <method> <parameters>
 */
@MetaClass(triggerPrefix="", helpTopic="The Parrot cage for custom Parrots")
public class CustomParrotBot {
	private LinkedList<CustomParrot> list;
	private ScriptEngine scriptEngine;
	private Bot metaBot;

	public void initializeBot(Bot metaBot)
	{
		scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		this.metaBot = metaBot;
		if(metaBot.properties.containsKey("parrotlist"))
		{
			try {
				list = decodeList(metaBot.properties.get("parrotlist").toString());
			} catch (Exception e) {
				list = new LinkedList<>();
			}
		}
		else
		{
			list = new LinkedList<>();
		}
	}


	private LinkedList<CustomParrot> decodeList(String base64String) throws Exception {
		byte[] objectData = Base64.getDecoder().decode(base64String);
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(objectData));
		LinkedList<CustomParrot> list = (LinkedList<CustomParrot>) objectInputStream.readObject();
		objectInputStream.close();
		return list;
	}

	private String encodeList(LinkedList<CustomParrot> list) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
		objectOutputStream.writeObject( list );
		objectOutputStream.close();
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()); 
	}


	@MetaMethod(trigger="", helpTopic="Process the custom Parrots")
	public String processParrots(String message) throws ScriptException
	{
		String result = "";
		Iterator<CustomParrot> iterator = list.iterator();
		while(iterator.hasNext())
		{
			CustomParrot parrot = iterator.next();
			String conditionResult = scriptEngine.eval(parrot.condition).toString();
			if(!conditionResult.equalsIgnoreCase("true"))
			{
				continue;
			}
			String parrotResult = scriptEngine.eval(parrot.operation).toString();
			result += parrot.name + ": " + parrotResult + "\n";
		}
		return result.trim();
	}

	/**
	 * @return The Bot's message result
	 */
	@MetaMethod(trigger="?parrot custom new ", helpTopic="Create a new Custom Parrot")
	public String newCustomParrot(String name, String condition, String operation)
	{
		CustomParrot parrot = new CustomParrot(name, condition, operation);
		list.add(parrot);
		saveList();
		return "Created new custom Parrot: " + parrot.name;
	}

	private void saveList()
	{
		try
		{
			metaBot.properties.put("parrotlist", encodeList(list));
			metaBot.saveProperties();
		}
		catch(Exception e)
		{
			Log.log(Level.SEVERE, "Failed to save List!", e);
		}
	}

	class CustomParrot
	{
		String condition;
		String operation;
		String name;
		public CustomParrot(String name, String condition, String operation) {
			this.name = name;
			this.condition = condition;
			this.operation = operation;
		}
	}
}
