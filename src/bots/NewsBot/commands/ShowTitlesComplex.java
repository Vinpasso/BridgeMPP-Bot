package bots.NewsBot.commands;

import bots.NewsBot.service.TitleService;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class ShowTitlesComplex extends StringCommand {
    @Inject TitleService titleService;

    @Override
    public String handle(Map<String, String> args) {
        String[] titles;
        String category = args.get("category");
        String culture = args.get("culture");
        String limits = args.get("n");
        int limit = null == limits ? -1 : Integer.parseInt(limits);

        if (null != category && null != culture) {
            titles = titleService.getTitles(category, culture, limit);
        } else if (null != category) {
            titles = titleService.getTitles(category, limit);
        } else if (null != culture) {
            titles = titleService.getTitles(culture, limit);
        } else {
            titles = titleService.getTitles(limit);
        }

        StringBuilder sb = new StringBuilder("Top 3 Titles:");
        for (int i = 0; i < titles.length; i++) {
            sb.append("(").append(i).append(") ").append(titles[i]).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getCommand() {
        return "show [cat {category}] [cul {culture}] [limit {n}]";
    }

    @Override
    public String getDescription() {
        return "shows top 3 titles";
    }

    @Override
    public void init() {}
}
