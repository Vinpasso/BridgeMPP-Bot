package bots.CustomParrotBot;

import java.io.Serializable;

import bridgempp.util.Util;

public class CustomParrot implements Serializable {
	private static final long serialVersionUID = -1431976079784905003L;
	/**
	 * 
	 */
	private boolean active;
	private String condition;
	private String operation;
	private String name;
	private boolean canNerf = true;
	private int reputation = 0;
	private long birthday;

	public CustomParrot(String name, String condition, String operation) {
		this(true, name, condition, operation, System.currentTimeMillis());
	}
	
	public CustomParrot(boolean active, String name, String condition, String operation, long birthday)
	{
		this.setName(name);
		this.setCondition(condition);
		this.setOperation(operation);
		this.setActive(true);
		this.setBirthday(birthday);
	}
	
	public String toString()
	{
		return "Custom Parrot: Name: " + getName() + " Active: " + isActive() + " Age: " + Util.timeDeltaNow(getBirthday()) + " Nerfable: " + canNerf() + " Reputation: " + getReputation();
	}
	
	public void increaseReputation()
	{
		setReputation(getReputation() + 1);
	}

	public void decreaseReputation(int numParrots)
	{
		setReputation(getReputation() - numParrots);
	}
	
	/**
	 * @param reputation the reputation to set
	 */
	void setReputation(int reputation)
	{
		this.reputation = Math.max(-1000, Math.min(1000, reputation));
	}
	

	/**
	 * @return the birthday
	 */
	public long getBirthday()
	{
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(long birthday)
	{
		this.birthday = birthday;
	}

	/**
	 * @return the reputation
	 */
	int getReputation()
	{
		return reputation;
	}



	/**
	 * @return the canNerf
	 */
	boolean canNerf()
	{
		return canNerf;
	}

	/**
	 * @param canNerf the canNerf to set
	 */
	void setCanNerf(boolean canNerf)
	{
		this.canNerf = canNerf;
	}

	/**
	 * @return the name
	 */
	String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the operation
	 */
	String getOperation()
	{
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	void setOperation(String operation)
	{
		this.operation = operation;
	}

	/**
	 * @return the condition
	 */
	String getCondition()
	{
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	void setCondition(String condition)
	{
		this.condition = condition;
	}

	/**
	 * @return the active
	 */
	boolean isActive()
	{
		return active;
	}

	/**
	 * @param active the active to set
	 */
	void setActive(boolean active)
	{
		this.active = active;
	}

}