package bots.CustomParrotBot;

import java.io.Serializable;

public class CustomParrot implements Serializable {
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