package bots.QuizBot.parsers;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class HearthstoneCardParser
{
	static String[] patternStrings = { "<h1 class=\"heading-size-1\">(.*?)</h1>",
			"var a = \\$WH\\.ge\\('cardsoundlink0'\\), audio=\\$WH\\.ge\\('cardsound0'\\);\\s*\\$WH\\.Tooltip\\.simple\\(a, \"<i>(.*?)<\\/i>\", null, true\\);",
			"var a = \\$WH\\.ge\\('cardsoundlink1'\\), audio=\\$WH\\.ge\\('cardsound1'\\);\\s*\\$WH\\.Tooltip\\.simple\\(a, \"<i>(.*?)<\\/i>\", null, true\\);",
			"var a = \\$WH\\.ge\\('cardsoundlink2'\\), audio=\\$WH\\.ge\\('cardsound2'\\);\\s*\\$WH\\.Tooltip\\.simple\\(a, \"<i>(.*?)<\\/i>\", null, true\\);",
			"<span class=\"hearthstone-cost\" title=\"Cost\">(.*?)<\\/span>", 
			"<span class=\"hearthstone-attack\" title=\"Attack\">(.*?)<\\/span>",
			"<span class=\"hearthstone-health\" title=\"Health\">(.*?)<\\/span>",
			"<meta name=\"description\" content=\"([^\"]+?)\">" };

	public static void main(String[] args) throws Exception
	{
		PrintStream ps = new PrintStream(new File("parsed.txt"));
		Pattern idPattern = Pattern.compile("\\{\"id\":([^,]*?),\"image\":");
		Pattern[] patterns = new Pattern[patternStrings.length];
		for (int p = 0; p < patterns.length; p++)
		{
			patterns[p] = Pattern.compile(patternStrings[p]);
		}
		String searchString = IOUtils.toString(new URL("http://www.hearthhead.com/cards#text"));
		Matcher sMatcher = idPattern.matcher(searchString);
		int counter = 0;
		while (sMatcher.find())
		{
			String page = IOUtils.toString(new URL("http://www.hearthhead.com/card=" + sMatcher.group(1)));
			for (int p = 0; p < patterns.length; p++)
			{
				Matcher matcher = patterns[p].matcher(page);
				boolean found = matcher.find();
				for (int g = 0; g < matcher.groupCount(); g++)
				{
					String string = ((found) ? matcher.group(1).replaceAll(";", ",") : "") + ((p + 1 < patterns.length) ? ";" : "");
					System.out.print(string);
					ps.print(string);
				}
			}
			System.out.println("\nProcessed: " + ++counter + " cards");
			ps.println();
			ps.flush();
			Thread.sleep(10000);
		}
		ps.close();
	}

}
