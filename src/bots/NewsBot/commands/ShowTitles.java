package bots.NewsBot.commands;

import bots.NewsBot.service.TitleService;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class ShowTitles extends StringCommand {
    @Inject TitleService titleService;

    @Override
    public String handle(Map<String, String> args) {
        String[] titles;
        if (null != args.get("n")) {
            titles = titleService.getTitles(Integer.parseInt(args.get("n")));
        } else {
            titles = titleService.getTitles(3);
        }
        StringBuilder sb = new StringBuilder("Top 3 Titles:");
        for (int i = 0; i < titles.length; i++) {
            sb.append("(").append(i).append(") ").append(titles[i]).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getCommand() {
        return "[limit {n}]";
    }

    @Override
    public String getDescription() {
        return "shows top 3 titles";
    }

    @Override
    public void init() {}
}
