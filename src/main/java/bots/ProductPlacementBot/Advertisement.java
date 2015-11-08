package bots.ProductPlacementBot;

import java.net.URL;
import java.net.URLConnection;

import bots.ShowMeBot.ShowMeBot;

public class Advertisement {
	
	
	/**
	 * link to image which will be shown in the console (null = no image)
	 */
	private URL image;
	
	/**
	 * message of Advertisement which will be shown in the console (null = no message)
	 */
	private String info;
	
	/**
	 * Advertisement will be played if console-message contains a tag
	 * (ONLY USE LOWER CASES - DONT USE UPPER CASES)
	 */
	private String[] tags;
	
	private int lastPlayed = 0;
	
	public Advertisement(String picture, String info, String[] tags) {
		try {
			this.image = new URL(picture);
		} catch (Exception e) {
			this.image = null;
		}
		this.info = info;
		this.tags = new String[tags.length];
		for (int i = 0; i < tags.length; i++) {
			this.tags[i] = tags[i];
		}
	}
	
	public final byte[] getImage() {
		try {
			URLConnection connection = image.openConnection();
			byte[] image;
			image = new ShowMeBot().resizeImage(connection, 150, 150);
			return image;
		} catch (Exception e) {
			return null;
		}
	}
	
	public final String getInfo() {
		return info;
	}
	
	public final String[] getTags() {
		return tags;
	}
	
	public final int getLastPlayed() {
		return lastPlayed;
	}
	
	public final void setLastPlayed(int lastPlayed) {
		this.lastPlayed = lastPlayed;
	}
	
	public final int numberOfTags() {
		return tags.length;
	}
	
}
