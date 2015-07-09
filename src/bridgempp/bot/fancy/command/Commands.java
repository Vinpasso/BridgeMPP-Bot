package bridgempp.bot.fancy.command;

import bridgempp.bot.wrapper.Message;

import java.util.List;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface Commands {
    boolean accept(Message message);
    Message onMessage(Message message);
    void addCommand(Class<? extends Command> command);
    List<Command> getCommands();
}
