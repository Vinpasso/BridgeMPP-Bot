package bots.NewsBot.service;

import bots.NewsBot.dto.Article;
import bots.NewsBot.dto.Category;
import bots.NewsBot.dto.Culture;

import java.io.IOException;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface NewsService {
    Culture[] downloadCultures() throws IOException;
    Category[] downloadCategories() throws IOException;
    Category[] downloadCategories(String cultureId) throws IOException;
    Article[] downloadArticles(int categoryId) throws IOException;
}
