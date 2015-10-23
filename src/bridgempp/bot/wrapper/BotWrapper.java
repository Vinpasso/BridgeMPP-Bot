package bridgempp.bot.wrapper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import bots.config.MainModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.network.IncommingMessageHandler;
import bridgempp.bot.wrapper.network.KeepAliveSender;
import bridgempp.bot.wrapper.network.ProtoBuf;
import bridgempp.util.Log;

/**
 *
 */
public class BotWrapper {

	private static Bootstrap bootstrap;

	public static String build;

	private static Injector injector;
	
	private static volatile boolean isShuttingDown = false;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			build = args[0];
		} else {
			Log.log(Level.WARNING,
					"No external build version supplied to BridgeMPP-Bot-Wrapper");
		}
		EventLoopGroup loopGroup = new NioEventLoopGroup(2);
		bootstrap = new Bootstrap();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception {

			}
		});
		// init Guice Injector
		initGuice();

		File botsDir = new File("bots/");
		if (!botsDir.exists()) {
			botsDir.mkdir();
			Properties exampleBotProperties = new Properties();
			try {
				writeDefaultConfig(exampleBotProperties);
				exampleBotProperties.store(new FileOutputStream(
						"bots/exampleBot.config"), "Bot Wrapper Configuration");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Created Example Config. Please Edit");
			return;
		}
		for (File botConfig : botsDir.listFiles()) {
			try {
				botInitialize(botConfig);
			} catch (Exception e) {
				Log.log(Level.SEVERE, "Bot Initialize failed in Config: "
						+ botConfig.toString(), e);
			}
		}

	}

	private static void initGuice() {
		injector = Guice.createInjector(new MainModule());
	}

	public static void printMessage(Message message, Bot bot) {
		if (bot.channelFuture == null) {
			if (message.message.length() == 0) {
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

		ProtoBuf.Message protoMessage = ProtoBuf.Message.newBuilder()
				.setMessageFormat(message.getMessageFormat().getName())
				.setMessage(message.getMessage())
				.setSender(message.getSender()).setTarget(message.getTarget())
				.setGroup(message.group).build();
		bot.channelFuture.channel().writeAndFlush(protoMessage);
		Log.log(Level.INFO, "Outbound: " + message.toComplexString());
	}

	public static void printCommand(String command, Bot bot) {
		printMessage(new Message("", command, MessageFormat.PLAIN_TEXT), bot);
	}

	private static void botInitialize(File botConfig)
			throws UnsupportedOperationException {
		try {
			Properties botProperties = new Properties();
			if (!botConfig.exists()) {
				botConfig.createNewFile();
			}
			botProperties.load(new FileInputStream(botConfig));
			String botClass = botProperties.getProperty("botClass");
			if (botClass == null) {
				writeDefaultConfig(botProperties);
				Log.log(Level.SEVERE, "Bot Class is null, cannot execute BridgeMPP server commands in file: " + botConfig.getName());
				fail();
			}
			Class<?> clazz = Class.forName(botClass);
            if (!Bot.class.isAssignableFrom(clazz)) {
                Log.log(Level.SEVERE, "Bot class " + clazz.toString() + " not instance of Bot");
                fail();
            }
            @SuppressWarnings("unchecked")
            Bot bot = injector.getInstance((Class<Bot>)clazz);
			bot.setProperties(botProperties);
			bot.configFile = botConfig.getAbsolutePath();
			bot.initializeBot();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					Log.log(Level.INFO, "Deinitializing Bot: " + bot.name);
					isShuttingDown = true;
					try {
						bot.deinitializeBot();
						Log.log(Level.INFO, "Deinitialized Bot: " + bot.name);

					} catch (Exception e) {
						Log.log(Level.SEVERE,
								"Failed to deinitialize Bot! Data Loss possible");
					}
					Log.log(Level.INFO, "Saving Bot: " + bot.name);
					bot.saveProperties();
					bot.channelFuture.channel().close();
					Log.log(Level.INFO, "Saved Bot: " + bot.name);
				}

			}));
			String serverAddress = botProperties.getProperty("serverAddress");
			int portNumber = Integer.parseInt(botProperties
					.getProperty("serverPort"));
			if (serverAddress == null) {
				writeDefaultConfig(botProperties);
						Log.log(Level.SEVERE, "Server Address is null, cannot execute BridgeMPP server commands");
						fail();
			}
			ChannelFuture channelFuture = bootstrap
					.connect(new InetSocketAddress(serverAddress, portNumber));
			byte[] protocol = new byte[1];
			protocol[0] = 0x32;
			channelFuture.await();
			channelFuture.channel().writeAndFlush(
					Unpooled.wrappedBuffer(protocol));
			ChannelPipeline pipeline = channelFuture.channel().pipeline();
			pipeline.addLast("idleStateHandler", new IdleStateHandler(120, 60,
					120));
			pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
			pipeline.addLast("protobufDecoder", new ProtobufDecoder(
					ProtoBuf.Message.getDefaultInstance()));
			pipeline.addLast("frameEncoder",
					new ProtobufVarint32LengthFieldPrepender());
			pipeline.addLast("protobufEncoder", new ProtobufEncoder());
			pipeline.addLast("keepAliveSender", new KeepAliveSender());
			pipeline.addLast(new IncommingMessageHandler(bot));
			bot.channelFuture = channelFuture;

			String serverKey = botProperties.getProperty("serverKey");
			if (serverKey == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException(
						"Server Key is null, cannot execute BridgeMPP server commands");
			}
			printCommand("!botusekey " + serverKey, bot);
			String botAlias = botProperties.getProperty("botname");
			if (botAlias != null) {
				printCommand("!botcreatealias \"" + botAlias + "\"", bot);
			}
			bot.name = botAlias;
			String[] groups = botProperties.getProperty("groups").split("; ");
			for (int i = 0; i < groups.length; i++) {
				printCommand("!botsubscribegroup \"" + groups[i] + "\"", bot);
			}
			System.out.println("Sent request to join " + groups.length
					+ " groups");

		} catch (IOException | ClassNotFoundException | InterruptedException ex) {
			Log.log(Level.SEVERE, null, ex);
		}
	}



	private static void systemExit(int status)
	{
		if(isShuttingDown)
		{
			return;
		}
		isShuttingDown = true;
		System.exit(status);
	}
	
	public static void shutdown() {
		systemExit(0);
	}
	private static void fail() {
		systemExit(1);
	}

	private static void writeDefaultConfig(Properties botProperties)
			throws IOException {
		botProperties.put("serverKey", "<insertserveraccesskey>");
		botProperties.put("botname", "<Ahumanreadablebotname>");
		botProperties.put("groups", "<groupname1>; <groupname2>");
		botProperties.put("process", "<BotProcessWrapperLaunchCommand>");
		botProperties.put("botClass", "<FQ Class Name>");
	}
}
