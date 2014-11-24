package de.bots.command;

import de.bots.command.calc.CalculationInterpreter;
import de.bots.command.news.NewsInterpreter;

import java.util.Scanner;

public class CommandBot {
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
			args = msg.substring(commandEnd+1);
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
}
