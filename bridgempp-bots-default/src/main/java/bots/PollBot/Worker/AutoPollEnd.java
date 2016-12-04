package bots.PollBot.Worker;

import bots.PollBot.PollBot;

/**
 * Created by root on 6/30/15.
 */
public class AutoPollEnd implements Runnable {
    private PollBot pollBot;

    public AutoPollEnd(PollBot pollBot) {
        this.pollBot = pollBot;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(60 * 1000 * 10);
        } catch (Exception e) {
            return;
        }
        pollBot.endPoll();
    }
}
