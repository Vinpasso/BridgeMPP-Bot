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
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vincent Bode
 */
public class BotWrapper {

    static Socket socket;
    static Scanner scanner;
    static PrintStream printStream;

    static Bot bot;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            try {
                socket = new Socket("127.0.0.1", 1234);
                printStream = new PrintStream(socket.getOutputStream(), true, "UTF-8");
                scanner = new Scanner(socket.getInputStream(), "UTF-8");
                botInitialize();
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (!line.contains(": ")) {
                        continue;
                    }
                    Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Incomming: " + line);
                    Message message = new Message(line.substring(0, line.indexOf(": ", line.indexOf(": ") + 1)), line.substring(line.indexOf(": ", line.indexOf(": ") + 1) + 2));
                    bot.messageRecieved(message);
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

//    public static Message parseMessage(BufferedReader reader) throws IOException {
//        return new Message(reader.readLine(), reader.readLine());
//    }
    public static void printMessage(Message message) {
        Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Outgoing: {0}; {1}", new Object[]{message.target, message.message});
        //printStream.println(message.target);
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

        public String target;
        public String message;

        public Message(String sender, String message) {
            this.target = sender;
            this.message = message;
        }
    }
}
