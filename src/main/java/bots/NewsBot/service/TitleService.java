package bots.NewsBot.service;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface TitleService {
    String[] getTitles(int limit);
    String[] getTitles(String category);
    String[] getTitles(String category, int limit);
    String[] getTitles(int category, int limit);
    String[] getTitles(String culture, String category);
    String[] getTitles(String culture, String category, int limit);
}
