package bots.PollBot;

import bots.PollBot.Data.Poll;
import bots.PollBot.Worker.AutoPollEnd;
import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

/**
 * Created by root on 6/23/15.
 */
public class PollBot extends Bot {
    private static final String GROUP = "tumspam";
    private static final String POLL_ALREADY_EXISTS = "There is already an active poll. " +
            "If you're the owner, type '?result' to end it.";
    private static final String POLL_CREATED = " Vote aye/nay.";
    private static final String POLL_END_DENIED = "You are not the owner of the poll and thus cannot end it.";


    private Thread pollEnd;
    private Poll poll;

    @Override
    public final void initializeBot() {
    }

    @Override
    /**
     * Actions:
     * 1. Create Poll (Soll...?, Sollen...?, ?poll [keywords] ?
     * 2. Answer latest poll: aye/nay
     * 3. Answer specific Poll: ?aye/nay [keywords]
     * 4. End Poll: ?result, ?end
     */
    public final void messageReceived(final Message message) {
        if (message == null || message.getMessage() == null || message.getSender() == null || message.getGroup() == null)
            throw new IllegalArgumentException("Invalid Message!");

        String messageText = message.getMessage().toLowerCase();

        if (messageText.endsWith("?") && (messageText.startsWith("soll") || messageText.startsWith("?poll "))) {
            if (poll != null) {
                createPoll(message.getMessage(), message.getSender());
            } else
                sendMessage(new Message(GROUP, message.getMessage() + POLL_ALREADY_EXISTS, MessageFormat.PLAIN_TEXT));
        } else if (poll == null) return;

        //Poll is not null, modify it
        if (messageText.equals("aye")) {
            poll.vote(message.getSender(), true);
        } else if (messageText.equals("nay")) {
            poll.vote(message.getSender(), false);
        } else if (messageText.startsWith("?result") || messageText.startsWith("?end")) {
            if (message.getSender().equals(poll.creator)) endPoll();
            else sendMessage(new Message(GROUP, POLL_END_DENIED, MessageFormat.PLAIN_TEXT));
        }
    }

    public void createPoll(String message, String creator) {
        poll = new Poll(creator, message);
        pollEnd = new Thread(new AutoPollEnd(this));
        pollEnd.start();
        sendMessage(new Message(GROUP, message + POLL_CREATED, MessageFormat.PLAIN_TEXT));
    }

    public void endPoll() {
        if (pollEnd.isAlive()) {
            pollEnd.interrupt();
        }
        sendMessage(new Message(GROUP, "Poll: " + poll.message + " Result: " + (poll.getYes() > poll.getNo() ? "YES!" : "NO!") +
                " Yes: " + poll.getYes() + ", No: " + poll.getNo(), MessageFormat.PLAIN_TEXT));
        poll = null;
    }


}
