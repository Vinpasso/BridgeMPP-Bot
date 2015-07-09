package bots.NewsBot.commands;

import bots.NewsBot.dto.Category;
import bots.NewsBot.service.CategoryService;
import bridgempp.bot.fancy.command.StringCommand;
import com.google.inject.Inject;

import java.util.Map;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class ShowCategories extends StringCommand {
    @Inject private CategoryService categoryService;

    @Override
    public String handle(Map<String, String> args) {
        String culture;
        String limit;
        Category[] categories;

        if (null != (culture = args.get("culture"))) {
            if (null != (limit = args.get("n"))) {
                categories = categoryService.getCategories(culture, Integer.parseInt(limit));
            } else {
                categories = categoryService.getCategories(culture);
            }
        } else {
            if (null != (limit = args.get("n"))) {
                categories = categoryService.getCategories(Integer.parseInt(limit));
            } else {
                categories = categoryService.getCategories();
            }
        }
        StringBuilder sb = new StringBuilder("Categories:");
        for (Category cat : categories) {
            sb.append("(").append(cat.getId()).append(") ").append(cat.getNameEng()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getCommand() {
        return "(?:categories|cats) [{culture}] [limit {n}]";
    }

    @Override
    public String getDescription() {
        return "shows {n} categories of {culture}";
    }

    @Override
    public void init() {

    }
}
