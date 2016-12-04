package bots.NewsBot.service.impl;

import bots.NewsBot.dto.Category;
import bots.NewsBot.service.CategoryService;
import bots.NewsBot.service.CultureService;
import bots.NewsBot.service.NewsService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Singleton
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final static int DEFAULT_CATEGORY = 403;

    @Inject
    private NewsService newsService;
    @Inject
    private CultureService cultureService;

    private Category[] categories;

    @Override
    public Category[] getCategories() {
        if (null == categories) {
            try {
                categories = newsService.downloadCategories();
            } catch (IOException e) {
                log.warn("Error loading categories", e);
                throw new RuntimeException(e);
            }
        }
        return categories;
    }

    @Override
    public Category[] getCategories(int limit) {
        return Arrays.copyOfRange(categories, 0, limit);
    }

    @Override
    public Category[] getCategories(String culture) {
        return getCategories(culture, -1);
    }

    @Override
    public Category[] getCategories(String culture, int limit) {
        String cultureId = cultureService.getCultureId(culture);
        try {
            Category[] categories = newsService.downloadCategories(cultureId);
            if (limit < 0) {
                return categories;
            } else {
                return Arrays.copyOfRange(categories, 0, limit);
            }
        } catch (IOException e) {
            log.warn("Error loading categories for culture '" + culture + "'", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCategoryId(String category) {
        if (category == null) {
            return DEFAULT_CATEGORY;
        }
        Optional<Category> o = Arrays.stream(categories)
                .filter(c -> c.getNameEng().equalsIgnoreCase(category)
                        || c.getName().equalsIgnoreCase(category)
                        || String.valueOf(c.getId()).equalsIgnoreCase(category))
                .findAny();
        if (o.isPresent()) {
            return o.get().getId();
        } else {
            return -1;
        }
    }

    @Override
    public int getCategoryId(String culture, String category) {
        if (category == null) {
            return DEFAULT_CATEGORY;
        }
        Optional<Category> o = Arrays.stream(getCategories(culture))
                .filter(c -> c.getNameEng().equalsIgnoreCase(category)
                        || c.getName().equalsIgnoreCase(category)
                        || String.valueOf(c.getId()).equalsIgnoreCase(category))
                .findAny();
        if (o.isPresent()) {
            return o.get().getId();
        } else {
            return -1;
        }
    }
}
