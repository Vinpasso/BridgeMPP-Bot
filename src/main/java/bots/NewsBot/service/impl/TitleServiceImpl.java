package bots.NewsBot.service.impl;

import bots.NewsBot.dto.Article;
import bots.NewsBot.service.ArticleService;
import bots.NewsBot.service.CategoryService;
import bots.NewsBot.service.TitleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Arrays;


@Singleton
public class TitleServiceImpl implements TitleService {
	@Inject CategoryService categoryService;
    @Inject ArticleService articleService;

	public String[] getTitles(int limit) {
		return getTitles(null, limit);
	}

	public String[] getTitles(String category) {
		return getTitles(category, -1);
	}

	public String[] getTitles(String category, int limit) {
		int cat = categoryService.getCategoryId(category);
		return getTitles(cat, limit);
	}

	public String[] getTitles(int categoryId, int limit) {
        return Arrays.stream(articleService.getArticles(categoryId, limit)).map(Article::getTitle).toArray(String[]::new);
	}

	public String[] getTitles(String culture, String category) {
        return getTitles(categoryService.getCategoryId(culture, category), -1);
	}

	public String[] getTitles(String culture, String category, int limit) {
        return getTitles(categoryService.getCategoryId(culture, category), limit);
	}
}
