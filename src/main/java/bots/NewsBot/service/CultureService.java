package bots.NewsBot.service;

import bots.NewsBot.dto.Culture;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public interface CultureService {
    Culture[] getCultures();
    Culture[] getCultures(int limit);

    String getCultureId(String culture);
}
