package bridgempp.bot.fancy.command;

import bridgempp.bot.wrapper.Message;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class CommandsImpl implements Commands {
    private final String botPrefix = "?";
    private final String commandPrefix;

    @Inject private Injector injector;

    private List<Command> commands = new ArrayList<>();

    @Override
    public boolean accept(Message msg) {
        String message = msg.getMessage();
        if (!message.startsWith(botPrefix + commandPrefix)) {
            return false;
        }
        String args = message.substring(botPrefix.length() + commandPrefix.length()).trim();
        return commands.stream()
                .filter(c -> args.matches(c.getArgRegex()))
                .count() > 0;
    }

    @Override
    public Message onMessage(Message message) {
        String args = message.getMessage().substring(botPrefix.length() + commandPrefix.length()).trim();
        Command cmd = commands.stream()
                .filter(c -> args.matches(c.getArgRegex()))
                .max((c1, c2) -> c1.getArgCount() - c2.getArgCount())
                .get();
        Matcher matcher = Pattern.compile(cmd.getArgRegex()).matcher(args);
        String[] argNames = cmd.getArgNames();
        Map<String, String> arg = new HashMap<>(argNames.length);
        if (!matcher.matches()) {
            throw new IllegalStateException(commandPrefix + ": Error during onMessage: Accepted unmatched group");
        }
        for (int i = 0; i < argNames.length; i++) {
            arg.put(argNames[i], matcher.group(i+1));
        }
        return cmd.handle(message, arg);
    }

    @Override
    public void addCommand(Class<? extends Command> command) {
        commands.add(injector.getInstance(command));
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
