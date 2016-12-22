package bridgempp.bot.wrapper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import bridgempp.bot.database.PersistenceManager;
import bridgempp.bot.wrapper.network.CommandTransceiver;
import bridgempp.message.Message;
import bridgempp.message.MessageBuilder;
import bridgempp.services.socket.ProtoBufUtils;
import bridgempp.util.Log;

/**
 *
 */
public class BotWrapper
{

	private static Bootstrap bootstrap;

	public static String build;

	private static Injector injector;

	private static volatile boolean isShuttingDown = false;
	private static ArrayList<Bot> bots;
	private static File botsDir;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			build = args[0];
		} else
		{
			Log.log(Level.WARNING, "No external build version supplied to BridgeMPP-Bot-Wrapper");
		}
		bots = new ArrayList<>();
		Schedule.startExecutorService();
		Log.log(Level.INFO, "Initializing Bootstrap");
		EventLoopGroup loopGroup = new NioEventLoopGroup(2);
		bootstrap = new Bootstrap();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);

		//Do not remove this
		bootstrap.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception
			{

			}
		});
		
		Log.log(Level.INFO, "Initializing Guice");
		// init Guice Injector
		initGuice();

		Log.log(Level.INFO, "Accessing bot configurations");
		botsDir = new File("bots/");
		if (!botsDir.exists())
		{
			Log.log(Level.INFO, "Creating bot configuration directory");
			botsDir.mkdir();
			Properties exampleBotProperties = new Properties();
			try
			{
				writeDefaultConfig(exampleBotProperties);
				exampleBotProperties.store(new FileOutputStream("bots/exampleBot.config"), "Bot Wrapper Configuration");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Created example config. Please edit");
			return;
		}
		for (File botConfig : botsDir.listFiles())
		{
			try
			{
				botInitialize(botConfig);
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, "Bot Initialize failed in Config: " + botConfig.toString(), e);
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					Log.log(Level.INFO, "Received Shutdown Signal");
					Schedule.stopExecutorService();
					Log.log(Level.INFO, "Shutting down Network");
					loopGroup.shutdownGracefully();
					Log.log(Level.INFO, "Shutting down Bots");
					Iterator<Bot> botIterator = bots.iterator();
					while (botIterator.hasNext())
					{
						Bot bot = botIterator.next();
						Log.log(Level.INFO, "Deinitializing Bot: " + bot.name);
						isShuttingDown = true;
						try
						{
							bot.deinitializeBot();
							Log.log(Level.INFO, "Deinitialized Bot: " + bot.name);

						} catch (Exception e)
						{
							Log.log(Level.SEVERE, "Failed to deinitialize Bot! Data Loss possible");
						}
						Log.log(Level.INFO, "Saving Bot: " + bot.name);
						bot.saveProperties();
						bot.channel.close();
						Log.log(Level.INFO, "Saved Bot: " + bot.name);
					}
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, "Exception during Shutdown Routine: ", e);
				}
				Log.log(Level.INFO, "Shutdown successful. Exiting...");
			}

		}));

		/*Log.log(Level.INFO, "Waiting 60 seconds for Authentication to complete");
		try
		{
			Thread.sleep(60000);
		} catch (InterruptedException e)
		{
			Log.log(Level.INFO, "Interrupt while waiting for Authentication to complete", e);
		}
		Log.log(Level.INFO, "Checking for Status of Bot authentications");
		Iterator<Bot> iterator = bots.iterator();
		while (iterator.hasNext())
		{
			Bot bot = iterator.next();
			if (bot.channel.pipeline().last() instanceof CommandTransceiver)
			{
				Log.log(Level.SEVERE, "Bot: " + bot.name + " did not autheticate within the specified Timeout, exiting");
				shutdown();
			} else
			{
				Log.log(Level.INFO, "Bot: " + bot.name + " appears to be authenticated");
			}
		}*/
		Schedule.execute(() -> { Log.log(Level.INFO, "Loading Database..."); PersistenceManager.loadFactory(); Log.log(Level.INFO, "Loaded Database");});
	}

	private static void initGuice()
	{
		try
		{
			//TODO: Read this from a config
			injector = Guice.createInjector((Module) Class.forName("bots.config.MainModule").newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, "Guice failure", e);
		}
	}

	public static ChannelFuture printMessage(Message message, Bot bot)
	{
		if (bot.channel == null)
		{
			if (message.getLength() == 0)
			{
				System.out.println("CONSOLE BOT: Empty Message");
			}
			System.out.println("CONSOLE BOT: " + message.toString());
			return null;
		}
		if (message.getLength() == 0)
		{
			return null;
		}
		//message.validate();

		bridgempp.services.socket.protobuf.Message protoMessage = ProtoBufUtils.serializeMessage(message);
		ChannelFuture future = bot.channel.writeAndFlush(protoMessage);
		Log.log(Level.INFO, "Outbound: " + message.toString());
		return future;
	}

	public static ChannelFuture printCommand(String command, Bot bot)
	{
		return printMessage(new MessageBuilder(null, null).addPlainTextBody(command).build(), bot);
	}

	private static void botInitialize(File botConfig) throws UnsupportedOperationException
	{
		try
		{
			Log.wrapperLog(Level.INFO, "Loading bot: " + botConfig.getPath());
			Properties botProperties = new Properties();
			if (!botConfig.exists())
			{
				botConfig.createNewFile();
			}
			Log.wrapperLog(Level.INFO, "Loading bot properties: " + botConfig.getPath());
			botProperties.load(new FileInputStream(botConfig));
			String botClass = botProperties.getProperty("botClass");
			if (botClass == null)
			{
				writeDefaultConfig(botProperties);
				Log.log(Level.SEVERE, "Bot Class is null, cannot execute BridgeMPP server commands in file: " + botConfig.getName());
				fail();
			}
			Log.log(Level.INFO, "Loading bot class: " + botConfig.getPath());
			Class<?> clazz = Class.forName(botClass);
			if (!Bot.class.isAssignableFrom(clazz))
			{
				Log.log(Level.SEVERE, "Bot class " + clazz.toString() + " not instance of Bot");
				fail();
			}
			@SuppressWarnings("unchecked")
			Bot bot = injector.getInstance((Class<Bot>) clazz);
			bot.setProperties(botProperties);
			bot.configFile = botConfig.getAbsolutePath();

			Log.wrapperLog(Level.INFO, "Creating bot network interface: " + botConfig.getPath());
			String serverAddress = botProperties.getProperty("serverAddress");
			int portNumber = Integer.parseInt(botProperties.getProperty("serverPort"));
			if (serverAddress == null)
			{
				writeDefaultConfig(botProperties);
				Log.wrapperLog(Level.SEVERE, "Server Address is null, cannot execute BridgeMPP server commands");
				fail();
			}
			String serverKey = botProperties.getProperty("serverKey");
			if (serverKey == null)
			{
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException("Server Key is null, cannot execute BridgeMPP server commands");
			}
			String botAlias = botProperties.getProperty("botname");
			if (botAlias != null)
			{
				printCommand("!botcreatealias \"" + botAlias + "\"", bot);
			}
			bot.name = botAlias;
			String[] groups = botProperties.getProperty("groups").split("; ");
			if (groups.length == 0)
			{
				throw new UnsupportedOperationException("No groups declared. Please specify at least one group to join");
			}

			Log.log(Level.INFO, "Connecting bot to server...", bot);

			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(serverAddress, portNumber));
			// byte[] protocol = new byte[1];
			// protocol[0] = 0x32;
			// channelFuture.await();
			// channelFuture.channel().writeAndFlush(
			// Unpooled.wrappedBuffer(protocol));

			channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future)
				{
					if (!future.isSuccess())
					{
						Log.log(Level.WARNING, "Connection could not be established!", bot);
						BotWrapper.shutdown();
					} else
					{
						Log.log(Level.INFO, "Connection established.", bot);
						bot.channel = channelFuture.channel();
						new CommandTransceiver(bot.channel, bot.name, serverKey, groups, bot).initializeCommands();
					}
				}
			});
			
			channelFuture.await();

			bots.add(bot);
		} catch (Exception ex)
		{
			Log.log(Level.SEVERE, "Unexpected error", ex);
		}
	}

	private static void systemExit(int status)
	{
		if (isShuttingDown)
		{
			return;
		}
		isShuttingDown = true;
		System.exit(status);
	}

	public static void shutdown()
	{
		systemExit(0);
	}

	private static void fail()
	{
		systemExit(1);
	}

	private static void writeDefaultConfig(Properties botProperties) throws IOException
	{
		botProperties.put("serverKey", "<insertserveraccesskey>");
		botProperties.put("serverAddress", "<insertserveraddress>");
		botProperties.put("serverPort", "<insertserverport>");
		botProperties.put("botname", "<Ahumanreadablebotname>");
		botProperties.put("groups", "<groupname1>; <groupname2>");
		botProperties.put("process", "<BotProcessWrapperLaunchCommand>");
		botProperties.put("botClass", "<FQ Class Name>");
	}

	public static String statusCheck()
	{
		return "There are " + bots.size() + " Bots loaded in Memory\n" + "There are " + botsDir.listFiles().length + " config files present\n" + "The current Memory usage is "
				+ (Runtime.getRuntime().totalMemory() / 1000000L) + "/" + (Runtime.getRuntime().maxMemory() / 1000000L) + "MB\n" + "There are " + (Runtime.getRuntime().freeMemory() / 1000000L)
				+ " MB of free Memory\n" + "There are " + Thread.activeCount() + " threads running in the BotWrapper";
	}
}
