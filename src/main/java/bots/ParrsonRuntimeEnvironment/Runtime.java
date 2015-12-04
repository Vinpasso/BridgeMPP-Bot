package bots.ParrsonRuntimeEnvironment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.script.ScriptException;

@Entity()
public class Runtime
{
	@Id()
	@Column(name = "Name", nullable = false, length = 255)
	private String name;
	
	
	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private BindingContext context;
	
	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, optional = false)
	private ScriptEngineEnvironment environment;

	public Runtime(String name, ScriptEngineEnvironment environment, BindingContext context)
	{
		this.name = name;
		this.environment = environment;
		this.context = context;
	}
	
	/**
	 * JPA Constructor
	 */
	protected Runtime()
	{}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return environment.getLanguage();
	}

	public Object execute(String statement) throws ScriptException
	{
		Object result = environment.executeStatement(statement, context);
		context.saveBindings();
		return result;
	}
	
	public String toString()
	{
		return getName() + " (" + getLanguage() + ")";
	}
}
