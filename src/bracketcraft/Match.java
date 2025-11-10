package bracketcraft;

import java.io.Serializable;

public class Match implements Serializable {
    private Participant participant1;
    private Participant participant2;
    private Participant winner;
    private Match nextMatch;
    
    // --- NEW: Fields to store match scores ---
    private int score1 = 0;
    private int score2 = 0;

    /**
     * Default constructor for creating an empty match to be filled later.
     */
    public Match() {
        // Default constructor is still needed for creating future-round matches
    }

    /**
     * New constructor for instantly creating a match with participants.
     * @param p1 The first participant (or seed).
     * @param p2 The second participant (or seed).
     */
    public Match(Participant p1, Participant p2) {
        this.participant1 = p1;
        this.participant2 = p2;
    }

    // --- Getters and Setters ---
    public Participant getParticipant1() { return participant1; }
    public void setParticipant1(Participant p) { this.participant1 = p; }

    public Participant getParticipant2() { return participant2; }
    public void setParticipant2(Participant p) { this.participant2 = p; }

    public Participant getWinner() { return winner; }
    public void setWinner(Participant winner) { this.winner = winner; }

    public Match getNextMatch() { return nextMatch; }
    public void setNextMatch(Match nextMatch) { this.nextMatch = nextMatch; }
    
    // --- NEW: Getters and Setters for scores ---
    public int getScore1() { return score1; }
    public void setScore1(int score1) { this.score1 = score1; }

    public int getScore2() { return score2; }
    public void setScore2(int score2) { this.score2 = score2; }
}