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
	public static Sender sender = new Sender();


	public void initializeBot() {
		repeatTime = 30;
		repeatTimeTags = 10;
		lastPlayed = 0;
		playList = new Advertisements().getAdvertisements();
		Mix.mergeMix(playList);
		currentIndex = 0;
		advertSearcher = new AdvertisementSearcher(playList);
		sendMessage(new Message("tumspam", "Product Placement Bot: Wenig Publikum aber viel Werbung.", MessageFormat.PLAIN_TEXT));
	}

	public void messageReceived (Message message) {

		/**sender*/
		sender.add(message.getSender());
		/**end sender*/

		String msg = message.getPlainTextMessage();

		//check for command
		if (msg.toLowerCase().startsWith("?ppb ")) {

			//command: getSender
			if (msg.substring(5).equals("getSender")) {
				sendMessage(new Message(message.getGroup(), sender.toString(), MessageFormat.PLAIN_TEXT));
			}

			else if (new PasswordChecker(msg.substring(5)).checkPassword()) {
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
					sendMessage(new Message(message.getGroup(), "Mission accomplished", MessageFormat.PLAIN_TEXT));
				}
				// change repeat time of product placements by tags
				if (newRepeatTime != -2 && msg.charAt(11) == 't') {
					if (newRepeatTime == -1) {
						repeatTimeTags = Integer.MAX_VALUE;
					}
					else {
						repeatTimeTags = newRepeatTime;
					}
					sendMessage(new Message(message.getGroup(), "Mission accomplished", MessageFormat.PLAIN_TEXT));
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
			lastPlayed = currentTime();
			advertisement.setLastPlayed(currentTime());
			sendMessage(advertisement, message);
		}

		//play Product Placement by playList
		else if ((lastPlayed + repeatTime) <= currentTime()) {
			lastPlayed = currentTime();
			playList[currentIndex].setLastPlayed(currentTime());
			sendMessage(playList[currentIndex], message);
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
			sendMessage(new Message(message.getGroup(), "\n<img src=\"data:image/png;base64,"
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
