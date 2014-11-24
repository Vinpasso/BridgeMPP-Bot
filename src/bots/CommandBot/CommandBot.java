package bots.CommandBot;

import bots.CommandBot.calc.CalculationInterpreter;
import bots.CommandBot.news.NewsInterpreter;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.bot.wrapper.BotWrapper.Message;

import java.util.Scanner;

public class CommandBot extends bridgempp.bot.wrapper.BotWrapper.Bot {

    private CalculationInterpreter ci;
    private NewsInterpreter ni;

    public CommandBot() {
        ci = new CalculationInterpreter();
        ni = new NewsInterpreter();
    }

    public String evaluateMessage(String msg) {
        if (!msg.startsWith("?")) {
            return null;
        }
        int commandEnd = msg.indexOf(" ");
        String command = null;
        String args = null;
        if (commandEnd > 0) {
            command = msg.substring(1, commandEnd);
            args = msg.substring(commandEnd + 1);
        } else {
            command = msg.substring(1);
        }

        switch (command) {
            case "calc":
                return ci.getAnswer(args);
            case "news":
                return ni.getAnswer(args);
        }
        return null;
    }

    public static void main(String[] args) {
        CommandBot cb = new CommandBot();
        Scanner scan = new Scanner(System.in);
        String line;
        System.out.println("start:");
        while (!(line = scan.nextLine()).equals("end")) {
            System.out.println(cb.evaluateMessage(line));
        }
    }

    @Override
    public void initializeBot() {
    }

    @Override
    public void messageRecieved(BotWrapper.Message message) {
        String botResponse = evaluateMessage(message.message);
        if (botResponse != null) {
            sendMessage(new Message(message.target, botResponse));
        }
    }
}
