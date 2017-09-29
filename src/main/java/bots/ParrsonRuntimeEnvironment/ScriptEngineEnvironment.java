package bots.ParrsonRuntimeEnvironment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

@Entity()
public class ScriptEngineEnvironment
{
	@Id
	@Column(name = "Language", nullable = false, length = 255)
	private String scriptLanguage;

	private transient ScriptEngine engine;

	public ScriptEngineEnvironment(String language)
	{
		scriptLanguage = language;
	}

	protected ScriptEngineEnvironment()
	{
	}

	public Object executeStatement(String statement, BindingContext bindings) throws ScriptException
	{
		if (engine == null)
		{
			engine = ParrsonRuntimeManager.getScriptEngine(scriptLanguage);
		}
		return engine.eval(statement, bindings.asBindingContext());
	}

	public String getLanguage()
	{
		return scriptLanguage;
	}
}
