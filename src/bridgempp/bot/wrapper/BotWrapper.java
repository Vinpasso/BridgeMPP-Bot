package bridgempp.bot.wrapper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

/**
 *
 * @author Vincent Bode
 */
public class BotWrapper {

	private static Bootstrap bootstrap;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		EventLoopGroup loopGroup = new NioEventLoopGroup(2);
		bootstrap = new Bootstrap();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception {

			}
		});
		File botsDir = new File("bots/");
		if (!botsDir.exists()) {
			botsDir.mkdir();
			Properties exampleBotProperties = new Properties();
			try {
				writeDefaultConfig(exampleBotProperties);
				exampleBotProperties.store(new FileOutputStream("bots/exampleBot.config"), "Bot Wrapper Configuration");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Created Example Config. Please Edit");
			return;
		}
		for (File botConfig : botsDir.listFiles()) {
			botInitialize(botConfig);
		}

	}

	public static void printMessage(Message message, Bot bot) {
		if(bot.channelFuture == null)
		{
			if(message.message.length() == 0)
			{
				System.out.println("CONSOLE BOT: Empty Message");
			}
			System.out.println("CONSOLE BOT: " + message.toComplexString());
			return;
		}
		if (message.message.length() == 0) {
			return;
		}
		try {
			message.validate();
		} catch (Exception e) {
			throw new InvalidMessageFormatException(e);
		}

		ProtoBuf.Message protoMessage = ProtoBuf.Message.newBuilder().setMessageFormat(message.getMessageFormat())
				.setMessage(message.getMessage()).setSender(message.getSender()).setTarget(message.getTarget())
				.setGroup(message.group).build();
		bot.channelFuture.channel().writeAndFlush(protoMessage);
		Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Outbound: " + message.toComplexString());
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
				throw new UnsupportedOperationException("Bot Class is null, cannot execute BridgeMPP server commands");
			}
			Bot bot = (Bot) Class.forName(botClass).newInstance();
			bot.initializeBot();
			String serverAddress = botProperties.getProperty("serverAddress");
			int portNumber = Integer.parseInt(botProperties.getProperty("serverPort"));
			if (serverAddress == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException(
						"Server Address is null, cannot execute BridgeMPP server commands");
			}
			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(serverAddress, portNumber));
			byte[] protocol = new byte[1];
			protocol[0] = 0x32;
			channelFuture.await();
			channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer(protocol));
			channelFuture.channel().pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
			channelFuture.channel().pipeline()
					.addLast("protobufDecoder", new ProtobufDecoder(ProtoBuf.Message.getDefaultInstance()));
			channelFuture.channel().pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
			channelFuture.channel().pipeline().addLast("protobufEncoder", new ProtobufEncoder());
			channelFuture.channel().pipeline().addLast("writeTimeoutHandler", new WriteTimeoutHandler(30));
			channelFuture.channel().pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 60, 0));
			channelFuture.channel().pipeline().addLast("keepAliveSender", new KeepAliveSender());
			channelFuture.channel().pipeline().addLast(new IncommingMessageHandler(bot));
			bot.channelFuture = channelFuture;
			bot.setProperties(botProperties);

			String serverKey = botProperties.getProperty("serverKey");
			if (serverKey == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException("Server Key is null, cannot execute BridgeMPP server commands");
			}
			printCommand("!usekey " + serverKey, bot);
			String botAlias = botProperties.getProperty("botname");
			if (botAlias != null) {
				printCommand("!createalias " + botAlias, bot);
			}
			bot.name = botAlias;
			String[] groups = botProperties.getProperty("groups").split("; ");
			for (int i = 0; i < groups.length; i++) {
				printCommand("!subscribegroup " + groups[i], bot);
			}
			System.out.println("Joined " + groups.length + " groups");

		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
				| InterruptedException ex) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void writeDefaultConfig(Properties botProperties) throws IOException {
		botProperties.put("serverKey", "<insertserveraccesskey>");
		botProperties.put("botname", "<Ahumanreadablebotname>");
		botProperties.put("groups", "<groupname1>; <groupname2>");
		botProperties.put("process", "<BotProcessWrapperLaunchCommand>");
		botProperties.put("botClass", "<FQ Class Name>");
	}

	/**
	 * The Class to be implemented by a BridgeMPP Bot
	 *
	 */
	public static abstract class Bot {

		public String name;
		Properties properties;
		ChannelFuture channelFuture;

		/**
		 * Sets the Properties loaded from the Bot Configuration file
		 * @param properties The Bots Parameters
		 */
		public final void setProperties(Properties properties) {
			this.properties = properties;
		}

		/**
		 * Initialize the Bot
		 * Called when the Bot is loaded by the Botwrapper
		 */
		public abstract void initializeBot();

		/**
		 * Message Received
		 * Called when the Bot receives a BridgeMPP Message
		 * @param message The BridgeMPP Message
		 */
		public abstract void messageReceived(Message message);

		/**
		 * Send Message
		 * Sends this BridgeMPP Message to the target Group
		 * @param message The BridgeMPP Message to send
		 */
		public void sendMessage(Message message) {
			printMessage(message, this);
		}
	}

	public static class IncommingMessageHandler extends SimpleChannelInboundHandler<ProtoBuf.Message> {
		private Bot bot;

		public IncommingMessageHandler(Bot bot) {
			this.bot = bot;
		}

		protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtoBuf.Message protoMessage) {
			Message message = new Message(protoMessage.getGroup(), protoMessage.getSender(), protoMessage.getTarget(),
					protoMessage.getMessage(), protoMessage.getMessageFormat());
			Logger.getLogger(BotWrapper.class.getName()).log(Level.INFO, "Inbound: " + message.toComplexString());
			if (message.getMessage().startsWith("?botwrapper reload")) {
				bot.sendMessage(new Message(message.getGroup(), "Bot Wrapper reloading. Respawn Throttle 60 seconds",
						"Plain Text"));
				System.exit(0);
			}
			if(message.getMessage().startsWith("?botwrapper ping"))
			{
				bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " at your service", "Plain Text"));
			}
			try {
				bot.messageReceived(message);
			} catch (Exception e) {
				printMessage(
						new Message(message.getGroup(), "A Bot has crashed!\n" + e.toString() + "\n"
								+ e.getStackTrace()[0].toString(), "Plain Text"), bot);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
					"A Connection has been disconnected, exiting...", cause);
			System.exit(0);
		}

	}
	
	public static class KeepAliveSender extends ChannelDuplexHandler
	{
		@Override
		public void userEventTriggered(ChannelHandlerContext context, Object event) 
		{
			if(event instanceof IdleStateEvent)
			{
				IdleStateEvent idleEvent = (IdleStateEvent) event;
				if(idleEvent.state() == IdleState.WRITER_IDLE)
				{
					Logger.getLogger(BotWrapper.class.getName()).log(Level.INFO,
							"A Connection is idle. Sending PING...");

					ProtoBuf.Message protoMessage = ProtoBuf.Message.newBuilder().setMessageFormat("PLAIN_TEXT")
							.setMessage("").setSender("").setTarget("")
							.setGroup("").build();
					ChannelFuture future = context.writeAndFlush(protoMessage);
					future.addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							if(!future.isSuccess())
							{
								Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
										"A Connection has been disconnected after PING: " + future.toString() + ", exiting...");
								System.exit(0);
							}
						}
					});
				}
			}
		}
		
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception
		{
			if(cause instanceof WriteTimeoutException)
			{
				Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
						"A Connection has been disconnected after Write Timeout: " + cause.toString() + ", exiting...");
				System.exit(0);
			}
			else
			{
				super.exceptionCaught(context, cause);
			}
		}
	}
	
	/**
	 * BridgeMPP Message class containing following attributes
	 * String group The group the Message originated from/will be sent to
	 * String sender The sender of this Message, will be auto-set/overridden
	 * String target The destination of this Message, will be auto-set/overridden
	 * String message The raw text version of this Message
	 * String messageFormat The format in which this Message has been sent
	 */
	public static class Message {
		private String group;
		private String sender;
		private String target;
		private String message;
		private String messageFormat;

		public Message() {

		}
		
		/**
		 * Reply to an existing Message (Does not send the message)
		 * Send the message with sendMessage(message)
		 * @param message The received Message to reply to
		 * @param text The text of the reply Message
		 * @param format The format of the reply Message
		 * @return The new Message, to be passed to sendMessage
		 */
		static Message replyTo(Message message, String text, String format)
		{
			return new Message(message.getMessage(), text, format);
		}

		/**
		 * Check whether this Message violates BridgeMPP Message Restrictions
		 * Throws an exception which may or may not be caught at will
		 * @throws Exception The Reason for the invalidation of this message
		 */
		public void validate() throws Exception {
			if(getMessage().length() > 60000)
			{
				throw new Exception("Dangerous Message Length " + getMessage().length() + "! Send request rejected");
			}
			if(Pattern.compile("[\\x00-\\x08|\\x10-\\x1F]").matcher(getMessage()).find())
			{
				throw new Exception("Dangerous Control Characters detected! Access Denied! " + URLEncoder.encode(getMessage(), "UTF-8"));
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
		 * @param group The group to which the Message will be sent (will usually be retrieved from old message)
		 * @param message The message that will be sent to the group
		 * @param messageFormat The format of the Message ("PLAINTEXT", "XHTML")
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
}
