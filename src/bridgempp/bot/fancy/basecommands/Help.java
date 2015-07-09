package bridgempp.bot.fancy.basecommands;

import bridgempp.bot.fancy.command.Command;
import bridgempp.bot.fancy.command.Commands;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@Singleton
public class Help extends StringCommand {
    @Inject Commands commands;

    @Override
    public String getCommand() {
        return "(?:help|\\?)";
    }

    @Override
    public String getDescription() {
        return "Shows this help";
    }

    @Override
    public void init() {}

    @Override
    public String handle(Map<String, String> args) {
        StringBuilder sb = new StringBuilder("Help:\n");
        // for each command print it's command, arguments and description
        for (Command c : commands.getCommands()) {
            sb.append("\t").append(c.getCommand());
            sb.append(":\n\t ").append(c.getDescription());
        }
        return sb.toString();
    }
}
