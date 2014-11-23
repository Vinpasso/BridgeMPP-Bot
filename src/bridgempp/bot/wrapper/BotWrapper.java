package bridgempp.bot.wrapper;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
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
                socket = new Socket("vinpasso.org", 1234);
                printStream = new PrintStream(socket.getOutputStream(), true);
                scanner = new Scanner(socket.getInputStream());
                botInitialize();
                while (scanner.hasNext()) {
                    Message message = new Message(scanner.nextLine(), scanner.nextLine());
                    Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Incomming: {0}; {1}", new Object[]{message.target, message.message});
                    Message reply = bot.messageRecieved(message);
                    if(reply != null)
                    {
                        printMessage(reply);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Message parseMessage(BufferedReader reader) throws IOException {
        return new Message(reader.readLine(), reader.readLine());
    }

    public static void printMessage(Message message) {
        Logger.getLogger(BotWrapper.class.getSimpleName()).log(Level.INFO, "Outgoing: {0}; {1}", new Object[]{message.target, message.message});
        printStream.println(message.target);
        printStream.println(message.message);
    }

    public static void printCommand(String command) {
        printMessage(new Message("operator", command));
    }

    private static void botInitialize() {
        try {
            Properties botProperties = new Properties();
            botProperties.load(new FileInputStream("config.txt"));
            String serverKey = botProperties.getProperty("serverKey");
            if (serverKey.isEmpty()) {
                throw new UnsupportedOperationException("Server Key is null, cannot execute BridgeMPP server commands");
            }
            printCommand("!usekey " + serverKey);
            String[] groups = botProperties.getProperty("groups").split("; ");
            for (int i = 0; i < groups.length; i++) {
                printCommand("!subscribegroup " + groups[i]);
            }
            System.out.println("Joined " + groups.length + " groups");
        } catch (IOException ex) {
            Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static abstract class Bot {
        private final Properties properties;
        
        public Bot(Properties properties)
        {
            this.properties = properties;
        }
        
        public abstract Message messageRecieved(Message message);
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
