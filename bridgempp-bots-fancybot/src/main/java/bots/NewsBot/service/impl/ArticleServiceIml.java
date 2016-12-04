package bots.NewsBot.service.impl;

import bots.NewsBot.dto.Article;
import bots.NewsBot.service.ArticleService;
import bots.NewsBot.service.CategoryService;
import bots.NewsBot.service.NewsService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Singleton
@Slf4j
public class ArticleServiceIml implements ArticleService {
	@Inject private CategoryService categoryService;
    @Inject private NewsService newsService;

	public Article getArticle(int id) {
		return getArticle(null, id);
	}

	public Article getArticle(String category, int id) {
        int categoryId = categoryService.getCategoryId(category);
        return getArticle(categoryId, id);
	}

	public Article getArticle(int category, int id) {
        try {
            return newsService.downloadArticles(category)[id];
        } catch (IOException e) {
            log.warn("Error loading articles", e);
            throw new RuntimeException(e);
        }
    }

    public Article getArticle(String culture, String category, int id) {
        int categoryId = categoryService.getCategoryId(culture, category);
        return getArticle(categoryId, id);
    }

	public Article[] getArticles(String category, int limit) {
        int categoryId = categoryService.getCategoryId(category);
        return getArticles(categoryId, limit);
	}

	public Article[] getArticles(int categoryId, int limit) {
        try {
            Article[] articles = newsService.downloadArticles(categoryId);
            if (limit < 0) {
                return articles;
            } else {
                return Arrays.copyOfRange(articles, 0, limit);
            }
        } catch (IOException e) {
            log.warn("Error loading articles for category " + categoryId, e);
            throw new RuntimeException(e);
        }
    }
}
