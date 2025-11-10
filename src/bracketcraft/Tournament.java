package bracketcraft;

import javax.swing.JOptionPane;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tournament implements Serializable {
    private final String tournamentName;
    private final List<Participant> participants;
    private final List<List<Match>> rounds;

    public Tournament(String tournamentName, List<Participant> initialParticipants) {
        this.tournamentName = tournamentName;
        this.participants = new ArrayList<>(initialParticipants);
        this.rounds = new ArrayList<>();
    }

    /**
     * -- PHASE 3: Main Bracket Generation Controller --
     * Selects the correct bracket generation logic based on the chosen type.
     * @param bracketType The type of bracket to generate (e.g., "Single Elimination").
     */
    public void generateBracket(String bracketType) {
        if (participants == null || participants.size() < 2) return;
        rounds.clear();

        switch (bracketType) {
            case "Single Elimination":
                generateSingleElimination();
                break;
                
            case "Double Elimination":
                // Placeholder for future implementation
                JOptionPane.showMessageDialog(null, "Double Elimination is not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                break;

            // Add other cases for Round Robin, etc. in the future.
                
            default:
                // Fallback for an unknown type
                JOptionPane.showMessageDialog(null, "The selected bracket type is not recognized.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    /**
     * -- PHASE 3: Specific Logic for Single Elimination --
     * Contains the original logic for creating a single-elimination bracket.
     */
    private void generateSingleElimination() {
        List<Participant> bracketParticipants = new ArrayList<>(participants);
        // Assuming "Seeded" for now based on ParticipantsPanel order
        // If you wanted a random option here, you would Collections.shuffle(bracketParticipants);

        int numParticipants = bracketParticipants.size();
        // Calculate the next power of 2 for the bracket size
        int bracketSize = (int) Math.pow(2, Math.ceil(Math.log(numParticipants) / Math.log(2)));
        int numByes = bracketSize - numParticipants;

        // Participants who get a bye automatically advance.
        // We pull participants for the first round from the end of the seeded list.
        List<Participant> round1Players = new ArrayList<>();
        for (int i = 0; i < (bracketSize - 2 * numByes); i++) {
             round1Players.add(bracketParticipants.remove(numByes));
        }

        // --- Create Round 1 ---
        List<Match> round1Matches = new ArrayList<>();
        for (int i = 0; i < round1Players.size() / 2; i++) {
            Match match = new Match();
            match.setParticipant1(round1Players.get(i));
            match.setParticipant2(round1Players.get(round1Players.size() - 1 - i));
            round1Matches.add(match);
        }
         if (!round1Matches.isEmpty()) {
            rounds.add(round1Matches);
        }

        // 'bracketParticipants' now only contains those with byes.
        // Advancing entities can be Participants (with byes) or Matches (from Round 1).
        List<Object> advancingEntities = new ArrayList<>();
        advancingEntities.addAll(bracketParticipants);
        advancingEntities.addAll(round1Matches);
        
        // Loop to build all subsequent rounds
        while (advancingEntities.size() > 1) {
            List<Match> nextRoundMatches = new ArrayList<>();
            for (int i = 0; i < advancingEntities.size(); i += 2) {
                Match newMatch = new Match();
                
                Object entity1 = advancingEntities.get(i);
                if (entity1 instanceof Participant) newMatch.setParticipant1((Participant) entity1);
                else ((Match) entity1).setNextMatch(newMatch);

                Object entity2 = advancingEntities.get(i + 1);
                if (entity2 instanceof Participant) newMatch.setParticipant2((Participant) entity2);
                else ((Match) entity2).setNextMatch(newMatch);
                
                nextRoundMatches.add(newMatch);
            }
            rounds.add(nextRoundMatches);
            advancingEntities.clear();
            advancingEntities.addAll(nextRoundMatches);
        }
    }


    // --- Getters ---
    public String getTournamentName() { return tournamentName; }
    public List<List<Match>> getRounds() { return rounds; }
}