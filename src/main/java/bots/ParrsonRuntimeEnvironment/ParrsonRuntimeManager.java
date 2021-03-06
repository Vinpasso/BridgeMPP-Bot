package bots.ParrsonRuntimeEnvironment;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import fr.x9c.cadmium.support.scripting.OCamlScriptEngineFactory;
import bridgempp.bot.metawrapper.MetaNotifyException;

public class ParrsonRuntimeManager
{
	private static ScriptEngineManager manager;
	
	static
	{
		initializeManager();
	}
	
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

	public static List<ScriptEngineFactory> listEngineFactories()
	{
		return manager.getEngineFactories();
	}
	
	/**
	 * Retained to ensure compiler References
	 */
	public static void registerDependencies()
	{
		manager.registerEngineName("OCaml", new OCamlScriptEngineFactory());
	}
}
