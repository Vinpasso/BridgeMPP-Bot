package bridgempp.bot.fancy.command;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Message;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public abstract class StringCommand extends Command {
    @Override
    public Message handle(Message message, Map<String, String> args) {
        return new Message(message.getGroup(), handle(args), MessageFormat.PLAIN_TEXT);
    }

    public abstract String handle(Map<String, String> args);
}
