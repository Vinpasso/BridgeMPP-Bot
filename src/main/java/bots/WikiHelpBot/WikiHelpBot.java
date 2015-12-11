package bots.WikiHelpBot;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiHelpBot {

	public static final Pattern askingPattern = Pattern.compile(
			"(?i)Wer ist |Was ist ein |Was ist eine |Was ist der |Was ist die |Was ist das |Was ist |Was sind |What is an |What is a |What is |What are ");
	public static final Pattern greetingPattern = Pattern.compile("guten morgen.*|good morning.*");
	public static final Pattern NoLineBreakSpacePattern = Pattern.compile("&#160;");

	public static final String[] wikiLangDomains = { "de", "bar", "en", "es", "fr", "it", "cz", "ru", "pl", "ja", "zh",
			"pt" };

	private WikipediaAPIHandler apiHandler;

	public WikiHelpBot(String wikiLangDomain) {
		this.apiHandler = new WikipediaAPIHandler(wikiLangDomain);
	}

	private String getWikiHelp(String topic, String domain) {
		String wikiResponseString = null;
		if (domain != null) {
			apiHandler.wikiLangDomain = domain.trim();
			wikiResponseString = apiHandler.getWikiSummary(topic);
		} else {
			for (int i = 0; i < wikiLangDomains.length && wikiResponseString == null; i++) {
				apiHandler.wikiLangDomain = wikiLangDomains[i];
				wikiResponseString = apiHandler.getWikiSummary(topic);
			}
		}
		if (wikiResponseString != null) {
			try {
				String wikiDecodedResponseString = URLDecoder.decode(wikiResponseString, "UTF-8");
				return WikipediaAPIHandler.doReturnHTMLText ? wikiDecodedResponseString
						: NoLineBreakSpacePattern.matcher(wikiDecodedResponseString).replaceAll("\u00A0");
			} catch (Exception e) {
				return WikipediaAPIHandler.doReturnHTMLText ? wikiResponseString
						: wikiResponseString.replace("\u00A0", "");
			}
		}
		return null;
	}

	public String needWikiResponse(String msg) {
		Matcher askingMatcher = askingPattern.matcher(msg);
		if (askingMatcher.find()) {
			int IndexOfQMark = msg.indexOf('?');
			int start = askingMatcher.end();
			return getWikiHelp(msg.substring(start - 1, IndexOfQMark > start ? IndexOfQMark : msg.length()).trim(),IndexOfQMark+1 >= msg.length() ? null : msg.substring(IndexOfQMark+1));
		}
		return null;

	}

	public String getWikiBotWisdom(String message) {

		if (greetingPattern.matcher(message.trim().toLowerCase()).matches()) {
			return "Der WikiHelpBot grüßt euch, Erdbewohner";
		} else {
			return needWikiResponse(message);
		}
	}

	public static void main(String args[]) throws IOException {

		boolean exit = false;
		WikiHelpBot helpBot = new WikiHelpBot("de");
		Scanner reader = new Scanner(System.in);

		while (!exit) {
			String line = reader.nextLine();
			// exit = line.equals("exit");
			String wikiHelp = helpBot.getWikiBotWisdom(line);

			if (wikiHelp != null) {
				System.out.println("Wikipedia says:");
				System.out.println(wikiHelp);
			}
		}
		reader.close();
	}
}
