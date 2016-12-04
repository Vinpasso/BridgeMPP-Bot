package bots.ProductPlacementBot;

import bots.CalendarBot.CalDateFormat;
import bots.CalendarBot.CurrentDate;

public class PasswordChecker {
	private String password;
	private boolean correct = false;
	
	public PasswordChecker (String password) {
		this.password = password;
	}
	public boolean checkPassword () {
		part1();
		part2();
		part3();
		part4();
		part5();
		part6();
		part7();
		part8();
		part9();
		return !correct;
	}
	
	private void part1 () {
		switch (password.charAt(0)) {
		case 78:
			password = "i" + password.substring(1);
			break;
		case 76:
			correct = true;
			break;
		case 109:
			password = "a" + password.substring(1);
			break;
		case 77:
			password = "a" + password.substring(1);
			break;
		default:
			password = "f" + password.substring(1);
			break;
		}
	}
	
	private void part2 () {
		switch (password.charAt(1)) {
		case 84:
			password = "t" + password.substring(1);
			break;
		case 120:
			password = "t" + password.substring(1);
			break;
		case 109:
			password = "q" + password.substring(1);
			break;
		case 78:
			correct = true;
			break;
		default:
			correct = correct || false;
			break;
		}
	}
	
	public void part3 () {
		int x = 0;
		for (int i = 0; i <= password.charAt(3); i++) {
			x = x + ((password.charAt(3) + i) * i);
		}
		x = x % ('x' + 8);
		correct = correct || x != 32;
	}
	
	private void part4 () {
		int time = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), 1970);
		int x = password.charAt(0);
		x = ((((x / 2) / 2) / 2) / 2);
		int y = (int) ((((2 * x + x * Math.pow(x, x - 1)) * Math.pow(x, 3)) - ((x * x + Math.pow(x, x)) * 3 * x)) / (Math.pow(x, 6)));
		correct = correct || time == y ? true : y != 198;
	}
	
	private void part5 () {
		int c = CurrentDate.getTime().charAt(3);
		correct = correct || c + 17 != password.charAt(2);
	}
	
	private void part6 () {
		int c = ((CurrentDate.getTime().charAt(1) - 48) * (CurrentDate.getTime().charAt(4) - 48)) % 5;
		c = (c + 1089) % 128;
		correct = correct || c != password.charAt(4);
	}
	private void part7 () {
		int time = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), 1970);
		time = ((time % 1440) / 600) + 65;
		correct = correct || time != password.charAt(1);
	}
	
	private void part8 () {
		int c = CurrentDate.getTime().charAt(0) - 48;
		int x = password.charAt(2) - (CurrentDate.getTime().charAt(3) + 17) - 6 + c;
		correct = correct || x >= 0;
	}
	
	private void part9 () {
		correct = correct || !(part10(password.charAt(5), 1) == 847249408);
	}
	
	private int part10 (int x, int i) {
		if (x == 0 || i >= 1000) {
			return 1;			
		}
		if (x * i >= 128) {
			return x * part10((int) (Math.exp(x)) % 128, ++i);
		}
		if (x * i >= 64) {
			return x * part10((int) (127 * Math.sin(x)), ++i);
		}
		return x * part10((x * i) % 128, ++i);
	}
}
