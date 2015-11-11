package bots.ProductPlacementBot;

import java.util.ArrayList;
import java.util.List;

public class Sender {
	List<String> sender;


	public Sender (){
		sender = new ArrayList<>();
	}
	
	public void add(String msg) {
		if (!sender.contains(msg)) { 
			sender.add(msg);
		}
	}
	
	public String toString () {
		String all = "";
		for (int i = 0; i < sender.size(); i++) {
			all += " " + sender.get(i);
		}
		return all;
	}
}
