package bridgempp.bot.wrapper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Vincent Bode
 */
public class BotWrapper {

	static Socket socket;
	static BufferedReader bufferedReader;
	static PrintStream printStream;

	static Bot bot;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		while (true) {
			try {
				socket = new Socket("127.0.0.1", 1234);
				printStream = new PrintStream(socket.getOutputStream(), true, "UTF-8");
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				botInitialize();
				while (true) {
					String buffer = "";
					while(bufferedReader.ready())
					{
						buffer += bufferedReader.readLine() + "\n";
					}
					buffer = buffer.trim();
					Matcher matcher = Pattern.compile("(?<=<message>)[^<]+(?=<\\/message>)").matcher(buffer);
					while (matcher.find()) {					
						Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Incomming: " + matcher.group());
						Message message = Message.parseMessage(matcher.group());
						bot.messageRecieved(message);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	// public static Message parseMessage(BufferedReader reader) throws
	// IOException {
	// return new Message(reader.readLine(), reader.readLine());
	// }
	public static void printMessage(Message message) {
		Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Outgoing: {0}; {1}",
				new Object[] { message.target, message.message });
		// printStream.println(message.target);
		printStream.println(message.message);
	}

	public static void printCommand(String command) {
		printMessage(new Message("operator", command));
	}

	private static void botInitialize() {
		try {
			Properties botProperties = new Properties();
			File file = new File("config.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			botProperties.load(new FileInputStream(file));
			printCommand("!protoxmlcarry");
			String serverKey = botProperties.getProperty("serverKey");
			if (serverKey == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException("Server Key is null, cannot execute BridgeMPP server commands");
			}
			printCommand("!usekey " + serverKey);
			String botAlias = botProperties.getProperty("botname");
			if (botAlias != null) {
				printCommand("!createalias " + botAlias);
			}
			String[] groups = botProperties.getProperty("groups").split("; ");
			for (int i = 0; i < groups.length; i++) {
				printCommand("!subscribegroup " + groups[i]);
			}
			System.out.println("Joined " + groups.length + " groups");
			String botClass = botProperties.getProperty("botClass");
			if (botClass == null) {
				writeDefaultConfig(botProperties);
				throw new UnsupportedOperationException("Bot Class is null, cannot execute BridgeMPP server commands");
			}
			bot = (Bot) Class.forName(botClass).newInstance();
			bot.setProperties(botProperties);
			bot.initializeBot();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void writeDefaultConfig(Properties botProperties) throws IOException {
		botProperties.put("serverKey", "<insertserveraccesskey>");
		botProperties.put("botname", "<Ahumanreadablebotname>");
		botProperties.put("groups", "<groupname1>; <groupname2>");
		botProperties.put("process", "<BotProcessWrapperLaunchCommand>");
		botProperties.put("botClass", "<FQ Class Name>");
		botProperties.store(new FileOutputStream("config.txt"), "Bot Wrapper Configuration");
	}

	public static abstract class Bot {

		Properties properties;

		public final void setProperties(Properties properties) {
			this.properties = properties;
		}

		public abstract void initializeBot();

		public abstract void messageRecieved(Message message);

		public final void sendMessage(Message message) {
			printMessage(message);
		}
	}

	public static class Message {
		public String group;
		public String sender;
		public String target;
		public String message;
		public String messageFormat;

		public Message() {

		}

		public Message(String sender, String message) {
			this("", sender, "", message, "Plain Text");
		}

		public Message(String group, String sender, String target, String message, String messageFormat) {
			this.group = group;
			this.sender = sender;
			this.target = target;
			this.message = message;
			this.messageFormat = messageFormat;
		}

		public static Message parseMessage(String complexString) {
			Message message = new Message();
			Pattern pattern = Pattern.compile("[^(:\\ |\\ -->\\ )]+");
			Matcher matcher = pattern.matcher(complexString);
			matcher.find();
			message.messageFormat = matcher.group();
			matcher.find();
			message.group = matcher.group();
			matcher.find();
			message.sender = matcher.group();
			matcher.find();
			message.target = matcher.group();
			message.message = complexString.substring(matcher.end() + 2);
			return message;
		}
	}
}
