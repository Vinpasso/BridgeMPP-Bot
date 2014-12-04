package bots.ParrotBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParrotCage {

	Map<String, ParrotBot> parrots;
	List<String> doneParrots;
	public static int maxParrotCount = -1;

	
	public ParrotCage() {
		parrots = new HashMap<String, ParrotBot>();
		doneParrots = new ArrayList<String>();
	}

	public ParrotBot addParrot(String parrotName) {
		if(parrots.size() == maxParrotCount){
			return null;
		}
		ParrotBot parrot = null;
		if(parrotName == null || parrotName.trim().equals("")){
			parrot = new ParrotBot();
		}
		else{
			parrot = new ParrotBot(parrotName);
		}
		parrots.put(parrot.getName(), parrot);
		return parrot;
	}

	public void killParrot(String parrotName) {
		if(parrots.size() == 0){
			return;
		}
		if (parrotName.equals("all")) {
			for (ParrotBot parrot : parrots.values()) {
				parrot.kill();
			}
		} else {
			try {
				parrots.get(parrotName).kill();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateParrots() {
		for (ParrotBot parrot : parrots.values()) {
			parrot.updateParrot();

			if (parrot.isDone()) {
				doneParrots.add(parrot.getName());
			}
		}
		for (String doneParrot : doneParrots) {
			parrots.remove(doneParrot);
		}
	}

	public void feedParrot(String parrot){
		try{
			parrots.get(parrot).feed();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getParrotCount(){
		return parrots.size();
	}
	
	public String getStatus() {
		StringBuilder stringBuilder = new StringBuilder();
		for (ParrotBot parrot : parrots.values()) {
			String parrotStatus = parrot.getStatus();
			if (parrotStatus != null) {
				stringBuilder.append(parrotStatus).append(" <br/>");
			}
		}
		return stringBuilder.toString();
	}

	public String processMessage(String[] msgWords) {
		StringBuilder stringBuilder = new StringBuilder();
		for (ParrotBot parrot : parrots.values()) {
			String parrotMessage = parrot.processSplitMessage(msgWords);
			if (parrotMessage != null) {
				stringBuilder.append(parrot.getColouredName()).append(": ").append(parrotMessage).append(" <br/>");
			}
		}
		if(stringBuilder.length() > "<br/>".length()){
			return stringBuilder.substring(0, stringBuilder.length()-"<br/>".length());
		}
		return stringBuilder.toString();
	}
	
}
