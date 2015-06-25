package bridgempp.bot.wrapper;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import bridgempp.bot.messageformat.MessageFormat;

/**
 * BridgeMPP Message class containing following attributes String group The
 * group the Message originated from/will be sent to String sender The
 * sender of this Message, will be auto-set/overridden String target The
 * destination of this Message, will be auto-set/overridden String message
 * The raw text version of this Message String messageFormat The format in
 * which this Message has been sent
 */
public class Message {
	String group;
	private String sender;
	private String target;
	String message;
	private MessageFormat messageFormat;

	public Message() {

	}

	/**
	 * Reply to an existing Message (Does not send the message) Send the
	 * message with sendMessage(message)
	 * 
	 * @param message
	 *            The received Message to reply to
	 * @param text
	 *            The text of the reply Message
	 * @param format
	 *            The format of the reply Message
	 * @return The new Message, to be passed to sendMessage
	 */
	static Message replyTo(Message message, String text, MessageFormat format) {
		return new Message(message.getMessage(), text, format);
	}

	/**
	 * Check whether this Message violates BridgeMPP Message Restrictions
	 * Throws an exception which may or may not be caught at will
	 * 
	 * @throws Exception
	 *             The Reason for the invalidation of this message
	 */
	public void validate() throws Exception {
		if (getMessage().length() > 60000) {
			throw new Exception("Dangerous Message Length " + getMessage().length() + "! Send request rejected");
		}
		Matcher matcher = Pattern.compile("[\\x00-\\x08\\x0E-\\x1F]").matcher(getMessage());
		if (matcher.find()) {
			throw new Exception(
					"Dangerous Control Characters detected! Access Denied!\nAt position: " + matcher.start() + ", Character: " + URLEncoder.encode(getMessage().substring(matcher.start(), matcher.end()), "UTF-8") + "\nURL Encoded Original Message: "
							+ URLEncoder.encode(getMessage(), "UTF-8"));
		}
		if(messageFormat == MessageFormat.XHTML)
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			factory.setExpandEntityReferences(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new InputSource(new StringReader("<body>" + getMessage() + "</body>")));
		}
	}

	/**
	 * Generate a new Message to be sent over BridgeMPP
	 * 
	 * @param group
	 *            The group to which the Message will be sent (will usually
	 *            be retrieved from old message)
	 * @param message
	 *            The message that will be sent to the group
	 * @param messageFormat
	 *            The format of the Message ("PLAINTEXT", "XHTML")
	 */
	public Message(String group, String message, MessageFormat messageFormat) {
		this(group, "", "", message, messageFormat);
	}

	public Message(String group, String sender, String target, String message, MessageFormat messageFormat) {
		this.setGroup(group);
		this.setSender(sender);
		this.setTarget(target);
		this.setMessage(message);
		this.setMessageFormat(messageFormat);
	}

	public static Message parseMessage(String complexString) {
		Message message = new Message();
		String[] messageSplit = complexString.split("\\s*(?::| -->)\\s+", 5);
		if (messageSplit.length == 5) {
			message.setMessageFormat(MessageFormat.parseMessageFormat(messageSplit[0]));
			message.setGroup(messageSplit[1]);
			message.setSender(messageSplit[2]);
			message.setTarget(messageSplit[3]);
			message.setMessage(messageSplit[4]);
		} else {
			message.setMessage(complexString);
		}
		return message;
	}

	/**
	 * Generates an informative String representation of this Message
	 * 
	 * @return the String representation
	 */
	public String toComplexString() {
		String messageFormat = getMessageFormat() + ": ";
		String group = (getGroup() != null) ? (getGroup() + ": ") : "Direct Message: ";
		String sender = (getSender() != null) ? getSender().toString() : "Unknown";
		String target = (getTarget() != null) ? (getTarget().toString() + ": ") : ("Unknown: ");
		return messageFormat + group + sender + " --> " + target + getMessage();
	}

	@Override
	public String toString() {
		return toComplexString();
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Return the Message in the specified Format, or the Current Format if no Format is specified
	 * @return the message
	 */
    public String getMessage(MessageFormat... formats)
    {
    	for(MessageFormat format : formats)
    	{
    		if(messageFormat.canConvertToFormat(format))
    		{
    			return messageFormat.convertToFormat(message, format);
    		}
    	}
    	return message;
    }
    
    public String getPlainTextMessage()
    {
    	return getMessage(MessageFormat.PLAIN_TEXT);
    }

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the messageFormat
	 */
	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	/**
	 * @param messageFormat
	 *            the messageFormat to set
	 */
	public void setMessageFormat(MessageFormat messageFormat) {
		this.messageFormat = messageFormat;
	}

}