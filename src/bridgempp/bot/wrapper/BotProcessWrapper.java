/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bridgempp.bot.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import bridgempp.bot.messageformat.MessageFormat;

/**
 *
 * @author Vincent Bode
 */
public class BotProcessWrapper extends Bot implements Runnable {

    String processCommand;
    Process process;
    PrintStream printStream;
    BufferedReader reader;

    public BotProcessWrapper() {
    }
    @Override
    public void initializeBot()
    {
        try {
            processCommand = properties.getProperty("process");
            process = Runtime.getRuntime().exec(processCommand);
            printStream = new PrintStream(process.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            new Thread(this).start();
        } catch (IOException ex) {
            Logger.getLogger(BotProcessWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void messageReceived(Message message) {
        printStream.println(message.getMessage());
    }

    public void readMessage() {
        while (process.isAlive()) {
            try {
                String line = reader.readLine();
                while (reader.ready()) {
                    String thisLine = reader.readLine();
                    if (thisLine.trim().equals("null")) {
                        continue;
                    }
                    line += thisLine + "\n";
                }
                while (process.getErrorStream().available() > 0) {
                    byte[] buffer = new byte[1024];
                    process.getErrorStream().read(buffer);
                    line += "\n" + new String(buffer);
                }
                line = line.trim();
                if (line.equals("null") || line.isEmpty()) {
                    continue;
                }
                sendMessage(new Message("", line, MessageFormat.PLAIN_TEXT));
            } catch (IOException ex) {
                Logger.getLogger(BotProcessWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        readMessage();
    }

}
