package bots.ProductPlacementBot;

/**
 * This class adds all advertisements to the Product Placement Bot
 * 
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
			
			//witze
			new Advertisement(null, "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nWolltest du schon immer Leute nerven? Dann schalte jetzt Werbung ueber Product Placement Bot mit vielen Leerzeilen davor!\nProduct Placement Bot: Wenig Publikum aber viel Werbung.",
					new String[] {
					"bernie",
					"werbung"
			}),
			
			//unfall
			new Advertisement(null, "Sicherheitslücken passieren alltaeglich. Jaro sorgt dafuer, dass sie gefunden werden. Jetzt anfragen und der naechste Exploit kommt schon morgen.",
					new String[] {
					"sicherheit",
					"bug",
					"jaro",
					"professional",
					"exploit"
			}),
			
			//pesto
			new Advertisement(null, "Rache ist Pesto! Raeche dich jetzt mit Adrians Pesto und eine Lebensmittelvergiftung ist garantiert! (Kann toedlich sein)",
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
			}),
			new Advertisement(null, "Lust auf Turbinengeräusche? Bernie stellt seinen Rechner auch gerne bei dir auf! (Mit der Hoffnung, dass Alex irgendwann versehentlich hineingeraet)",
				new String[] {
						"flugzeug",
						"turbine",
						"fliegen"
			}),
			new Advertisement(null, "Keine Ahnung was ein Callstack ist? Prof. Brüggeman-Klein erklärt es dir!",
				new String[]{
						"callstack",
						"brüggeman",
						"klein",
						"java",
						"programmieren",
						"hacken"
			}),
			new Advertisement(null, "Lust auf Papageien, die ohne Unterlass plappern? tippe nun ?parrot buy und du bekommst deinen Papageien für umsonst!",
				new String[]{
						"papagei",
						"plappern",
						"spam",
						"parrot"
			}),
			new Advertisement(null, "Süchtig nach Klebstoff und du kommst davon nicht mehr weg? Lösungsmittel in Familiengröße jetzt reduziert!",
				new String[]{
						"klebstoff",
						"lösungsmittel",
						"sucht",
			}),
			new Advertisement(null, "Probleme beim Einschlafen? Mathevorlesungen an der Universität helfen, sagen Studenten",
				new String[]{
						"einschlafen",
						"mathe",
			}),
			new Advertisement(null, "Haben sie ein unendliches Programm? Fragen sie Christian und er wird es zum terminieren bringen!",
				new String[]{
						"unendlich",
						"programm",
						"christian",
						"terminieren"
			}),
			new Advertisement(null, "Wissen sie es gute Dinge zu schätzen? Bewerben sie sich jetzt als Immobilienhändler!"),
				new String[]{
						"haus",
						"schätzen",
						"bewerben",
						"immobilie",
						"wert"
				}
	};

	public Advertisement[] getAdvertisements() {
		return adverts;
	}
}
