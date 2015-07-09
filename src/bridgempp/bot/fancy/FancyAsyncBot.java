package bridgempp.bot.fancy;

import bridgempp.bot.wrapper.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public abstract class FancyAsyncBot extends FancyBot {
    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public FancyAsyncBot(String botName) {
        super(botName);
    }

    @Override
    public void messageReceived(Message message) {
        if (ci.accept(message)) {
            executor.execute(() -> sendMessage(ci.onMessage(message)));
        }
    }
}
