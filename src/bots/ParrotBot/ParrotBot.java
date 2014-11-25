package bots.ParrotBot;

import java.util.Random;
import java.util.Scanner;


public class ParrotBot{

	private final String ParrotSound;
	private final Random r;
	
	private char getRandomLowercaseChar(){
		return (char)(r.nextInt(26) + 'a');
	}
	
	ParrotBot(){
		r = new Random();
		r.setSeed(System.nanoTime());
		char c1 = getRandomLowercaseChar();
		char c2 = getRandomLowercaseChar();
		ParrotSound = "Kr" + c1 + "a" + c2 + "h"; 
	}
	
	public String processSplitMessage(String[] messageWords){
		StringBuilder repeatingWords = new StringBuilder("");
		for(int i = 0; i < messageWords.length && repeatingWords.length() < 1;i++){
			if(Math.random() > 0.6){
				for(int j = i; j < messageWords.length;j++){
					if(Math.random() > 0.3){
						repeatingWords.append(messageWords[j]).append(" ");
					}
				}
			}
		}
		
		if(repeatingWords.length() < 1){
			return null;
		}
		
		if(Math.random() > 0.3){
			repeatingWords = new StringBuilder(ParrotSound).append(" ").append(repeatingWords);
		}
		if(Math.random() > 0.3){
			repeatingWords.append("...").append(ParrotSound);
		}
		
		return repeatingWords.toString();
	}
	
	public String processMessage(String message){
		String[] messageWords = message.split(" ");
		return processSplitMessage(messageWords);
	}
	

	
	public static void main(String[] args) {
		ParrotBot parrot1 = new ParrotBot();
		Scanner reader = new Scanner(System.in);
		boolean exit = false;

		while (!exit) {
			String line = reader.nextLine();
			//exit = line.equals("exit");
			String wikiHelp = parrot1.processMessage(line);

			if (wikiHelp != null) {
				System.out.println("Parrot says:");
				 System.out.println(wikiHelp);
			}
		}
		reader.close();
	}

}
