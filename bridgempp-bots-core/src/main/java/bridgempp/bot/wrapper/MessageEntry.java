package bridgempp.bot.wrapper;

import bridgempp.message.Message;

public class MessageEntry implements Comparable<MessageEntry>
{
	private long creationTime;
	private Message message;
	private Bot bot;
	
	
	public MessageEntry(Message message, Bot bot)
	{
		this.message = message;
		this.bot = bot;
		this.creationTime = System.currentTimeMillis();
	}


	/**
	 * @return the creationTime
	 */
	long getCreationTime()
	{
		return creationTime;
	}


	/**
	 * @return the message
	 */
	Message getMessage()
	{
		return message;
	}


	/**
	 * @return the bot
	 */
	Bot getBot()
	{
		return bot;
	}


	@Override
	public int compareTo(MessageEntry o)
	{
		if(o == null) return 1;
		return (int) (getPriority() - o.getPriority());
	}


	private long getPriority()
	{
		return getCreationTime() + bot.getProcessingTime();
	}
	
}
