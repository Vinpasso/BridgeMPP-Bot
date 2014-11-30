package bots.ParrotBot;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ParrotBot{

	private static final long foodreduceTimeinHrs = 5;
	private static final long foodreduceTimeinMin = 60*foodreduceTimeinHrs;
	private static final long foodreduceTimeinSec = 60*foodreduceTimeinMin;
	private static final long foodreduceTimeinMS = 1000*foodreduceTimeinSec;
	
	public static final Pattern removeadditionalnewLinesandWhiteSpaces = Pattern.compile("[\n ]+", Pattern.DOTALL);
	
	private final String ParrotSound;
	private final Random r;
	private double feediness = 0.5;
	private boolean dead = false;
	private String name;
	long lastUpdate;
	List<String> statusqeue = new LinkedList<String>();
	
	private char getRandomLowercaseChar(){
		return (char)(r.nextInt(26) + 'a');
	}
	
	private double getRandomNum(){
		return (r.nextDouble())%1;
	}
	
	public ParrotBot(){
		this("tmp");
		StringBuilder parrotName = new StringBuilder();
		parrotName.append(Character.toUpperCase(getRandomLowercaseChar()));
		for(int i = 0; i < 6; i++){
			parrotName.append(getRandomLowercaseChar());
		}
		name = parrotName.toString();
	}
	
	public ParrotBot(String parrotName){
		r = new Random();
		r.setSeed(System.nanoTime());
		char c1 = getRandomLowercaseChar();
		char c2 = getRandomLowercaseChar();
		ParrotSound = "Kr" + c1 + "a" + c2 + "h";
		name = parrotName;
		lastUpdate = System.currentTimeMillis();
	}
	
	public String processSplitMessage(String[] messageWords){
		if(messageWords == null || messageWords.length == 0){
			return null;
		}
		StringBuilder repeatingWords = new StringBuilder();
		for(int i = 0; i < messageWords.length && repeatingWords.length() < 1;i++){
			if(getRandomNum() > 0.6){
				for(int j = i; j < messageWords.length;j++){
					if(getRandomNum() > 0.3 && !removeadditionalnewLinesandWhiteSpaces.matcher(messageWords[j]).matches()){
						repeatingWords.append(messageWords[j]).append(" ");
					}
				}
			}
		}
		
		if(repeatingWords.length() < 1){
			return null;
		}
		
		if(getRandomNum() > 0.3){
			repeatingWords = new StringBuilder(ParrotSound).append(" ").append(repeatingWords);
		}
		if(getRandomNum() > 0.3){
			repeatingWords.append("...").append(ParrotSound);
		}
		
		return repeatingWords.toString();
	}
	
	public String processMessage(String message){
		if(!dead){
			String[] messageWords = message.split(" ");
			String answer = processSplitMessage(messageWords);
			return answer;
		}
		return null;
	}
	
	public void updateParrot(){
		if(System.currentTimeMillis() - lastUpdate > foodreduceTimeinMS){
			hunger();
		}
	}
	
	public String getStatus(){
		return statusqeue.isEmpty()? null : statusqeue.remove(0) ;
	}
	
	public void hunger(){
		feediness -= 0.01;
	if(feediness < 0 && !dead){
			statusqeue.add("Parrot " + name + " started making weird noises.");
			statusqeue.add("Parrot " + name + " fell from his favourite place.");
			statusqeue.add("Parrot " + name + " took his last breath.");
			dead = true;
		}
	}
	
	public void feed(){
		if(dead){
			return;
		}
		feediness += 0.01;
		statusqeue.add("Parrot " + name + " ate the food.");
		if(feediness > 1.0){
			statusqeue.add("Parrot " + name + " looks fat.");
			statusqeue.add("Parrot" + name + " exploded into a cloud of feathers.");
			dead = true;
		}
	}
	
	public void kill(){
		statusqeue.add("Parrot " + name + " screams in pain.");
		statusqeue.add("Parrot " + name + " fell from his favourite place.");
		statusqeue.add("Parrot " + name + " is breathing heavily.");
		statusqeue.add("Parrot " + name + " is turning red.");
		statusqeue.add("Parrot " + name + " is turning into a pool of blood.");
		statusqeue.add("Parrot " + name + " is no more....");
		dead = true;
		
	}
	
	public boolean isDone(){
		return dead && statusqeue.isEmpty();
	}
	
	public String getName(){
		return name;
	}
	
	public static void main(String[] args) {
		ParrotBot parrot1 = new ParrotBot();
		Scanner reader = new Scanner(System.in);
		boolean exit = false;
		
		while (!exit) {
			String line = reader.nextLine();
			//exit = line.equals("exit");
			String wikiHelp = parrot1.processMessage(line);
			System.out.println(parrot1.getStatus());
			if (wikiHelp != null) {
				System.out.println("Parrot " + parrot1.name + " says:");
				 System.out.println(wikiHelp);
			}
		}
		reader.close();
	}

}
