package bots.TUMCanteenBot;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Message;
import bridgempp.bot.wrapper.Schedule;


public class TUMCanteenBotTest {
	
	private static final String[] testCases = {
		"some text", "?command 123", "?", "", " ", "?listcanteens", "?listcanteens garbagetext123123??",
		"?canteen 422", "?canteen", "?canteen ", "?canteen b", "?canteen    422"
	};
	
	private TUMCanteenBot bot;
	private Consumer<String> botOutputConsumer;
	private String lastOutput;
	
	@BeforeClass
	public static void classSetup() {
		Schedule.startExecutorService();
	}
	
	@AfterClass
	public static void afterClass() {
		Schedule.stopExecutorService();
	}
	
	@Before
	public void setUp() {
		botOutputConsumer = (t) -> lastOutput = t;
		lastOutput = null;
		
		bot = new TUMCanteenBot();
		bot.setDebugOutputReader(botOutputConsumer);
		bot.initializeBot();
	}
	
	@After
	public void after() {
		bot.deinitializeBot();
	}

	@Test
	public void testUninterestingInput() {
		final String[] testCases = {
			"some text", "?command 123", "?", "", " "
		};
		
		for (String testString : testCases) {
			lastOutput = null;
			Message message = new Message("GROUP", testString, MessageFormat.PLAIN_TEXT);
			bot.messageReceived(message);
			
			Assert.assertEquals("Should not output anything", null, lastOutput);
		}
	}
	
	@Test
	public void testCanteenErrorMessage() {
		final String[] testCases = {
			"?canteen abc", "?canteen h132", "?canteen -123", "?canteen", "?canteen     "
		};
		
		for (String testString : testCases) {
			lastOutput = null;
			Message message = new Message("GROUP", testString, MessageFormat.PLAIN_TEXT);
			bot.messageReceived(message);
			
			if (lastOutput == null) {
				Assert.fail("Bot did not send an error message");
			}
			
			Assert.assertTrue("Should send an error message. Message was: " + lastOutput, lastOutput.contains("To see a list of all available"));
		}
	}
	
	@Test
	public void testCanteenList() {
		Message message = new Message("GROUP", "?listcanteens", MessageFormat.PLAIN_TEXT);
		bot.messageReceived(message);
		
		Assert.assertTrue("Should output all 19 canteens. Output: " + lastOutput, lastOutput.split("\n").length == 19);
	}
	
	@Test
	public void testCanteenQuery() {
		Message message = new Message("GROUP", "?canteen 422", MessageFormat.PLAIN_TEXT);
		bot.messageReceived(message);
		
		Assert.assertTrue("Should output something. Output: " + lastOutput, lastOutput != null && lastOutput.length() > 10);
	}
	
	@Test
	public void testDaily() {
		Message message;
		for (int i = 0; i < 2; i++) {
			message = new Message("GROUP", "?canteendaily 422", MessageFormat.PLAIN_TEXT);
			bot.messageReceived(message);
			Assert.assertEquals("Should output that daily messages for 422 have been enabled", "Daily messages scheduled for canteen 422", lastOutput);
		}
		
		for (int i = 0; i < 2; i++) {
			message = new Message("GROUP", "?canteendisabledaily 422", MessageFormat.PLAIN_TEXT);
			bot.messageReceived(message);
			Assert.assertEquals("Should output that daily messages for 422 have been disabled", "Disabled daily messages for canteen 422", lastOutput);
		}
	}
	
}
