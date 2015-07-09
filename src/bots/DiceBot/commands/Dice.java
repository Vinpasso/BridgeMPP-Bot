package bots.DiceBot.commands;

import bridgempp.bot.fancy.command.StringCommand;

import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author <a href="mailto:jaro.fietz@uniscon.de">Jaro Fietz</a>.
 */
public class Dice extends StringCommand {
    private Random rand;

    @Override
    public String handle(Map<String, String> args) {
        int sides = 20;
        int times = 1;
        if (null != args.get("sides")) {
            sides = Integer.parseInt(args.get("sides"));
        }
        if (null != args.get("times")) {
            times = Integer.parseInt(args.get("times"));
        }
        if (times == 1) {
            return String.valueOf(rand.nextInt(sides)+1);
        }
        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < times; i++) {
            sj.add(String.valueOf(rand.nextInt(sides)+1));
        }
        return "{" + sj.toString() + "}";
    }

    @Override
    public String getCommand() {
        return "[{sides}][ {times}]";
    }

    @Override
    public String getDescription() {
        return "rolls a `sides`-sided dice `times` times; default is d20 1 time";
    }

    @Override
    public void init() {
        rand = new Random();
    }
}
