package bridgempp.bot.wrapper;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

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
	private String messageFormat;

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
	static Message replyTo(Message message, String text, String format) {
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
		if (Pattern.compile("[\\x00-\\x08|\\x0E-\\x1F]").matcher(getMessage()).find()) {
			throw new Exception(
					"Dangerous Control Characters detected! Access Denied!\nURL Encoded Original Message: "
							+ URLEncoder.encode(getMessage(), "UTF-8"));
		}
		switch (messageFormat) {
		case "XHTML":
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			factory.setExpandEntityReferences(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new InputSource(new StringReader("<body>" + getMessage() + "</body>")));
			break;
		default:
			break;
		}
	}

	@Deprecated
	public Message(String sender, String message) {
		this("", sender, "", message, "Plain Text");
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
	public Message(String group, String message, String messageFormat) {
		this(group, "", "", message, messageFormat);
	}

	public Message(String group, String sender, String target, String message, String messageFormat) {
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
			message.setMessageFormat(messageSplit[0]);
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
	 * @return the message
	 */
	public String getMessage() {
		return message;
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
	public String getMessageFormat() {
		return messageFormat;
	}

	/**
	 * @param messageFormat
	 *            the messageFormat to set
	 */
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

}