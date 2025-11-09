package bracketcraft;

import java.io.Serializable;

/**
 * Represents a single match in the tournament.
 */
public class Match implements Serializable {
    private Participant participant1;
    private Participant participant2;
    private Participant winner;
    private Match nextMatch; // The match in the next round the winner advances to.

    // --- Getters and Setters ---
    public Participant getParticipant1() { return participant1; }
    public void setParticipant1(Participant p) { this.participant1 = p; }

    public Participant getParticipant2() { return participant2; }
    public void setParticipant2(Participant p) { this.participant2 = p; }

    public Participant getWinner() { return winner; }
    public void setWinner(Participant winner) { this.winner = winner; }

    public Match getNextMatch() { return nextMatch; }
    public void setNextMatch(Match nextMatch) { this.nextMatch = nextMatch; }
}