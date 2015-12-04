package bots.ParrsonRuntimeEnvironment;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import bridgempp.bot.metawrapper.MetaNotifyException;

public class ParrsonRuntimeManager
{
	private static ScriptEngineManager manager;
	
	
	public static ScriptEngine getScriptEngine(String scriptLanguage)
	{
		if(manager == null)
		{
			initializeManager();
		}
		ScriptEngine engineByName = manager.getEngineByName(scriptLanguage);
		if(engineByName == null)
		{
			throw new MetaNotifyException("No Script Engine found for language: " + scriptLanguage);
		}
		return engineByName;
	}
	
	public static void initializeManager()
	{
		manager = new ScriptEngineManager();
	}

}
