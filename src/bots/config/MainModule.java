package bots.config;

import com.google.inject.AbstractModule;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new UtilModule());
        install(new BotsModule());
    }
}
