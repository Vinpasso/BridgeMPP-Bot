package bots.ParrsonRuntimeEnvironment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "SCRIPT")
public class Script
{
	@Id()
	@Column(name = "NAME", nullable = false, length = 255)
	private String name;
	
	@Column(name = "LANGUAGE", nullable = false, length = 255)
	private String language;
	
	@Column(name = "CODE", nullable = false)
	@Lob
	private String script;
	
	
	public Script(String name, String language, String script)
	{
		this.name = name;
		this.language = language;
		this.script = script;
	}
	
	
	/**
	 * JPA-Constructor
	 */
	protected Script()
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
		return language;
	}


	/**
	 * @return the script
	 */
	public String getScript()
	{
		return script;
	}
	
}
