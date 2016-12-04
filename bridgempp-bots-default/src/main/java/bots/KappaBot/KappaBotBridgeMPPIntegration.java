package bots.KappaBot;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import bridgempp.util.ImageEdit;
import java.util.regex.Pattern;

/**
 * Created by alex on 06.05.16.
 */
public class KappaBotBridgeMPPIntegration extends Bot {

    KappaBot bot = new KappaBot();
    public static final String kappaCommandPrefix = "?twitch ";
    public static final String kappaEmoteSizeCommand = "size";
    public static final String kappaRefreshCommand = "refresh";


    public static final Pattern isKappaCommand = Pattern.compile("\\A\\"+kappaCommandPrefix);


    @Override
    public void initializeBot() {
            this.properties.setProperty("old_emotes", bot.refresh_emotes(properties.getProperty("old_emotes","{emotes:{}}")).toString(4));
    }

    @Override
    public void messageReceived(Message message) {
        if (isKappaCommand.matcher(message.getPlainTextMessage()).find()) {
            String remMessage = message.getPlainTextMessage().substring(kappaCommandPrefix.length());
            if (remMessage.startsWith(kappaEmoteSizeCommand)) {
                sendMessage(new Message(message.getGroup(), "Currently loaded Emotes: " + bot.getEmoteCount(), MessageFormat.XHTML));
            }
            if (remMessage.startsWith(kappaRefreshCommand)) {
                this.properties.setProperty("old_emotes",bot.refresh_emotes(properties.getProperty("old_emotes","{emotes:{}}")).toString(4));
                this.saveProperties();
                sendMessage(new Message(message.getGroup(), "All Emotes successfully been loaded", MessageFormat.XHTML));
            }
        } else {
            KappaBot.EmoteInfo[] emoteUrls = bot.getEmpoteUrls(message.getPlainTextMessage());
            for (KappaBot.EmoteInfo emote : emoteUrls) {
                byte[] image;
                try {
                    sendMessage(new Message(message.getGroup(), "You seem to lack an important Emote, please help yourself: ", MessageFormat.XHTML));
                    sendMessage(
                        new Message(message.getGroup(),
                            ImageEdit.imageAsTag(KappaBot.getUrlFromImageID(emote.image_id), 32, 32, emote.emotename, "PNG"), MessageFormat.XHTML));
                } catch (Exception e) {

                }
            }
        }
    }
}
