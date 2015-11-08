package bots.NewsBot.service.impl;

import bots.NewsBot.dto.Article;
import bots.NewsBot.dto.Articles;
import bots.NewsBot.dto.Category;
import bots.NewsBot.dto.Culture;
import bots.NewsBot.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Singleton
@Slf4j
public class NewsServiceImpl implements NewsService {
	@Inject ObjectMapper objectMapper;

	public Culture[] downloadCultures() throws IOException {
		URL url = new URL("http://api.feedzilla.com/v1/cultures.json");
		return objectMapper.readValue(url, Culture[].class);
	}

    public Category[] downloadCategories() throws IOException {
        URL url = new URL("http://api.feedzilla.com/v1/categories.json");
        return objectMapper.readValue(url, Category[].class);
    }

	public Category[] downloadCategories(String cultureId) throws IOException {
		URL url = new URL("http://api.feedzilla.com/v1/categories.json?culture_code=" + cultureId);
        return objectMapper.readValue(url, Category[].class);
	}

	public Article[] downloadArticles(int category) throws IOException {
		URL url = new URL("http://api.feedzilla.com/v1/categories/" + category + "/articles.json");
		URLConnection con = url.openConnection();
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		Object o = con.getContent();
        return objectMapper.readValue(url, Articles.class).getArticles();
	}
}
