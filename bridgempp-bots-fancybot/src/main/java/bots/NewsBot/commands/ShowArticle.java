package bots.NewsBot.commands;

import bots.NewsBot.dto.Article;
import bots.NewsBot.service.ArticleService;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class ShowArticle extends StringCommand {
    @Inject ArticleService articleService;

    @Override
    public String handle(Map<String, String> args) {
        int n = Integer.parseInt(args.get("n"));
        Article article = articleService.getArticle(n);
        return article.getTitle() + ":\n" + article.getSummary() + " --- " + article.getAuthor()
                + "(" + article.getUrl() + ")";
    }

    @Override
    public String getCommand() {
        return "{n}";
    }

    @Override
    public String getDescription() {
        return "show Article number n";
    }

    @Override
    public void init() {

    }
}
