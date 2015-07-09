package bridgempp.bot.fancy;

import bridgempp.bot.fancy.command.Command;
import bridgempp.bot.fancy.command.Commands;
import bridgempp.bot.fancy.command.CommandsImpl;
import bridgempp.bot.fancy.config.FancyBotModule;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FancyBot extends Bot {
	protected Commands ci;
    @Inject private Injector injector;

    public FancyBot(String botName) {
        ci = new CommandsImpl(botName);
    }

	@Override
	public void initializeBot() {
        Injector childInjector = injector.createChildInjector(new FancyBotModule(ci));
        childInjector.injectMembers(ci);
        childInjector.injectMembers(this);
        init();
	}

	@Override
	public void messageReceived(Message message) {
        if (ci.accept(message)) {
            sendMessage(ci.onMessage(message));
        }
	}

    public void addCommand(Class<? extends Command> command) {
        ci.addCommand(command);
    }

    public abstract void init();
}
