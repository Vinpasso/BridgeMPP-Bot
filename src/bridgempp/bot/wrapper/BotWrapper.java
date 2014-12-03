package bridgempp.bot.wrapper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 *
 * @author Vincent Bode
 */
public class BotWrapper {


	static Selector selector;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			selector = Selector.open();
			File botsDir = new File("bots/");
			if(!botsDir.exists())
			{
				botsDir.mkdir();
				Properties exampleBotProperties = new Properties();
				writeDefaultConfig(exampleBotProperties);
				exampleBotProperties.store(new FileOutputStream("bots/exampleBot.config"), "Bot Wrapper Configuration");
				System.out.println("Created Example Config. Please Edit");
				return;
			}
			for(File botConfig : botsDir.listFiles())
			{
				botInitialize(botConfig);
			}
			while (true) {
				selector.select();
				Iterator<SelectionKey> updates = selector.selectedKeys().iterator();
				while (updates.hasNext()) {
					SelectionKey selectionKey = updates.next();
					updates.remove();
					Bot bot = (Bot) selectionKey.attachment();
					bot.channel.read(bot.buffer);
					CodedInputStream inputStream = CodedInputStream.newInstance(bot.buffer);
					int needLength = inputStream.readRawVarint32();
					if(needLength > bot.buffer.capacity())
					{
						throw new RuntimeException("Message larger than Buffer");
					}
					ProtoBuf.Message protoMessage = ProtoBuf.Message.parseFrom(inputStream);
					Message message = new Message(protoMessage.getGroup(), protoMessage.getSender(), protoMessage.getTarget(), protoMessage.getMessage(), protoMessage.getMessageFormat());
					try
					{
					bot.messageRecieved(message);
					}
					catch(Exception e)
					{
						printMessage(new Message(message.getGroup(), "A Bot has crashed!\n" + e.toString(), "Plain Text"), bot);
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void printMessage(Message message, Bot bot) {
		if (message.message.length() == 0) {
			return;
		}
		Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO,
				"Outgoing: " + message.toComplexString());
		try {
			ProtoBuf.Message protoMessage = ProtoBuf.Message.newBuilder()
					.setMessageFormat(message.getMessageFormat())
					.setMessage(message.getMessage())
					.setSender(message.getSender())
					.setTarget(message.getTarget())
					.setGroup(message.getGroup()).build();
			int serializedSize = protoMessage.getSerializedSize();
			int headerSize = CodedOutputStream.computeRawVarint32Size(serializedSize);
			ByteBuffer buffer = ByteBuffer.allocate(headerSize + serializedSize);
			CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(buffer);
			codedOutputStream.writeRawVarint32(headerSize);
			protoMessage.writeTo(codedOutputStream);
			codedOutputStream.flush();
			bot.channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printCommand(String command, Bot bot) {
		printMessage(new Message("operator", command), bot);
	}

	private static void botInitialize(File botConfig) {
		try {
			Properties botProperties = new Properties();
			if (!botConfig.exists()) {
				botConfig.createNewFile();
			}
			botProperties.load(new FileInputStream(botConfig));
			String botClass = botProperties.getProperty("botClass");
			if (botClass == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException(
						"Bot Class is null, cannot execute BridgeMPP server commands");
			}
			Bot bot = (Bot) Class.forName(botClass).newInstance();
			String serverAddress = botProperties.getProperty("serverAddress");
			int portNumber = Integer.parseInt(botProperties.getProperty("serverPort"));
			if(serverAddress == null)
			{
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException(
						"Server Address is null, cannot execute BridgeMPP server commands");
			}
			SocketChannel socket = SocketChannel.open();
			socket.connect(new InetSocketAddress(serverAddress, portNumber));
			socket.configureBlocking(false);

			SelectionKey selectionKey = socket.register(selector, SelectionKey.OP_READ);
			bot.channel = socket;
			bot.buffer = ByteBuffer.allocate(1024);
			bot.channel.write(ByteBuffer.wrap("!protoProtoBufCarry".getBytes("UTF-8")));
			bot.setProperties(botProperties);
			selectionKey.attach(bot);
			
			String serverKey = botProperties.getProperty("serverKey");
			if (serverKey == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException(
						"Server Key is null, cannot execute BridgeMPP server commands");
			}
			printCommand("!usekey " + serverKey, bot);
			String botAlias = botProperties.getProperty("botname");
			if (botAlias != null) {
				printCommand("!createalias " + botAlias, bot);
			}
			String[] groups = botProperties.getProperty("groups").split("; ");
			for (int i = 0; i < groups.length; i++) {
				printCommand("!subscribegroup " + groups[i], bot);
			}
			System.out.println("Joined " + groups.length + " groups");


			bot.initializeBot();
		} catch (IOException | ClassNotFoundException | InstantiationException
				| IllegalAccessException ex) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private static void writeDefaultConfig(Properties botProperties)
			throws IOException {
		botProperties.put("serverKey", "<insertserveraccesskey>");
		botProperties.put("botname", "<Ahumanreadablebotname>");
		botProperties.put("groups", "<groupname1>; <groupname2>");
		botProperties.put("process", "<BotProcessWrapperLaunchCommand>");
		botProperties.put("botClass", "<FQ Class Name>");
	}

	public static abstract class Bot {

		Properties properties;
		SocketChannel channel;
		ByteBuffer buffer;
		
		public final void setProperties(Properties properties) {
			this.properties = properties;
		}

		public abstract void initializeBot();

		public abstract void messageRecieved(Message message);

		public final void sendMessage(Message message) {
			printMessage(message, this);
		}
	}

	public static class Message {
		private String group;
		private String sender;
		private String target;
		private String message;
		private String messageFormat;

		public Message() {

		}

		@Deprecated
		public Message(String sender, String message) {
			this("", sender, "", message, "Plain Text");
		}

		public Message(String group, String message, String messageFormat) {
			this(group, "", "", message, messageFormat);
		}

		public Message(String group, String sender, String target,
				String message, String messageFormat) {
			this.setGroup(group);
			this.setSender(sender);
			this.setTarget(target);
			this.setMessage(message);
			this.setMessageFormat(messageFormat);
		}

		public static Message parseMessage(String complexString) {
			Message message = new Message();
			String[] messageSplit = complexString
					.split("\\s*(?::| -->)\\s+", 5);
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

		public String toComplexString() {
			String messageFormat = getMessageFormat() + ": ";
			String group = (getGroup() != null) ? (getGroup() + ": ")
					: "Direct Message: ";
			String sender = (getSender() != null) ? getSender().toString()
					: "Unknown";
			String target = (getTarget() != null) ? (getTarget().toString() + ": ")
					: ("Unknown: ");
			return messageFormat + group + sender + " --> " + target
					+ getMessage();
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
}
