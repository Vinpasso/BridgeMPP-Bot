package bots.NewsBot.commands;

import bots.NewsBot.dto.Culture;
import bots.NewsBot.service.CultureService;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class ShowCultures extends StringCommand {
    @Inject private CultureService cultureService;

    @Override
    public String handle(Map<String, String> args) {
        Culture[] cultures;
        if (null != args.get("n")) {
            cultures = cultureService.getCultures(Integer.parseInt(args.get("n")));
        } else {
            cultures = cultureService.getCultures();
        }
        StringBuilder sb = new StringBuilder("Cultures:\n");
        for (Culture culture : cultures) {
            sb.append("(").append(culture.getId()).append(") ").append(culture.getNameEng()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getCommand() {
        return "(?:cul|cultures) [limit {n}]";
    }

    @Override
    public String getDescription() {
        return "lists all cultures";
    }

    @Override
    public void init() {

    }
}
