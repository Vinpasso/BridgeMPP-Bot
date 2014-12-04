package bots.AdventureBot;

import java.io.IOException;







import java.util.Iterator;
import java.util.LinkedList;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.KeyboardEvent;

import bots.AdventureBot.StringUtils.diff_match_patch;
import bots.AdventureBot.StringUtils.diff_match_patch.Diff;
import bots.AdventureBot.StringUtils.diff_match_patch.LinesToCharsResult;
import bridgempp.bot.wrapper.BotConsoleTester.ConsoleBot;
import bridgempp.bot.wrapper.BotWrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper.Message;

public class TextAdventuresAdventureBot extends ConsoleBot {

	private WebClient webclient;
	private HtmlPage htmlPage;
	private diff_match_patch diff;
	private String cached;
	
	@Override
	public void initializeBot() {
		
	}

	@Override
	public void messageRecieved(Message message) {
		if(message.getMessage().startsWith("?adventure "))
		{
			String command = message.getMessage().substring(11).trim();
			if(command.contains(" "))
			{
				command = command.substring(0, command.indexOf(" "));
			}
			interpretCommand(command, message);
		}
		else if(message.getMessage().startsWith("?ad") && htmlPage != null)
		{
			String command = message.getMessage().substring(4).trim();
			enterQuery(command);
			sendMessage(new Message(message.getGroup(), getResponse(), "Plain Text"));
		}
	}

	private void interpretCommand(String command, Message message) {
		switch(command)
		{
		case "play":
			cached = "";
			initializeGame(message.getMessage().substring(message.getMessage().lastIndexOf(" ") + 1));
			sendMessage(new Message(message.getGroup(), "Welcome to TextAdventures", "Plain Text"));
			sendMessage(new Message(message.getGroup(), getResponse(), "Plain Text"));
			break;
		}
		
	}
	
	private void initializeGame(String location)
	{
		diff = new diff_match_patch();
		webclient = new WebClient(BrowserVersion.FIREFOX_24);
		webclient.getOptions().setThrowExceptionOnScriptError(false);
		try {
			htmlPage = webclient.getPage(location);
			webclient.waitForBackgroundJavaScript(10000);
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void enterQuery(String query)
	{
		try {
			htmlPage.getFocusedElement().type(query);
			htmlPage.getFocusedElement().type(KeyboardEvent.DOM_VK_RETURN);
			webclient.waitForBackgroundJavaScript(10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getResponse()
	{
		String text = htmlPage.asText();
		LinesToCharsResult lines = diff.diff_linesToChars(cached, text);
		LinkedList<Diff> differences = diff.diff_main(lines.chars1, lines.chars2, false);
		diff.diff_charsToLines(differences, lines.lineArray);
		String buffer = "";
		Iterator<Diff> iterator = differences.iterator();
		while(iterator.hasNext())
		{
			Diff diff = iterator.next();
			switch(diff.operation)
			{
			case DELETE:
				//buffer += "\n- " + diff.text;
				break;
			case EQUAL:
				break;
			case INSERT:
				buffer += "\n+ " + diff.text;
				break;
			default:
				break;
			}
		}
		cached = text;
		return buffer.replaceAll("(\\s)\\s+", "$1");
	}
	
}