package bots.ProductPlacementBot;

import java.util.Base64;

import bots.CalendarBot.CalDateFormat;
import bots.CalendarBot.CurrentDate;
import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class ProductPlacementBot extends Bot {
	private int repeatTime;
	private int repeatTimeTags;
	private int lastPlayed;
	private Advertisement[] playList;
	int currentIndex;
	AdvertisementSearcher advertSearcher;
	
	public void initializeBot() {
		repeatTime = 1000;
		repeatTimeTags = 1;
		lastPlayed = 0;
		playList = new Advertisements().getAdvertisements();
		Mix.mergeMix(playList);
		currentIndex = 0;
		advertSearcher = new AdvertisementSearcher(playList);
	}
	
	public void messageReceived (Message message) {
		String msg = message.getPlainTextMessage();
		
		//check for command
		if (msg.toLowerCase().startsWith("?ppb ")) {
			if (new PasswordChecker(msg.substring(5)).checkPassword()) {
				int newRepeatTime = -2;
				int i = 1;
				while (newRepeatTime == -2 && msg.length() - i >= 12) {
					try {
						newRepeatTime = Integer.parseInt(msg.substring(12, msg.length() - i));
					} catch (NumberFormatException e) {
						i++;
					}
				}
				// change repeat time of product placement
				if (newRepeatTime != -2 && msg.charAt(11) == 'a') {
					if (newRepeatTime == -1) {
						repeatTime = Integer.MAX_VALUE;
					}
					else {
						repeatTime = newRepeatTime;
					}
				}
				// change repeat time of product placements by tags
				if (newRepeatTime != -2 && msg.charAt(11) == 't') {
					if (newRepeatTime == -1) {
						repeatTimeTags = Integer.MAX_VALUE;
					}
					else {
						repeatTimeTags = newRepeatTime;
					}
				}
			}
			else {
				sendMessage(new Message(message.getGroup(), "Access denied", MessageFormat.PLAIN_TEXT));
			}
		}
		
		msg = message.getSender() + " " + msg;
		
		//if playList is empty
		if (playList.length == 0) return;
		
		//play Product Placement by Tag
		Advertisement advertisement = advertSearcher.searchAdvertisement(msg);
		if (advertisement != null && ((advertisement.getLastPlayed() + repeatTimeTags) <= currentTime())) {
			sendMessage(advertisement, message);
			advertisement.setLastPlayed(currentTime());
		}
		
		//play Product Placement by playList
		else if ((lastPlayed + repeatTime) <= currentTime()) {
			sendMessage(playList[currentIndex], message);
			lastPlayed = currentTime();
			if (currentIndex < playList.length - 1) {
				currentIndex++;
			}
			else {
				currentIndex = 0;
			}
		}
	}
	
	private void sendMessage (Advertisement advertisement, Message message) {
		byte[] image = advertisement.getImage();
		if (image != null) {
			sendMessage(new Message(message.getGroup(), "\n<img src=\"data:image/jpeg;base64,"
					+ Base64.getEncoder().encodeToString(image) + "\" alt=\""
					+ ((advertisement.getInfo() != null) ? advertisement.getInfo() : "")
					+ "\" width=\"320\" height=\"240\"/>", MessageFormat.XHTML));
		}
		if (advertisement.getInfo() != null) {
			sendMessage(new Message(message.getGroup(), advertisement.getInfo(), MessageFormat.PLAIN_TEXT));
		}
	}
	
	private int currentTime() {
		return CalDateFormat.dateToMin(CurrentDate.getDateWTime(), 1970);
	}
}
