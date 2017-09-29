package bots.PollBot.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO Data model for a poll.
 */
public class Poll {
    public final long creationTime;
    public final String creator;
    public final String message;
    public final Map<String, Boolean> votes;

    public Poll(String creator, String message) {
        this.creationTime = System.currentTimeMillis();
        this.creator = creator;
        this.message = message;
        this.votes = new HashMap<>();
    }

    public void vote(String voter, boolean value) {
        votes.put(voter, value);
    }

    public int getYes() {
        int numberYes = 0;
        for (String user : votes.keySet()) {
            if (votes.get(user)) numberYes++;
        }
        return numberYes;
    }

    public int getNo() {
        return votes.size() - getYes();
    }
}
