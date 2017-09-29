package bridgempp.bot.fancy.command;

import bridgempp.bot.wrapper.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public abstract class Command {
    public abstract String getCommand();
    public abstract String getDescription();

    public abstract void init();
    public abstract Message handle(Message message, Map<String, String> args);

    public int getArgCount() {
        return getCommand().split(" ").length;
    }

    public String getArgRegex() {
        return getCommand().replaceAll("[{]\\w+[}]", "(\\\\w*)").replaceAll("[\\[]([\\w\\(\\)\\.\\*\\\\ ]+)[\\]]", "(?:$1)?");
    }

    public String[] getArgNames() {
        Matcher matcher = Pattern.compile("[{](\\w*)[}]").matcher(getCommand());
        List<String> names = new ArrayList<>();
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names.toArray(new String[names.size()]);
    }
}
