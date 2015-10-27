package bots.ProductPlacementBot;

/**
 * This class adds all advertisements to the Product Placement Bot
 * @author Bernie
 *
 */
public class Advertisements {
	private final Advertisement[] adverts = {
			/*add new advertisement here (separated by commas): */
			
			/*test2
			new Advertisement(null, "dies ist Test2 aus tum",
					new String[] {
					"tes2",
					"2tes"
					}),
			*/
			
			//FlyTum
			new Advertisement("http://home.in.tum.de/~gaidab/html-data/02/ppb/FlyTum.jpg", "flytum",
					new String[] {
					"flug",
					"cessna",
					"landshut",
					"deutschland",
					"europa",
					"tum"
			})
			
	};
	
	public Advertisement[] getAdvertisements() {
		return adverts;
	}
}
