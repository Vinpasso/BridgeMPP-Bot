package bots.WikiHelpBot;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiHelpBot {

	public static final Pattern askingPattern = Pattern.compile("(?i)Wer ist |Was ist ein |Was ist eine |Was ist der |Was ist die |Was ist das |Was ist |Was sind |What is an |What is a |What is |What are ");
	public static final Pattern greetingPattern = Pattern.compile("guten morgen.*|good morning.*");
	public static final String[] wikiLangDomains = {"de","bar","en","es","fr","it","cz"};
	
	private WikipediaAPIHandler apiHandler;
	
	public WikiHelpBot(String wikiLangDomain) {
		this.apiHandler = new WikipediaAPIHandler(wikiLangDomain);
	}



	private String getWikiHelp(String topic) {
		String wikiResponseString = null;
		for(int i = 0; i < wikiLangDomains.length && wikiResponseString == null;i++){
			apiHandler.wikiLangDomain = wikiLangDomains[i];
			wikiResponseString = apiHandler.getWikiSummary(topic);
		}
		if (wikiResponseString != null) {
			try {
				return URLDecoder.decode(wikiResponseString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return wikiResponseString;
			}
		}
		return null;
	}

	
	public String needWikiResponse(String msg) {
		Matcher askingMatcher = askingPattern.matcher(msg);
		if (askingMatcher.find()) {
			int IndexOfQMark = msg.indexOf('?');
			int start = askingMatcher.group().length();
			return getWikiHelp(msg.substring(start - 1, IndexOfQMark > start ? IndexOfQMark : msg.length()).trim());
		}
		return null;

	}
	
	public String getWikiBotWisdom(String message){
		
		if(greetingPattern.matcher(message.trim().toLowerCase()).matches()){
			return "Der WikiHelpBot grüßt euch, Erdbewohner";
		}
		else{
			return needWikiResponse(message);
		}
	}
	
	public static void main(String args[]) throws IOException {

		boolean exit = false;
		WikiHelpBot helpBot = new WikiHelpBot("de");
		Scanner reader = new Scanner(System.in);

		while (!exit) {
			String line = reader.nextLine();
			//exit = line.equals("exit");
			String wikiHelp = helpBot.getWikiBotWisdom(line);

			if (wikiHelp != null) {
				System.out.println("Wikipedia says:");
				 System.out.println(wikiHelp);
			}
		}
		reader.close();
	}
}
