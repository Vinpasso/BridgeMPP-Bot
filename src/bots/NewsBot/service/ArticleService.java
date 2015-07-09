package bots.NewsBot.service;

import bots.NewsBot.dto.Article;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface ArticleService {
    Article getArticle(int id);
    Article getArticle(String category, int id);
    Article getArticle(int category, int id);
    Article getArticle(String culture, String category, int id);

    Article[] getArticles(String category, int limit);
    Article[] getArticles(int category, int limit);
}
