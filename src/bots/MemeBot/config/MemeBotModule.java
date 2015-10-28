package bots.MemeBot.config;

import bots.MemeBot.service.*;
import bots.MemeBot.service.impl.*;
import com.google.inject.AbstractModule;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class MemeBotModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MemeService.class).to(MemeServiceImpl.class);
    }
}
