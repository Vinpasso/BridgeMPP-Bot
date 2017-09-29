package bots.ProductPlacementBot;

import java.util.ArrayList;

public class AdvertisementSearcher {
	String[] tags;
	Advertisement[] adverts;
	
	public AdvertisementSearcher(Advertisement[] adverts) {
		this.adverts = adverts;
		int numberOfTag = 0;
		for (int i = 0; i < this.adverts.length; i++) {
			numberOfTag += (2 * this.adverts[i].numberOfTags());
		}
		tags = new String[numberOfTag];
		
		//assign tags
		numberOfTag = 0;
		for (int i = 0; i < adverts.length; i++) {
			for (int j = 0; j < adverts[i].getTags().length; j++) {
				for (int k = 0; k < tags.length; k++) {
					if (tags[k] == null) {
						//add tag
						tags[k] = adverts[i].getTags()[j];
						tags[k + 1] = "" + i;
						break;
					}
					if (adverts[i].getTags()[j].equals(tags[k])) {
						shiftArray(tags, 1, k + 1);
						tags[k + 1] = "" + i;
						break;
					}
				}
			}
		}	
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public Advertisement searchAdvertisement (String msg) {
		msg = msg.toLowerCase();
		for (int i = 0; i < tags.length && tags[i] != null; i++) {
			try {
				Integer.parseInt(tags[i]);
			}
			catch (NumberFormatException e) {
				if (msg.contains(tags[i])) {
					Advertisement[] advertisement = getAdvertsByTag(i);
					Advertisement lastplayed = advertisement[0];
					for (int j = 1; j < advertisement.length; j++) {
						if (advertisement[j].getLastPlayed() < lastplayed.getLastPlayed()) {
							lastplayed = advertisement[j];
						}
					}
					return lastplayed;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param indexOfTag
	 * @return
	 */
	public Advertisement[] getAdvertsByTag (int indexOfTag) {
		ArrayList<Advertisement> adverts = new ArrayList<>();
		for (int i = indexOfTag + 1; i < tags.length; i++) {	
			try {
				adverts.add(this.adverts[Integer.parseInt(tags[i])]);
			} catch (Exception e) {
				break;
			}
		}
		return adverts.toArray(new Advertisement[adverts.size()]);
	}
	
	/**
	 * shifts array to the right
	 * @param array
	 * @param steps
	 * @param begin 
	 */
	public void shiftArray (String[] array, int steps, int begin) {
		for (int i = 0; i < steps; i++) {
			for (int j = array.length - 1; j > begin; j--) {
				array[j] = array[j - 1];
			}
		}
	}
}
