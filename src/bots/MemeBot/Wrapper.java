package bots.MemeBot;

import bots.MemeBot.dto.Meme;
import bots.MemeBot.service.MemeService;
import bots.config.MainModule;
import bridgempp.bot.fancy.FancyAsyncBot;
import bridgempp.bot.fancy.basecommands.Help;
import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.Scanner;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class Wrapper extends Bot {
    @Inject MemeService memeService;


    public static void main(String[] args) {
        Injector i = Guice.createInjector(new MainModule());
        class WrapperTest extends Wrapper {
            @Override
            public void sendMessage(Message message) {
                System.out.println(message.getMessage());
            }
        }
        Wrapper wrapper = new WrapperTest();
        i.injectMembers(wrapper);
        wrapper.initializeBot();
        Scanner scan = new Scanner(System.in);
        String line;
        while (!(line = scan.nextLine()).equals("exit")) {
            wrapper.messageReceived(new Message("uiae", line, MessageFormat.PLAIN_TEXT));
        }
    }

    @Override
    public void initializeBot() {

    }

    @Override
    public void messageReceived(Message message) {
        Meme meme;
        if ((meme = memeService.getMeme(message.getPlainTextMessage())) != null) {
            new Message(message.getGroup(), meme.toHtml(), MessageFormat.HTML);
        }
    }
}
