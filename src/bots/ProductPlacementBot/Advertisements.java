package bots.ProductPlacementBot;

/**
 * This class adds all advertisements to the Product Placement Bot
 * @author Bernie
 *
 */
public class Advertisements {
	private final Advertisement[] adverts = {
			/*add new advertisement here (separated by commas): */
			
			
			//FlyTum
			new Advertisement("http://home.in.tum.de/~gaidab/html-data/02/ppb/FlyTum.jpg", "flytum",
					new String[] {
					"flug",
					"flight",
					"cessna",
					"landshut",
					"deutschland",
					"europa"
			}),
			
			//Alex
			new Advertisement(null, "Nutten gesucht! Du bist sexgeil, schamlos und auf der Suche nach dem grossen Geld, dann bewirb dich jetzt als Prostituierte bei Zuhaelter Alex.",
					new String[] {
					"alex",
					"thole"
			}),
			
			//unfall
			new Advertisement(null, "Unfaelle passieren alltaeglich. Jaro sorgt dafuer, dass es auch so bleibt. Jetzt anfragen und der naechste Unfall passiert schon morgen.",
					new String[] {
					"unfall",
					"kill",
					"dead",
					"professional"					
			}),
			
			//pesto
			new Advertisement(null, "Rache ist Pesto! Raeche dich jetzt mit Adrians Pesto und eine Lebensmittelvergiftung ist garantiert! \(Kann toedlich sein\)",
					new String[] {
					"pesto",
					"rache",
					"revenge",
					"payback",
			}),
			
			//Werbung
			new Advertisement(null, "Schalten auch Sie ihre Werbung hier auf BridgeMPP. Product Placement Bot: Wenig Publikum aber viel Werbung.",
					new String[] {
					"werbung",
					"advertisement",
					"publikum"
			}),
			
			//Schlafwandler
			new Advertisement(null, "Schlafwandler vermisst! Vermisst wird ein junger Mann mit dunklen Locken, Bart und Brille, zockt gerne Dota und Magic. Zuletzt gesehen im Bett like always.",
					new String[] {
					"schlafwandler",
					"vermisst",
					"dota",
					"magic",
					"bett",
					"bed",
					"sleep",
					"schlafen"
			})
	};
	
	public Advertisement[] getAdvertisements() {
		return adverts;
	}
}
