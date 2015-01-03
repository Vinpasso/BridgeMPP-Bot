package bots.rXkcdBot;

import bridgempp.bot.wrapper.BotWrapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by Miguel on 23.12.2014.
 * <p/>
 * We are using the Website http://relevantxkcd.appspot.com/ which wheighs a String by its explainxkcd site and returns
 * an array through http://relevantxkcd.appspot.com/process?action=xkcd&query=%s % input we can iterate through the
 * array skipping the int every step and display the image at http://www.explainxkcd.com/%s % value.
 */
public class RelevantXkcdBot extends BotWrapper.Bot {

    private int position;
    private URL currUrl;
    private int currComic;

    @Override
    public void initializeBot() {

    }

    @Override
    public void messageRecieved(BotWrapper.Message message) {
        if (message.getMessage().startsWith("?xkcd ")) {
            String argument = message.getMessage().substring(6);
            switch (argument) {
                case "explain":
                    sendMessage(new BotWrapper.Message(message.getGroup(), "http://www.explainxkcd.com/wiki/index.php/" + currComic, "Plain Text"));
                    break;
                case "next":
                    try {
                        Scanner comics = new Scanner(currUrl.openStream());
                        comics.nextLine();
                        comics.nextLine();
                        for (int i = 0; i < position; i++) {
                            if (!comics.hasNext()) {
                                sendMessage(new BotWrapper.Message(message.getGroup(), "There are no more relevant comics!", "Plain Text"));
                                comics.close();
                                return;
                            }
                            comics.nextLine();
                        }
                        if (!comics.hasNext()) {
                            sendMessage(new BotWrapper.Message(message.getGroup(), "There are no more relevant comics!", "Plain Text"));
                            comics.close();
                            return;
                        }
                        currComic = comics.nextInt();
                        sendMessage(new BotWrapper.Message(message.getGroup(), "http://www.explainxkcd.com" + comics.nextLine().trim(), "Plain Text"));
                        position++;
                        comics.close();
                    } catch (IOException e) {
                        sendMessage(new BotWrapper.Message(message.getGroup(), "An error has ocurred: " + e, "Plain Text"));
                    }
                    break;
                default:
                    try {
                        currUrl = new URL("http://relevantxkcd.appspot.com/process?action=xkcd&query=" + URLEncoder.encode(argument, "UTF-8"));
                        Scanner comics = new Scanner(currUrl.openStream());
                        comics.nextLine();
                        comics.nextLine(); // Skip weight arguments
                        currComic = comics.nextInt(); //Skip first part
                        position = 0;
                        sendMessage(new BotWrapper.Message(message.getGroup(), "http://www.explainxkcd.com" + comics.nextLine().trim(), "Plain Text"));
                        position++;
                        comics.close();
                    } catch (IOException e) {
                        sendMessage(new BotWrapper.Message(message.getGroup(), "An error has ocurred: " + e, "Plain Text"));
                    }
                    break;
            }
        }
    }
}
