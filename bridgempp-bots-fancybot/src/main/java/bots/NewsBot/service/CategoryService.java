package bots.NewsBot.service;

import bots.NewsBot.dto.Category;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface CategoryService {
    Category[] getCategories();
    Category[] getCategories(int limit);
    Category[] getCategories(String culture);
    Category[] getCategories(String culture, int limit);

    int getCategoryId(String category);
    int getCategoryId(String culture, String category);
}
