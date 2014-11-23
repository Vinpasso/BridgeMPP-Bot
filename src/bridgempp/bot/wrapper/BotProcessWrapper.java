/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bridgempp.bot.wrapper;

import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vincent Bode
 */
public class BotProcessWrapper extends Bot {

    String processCommand;
    Process process;
    PrintStream printStream;
    Scanner scanner;
    
    public BotProcessWrapper(Properties properties) {
        super(properties);
        try {
            processCommand = properties.getProperty("process");
            process = Runtime.getRuntime().exec(processCommand);
            printStream = new PrintStream(process.getOutputStream(), true);
            scanner = new Scanner(process.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(BotProcessWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public BotWrapper.Message messageRecieved(BotWrapper.Message message) {
        printStream.println(message.message);
        String line = scanner.nextLine();
        if(line.equals("null") || line.isEmpty())
        {
            return null;
        }
        return new Message(message.target, line);
    }
    
}
