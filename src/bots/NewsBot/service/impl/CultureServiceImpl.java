package bots.NewsBot.service.impl;

import bots.NewsBot.dto.Culture;
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
public class CultureServiceImpl implements CultureService {
    @Inject private NewsService newsService;

	private Culture[] cultures;

	@Override
	public Culture[] getCultures() {
        if (null == cultures) {
            try {
                cultures = newsService.downloadCultures();
            } catch (IOException e) {
                log.warn("Error loading Cultures");
                throw new RuntimeException(e);
            }
        }
        return cultures;
	}

	@Override
	public Culture[] getCultures(int limit) {
        return Arrays.copyOfRange(getCultures(), 0, limit < 0 ? 5 : limit);
    }

    @Override
	public String getCultureId(String culture) {
        Optional<Culture> o = Arrays.stream(getCultures())
                .filter(c -> c.getNameEng().equalsIgnoreCase(culture)
                        || c.getName().equalsIgnoreCase(culture)
                        || c.getId().equalsIgnoreCase(culture)).findFirst();
        if (o.isPresent()) {
            return o.get().getId();
        } else {
            return null;
        }
	}
}
