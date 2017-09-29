package bots.config;

import bots.MemeBot.config.MemeBotModule;
import bots.NewsBot.config.NewsBotModule;
import com.google.inject.AbstractModule;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class BotsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new NewsBotModule());
        install(new MemeBotModule());
    }
}
