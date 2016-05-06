package bots.KappaBot;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import bridgempp.util.ImageEdit;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Scanner;

/**
 * Created by alex on 06.05.16.
 */
public class KappaBotBridgeMPPIntegration extends Bot {

    KappaBot bot = new KappaBot();


    @Override
    public void initializeBot() {
        bot.refresh_emotes();
    }

    @Override
    public void messageReceived(Message message) {
        KappaBot.EmoteInfo[] emoteUrls = bot.getEmpoteUrls(message.getPlainTextMessage());
        for(KappaBot.EmoteInfo emote : emoteUrls){
            byte[] image;
            try{
            sendMessage(new Message(message.getGroup(),"You seem to lack an important Emote, please help yourself: ",MessageFormat.XHTML));
            sendMessage(
                new Message(message.getGroup(),
                    ImageEdit.imageAsTag(KappaBot.getUrlFromImageID(emote.image_id),32,32,emote.emotename,"PNG"), MessageFormat.XHTML));
        }

        catch(Exception e){

        }
        }
    }
}
