package bridgempp.bot.fancy.config;

import bridgempp.bot.fancy.command.Commands;
import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
@AllArgsConstructor
public class FancyBotModule extends AbstractModule {
    private Commands commands;

    @Override
    protected void configure() {
        bind(Commands.class).toInstance(commands);
    }
}
