package bots.NewsBot.config;

import bots.NewsBot.service.*;
import bots.NewsBot.service.impl.*;
import com.google.inject.AbstractModule;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class NewsBotModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NewsService.class).to(NewsServiceImpl.class);
        bind(CultureService.class).to(CultureServiceImpl.class);
        bind(CategoryService.class).to(CategoryServiceImpl.class);
        bind(ArticleService.class).to(ArticleServiceIml.class);
        bind(TitleService.class).to(TitleServiceImpl.class);
    }
}
