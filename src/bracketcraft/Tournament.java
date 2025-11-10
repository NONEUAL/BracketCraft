package bracketcraft;

import javax.swing.JOptionPane;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tournament implements Serializable {
    private String tournamentName;
    private List<Participant> participants;
    private final List<List<Match>> rounds;
    
    // --- REFINEMENT: Rules are now part of the tournament data ---
    private String rules;

    public Tournament(String tournamentName, List<Participant> initialParticipants) {
        this.tournamentName = tournamentName;
        this.participants = new ArrayList<>(initialParticipants);
        this.rounds = new ArrayList<>();
        // Default rules text
        this.rules = "1. All matches are Best of 3.\n2. No substitutions allowed.\n3. Organizer's decision is final.";
    }

    /**
     * Main Bracket Generation Controller.
     * Selects the correct bracket generation logic based on the chosen type.
     * @param bracketType The type of bracket to generate (e.g., "Single Elimination").
     */
    public void generateBracket(String bracketType) {
        if (participants == null || participants.size() < 2) return;
        rounds.clear();

        switch (bracketType) {
            case "Single Elimination":
                generateStandardSingleElimination();
                break;
                
            case "Double Elimination":
                JOptionPane.showMessageDialog(null, "Double Elimination is not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            default:
                JOptionPane.showMessageDialog(null, "The selected bracket type is not recognized.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    /**
     * -- REFINED MATCHMAKING --
     * Generates a single-elimination bracket using a standard seeding algorithm.
     * This ensures Seed #1 and Seed #2 are on opposite sides of the bracket.
     */
    private void generateStandardSingleElimination() {
        List<Participant> seededParticipants = new ArrayList<>(this.participants);
        int numParticipants = seededParticipants.size();
        
        // 1. Determine the bracket size (next power of 2)
        int bracketSize = 2;
        while (bracketSize < numParticipants) {
            bracketSize *= 2;
        }
        
        // 2. Pad the participant list with "BYE" placeholders to fill the bracket
        List<Participant> bracketSlots = new ArrayList<>(bracketSize);
        // This is the standard seeding order for a bracket (e.g., 1, 16, 8, 9, 5, 12...)
        int[] seedOrder = getSeedOrder(bracketSize); 
        Participant[] initialSlots = new Participant[bracketSize];
        for(int i = 0; i < numParticipants; i++) {
            // Place the i-th seeded participant into the correct slot based on standard seeding
            initialSlots[seedOrder[i] - 1] = seededParticipants.get(i);
        }
        Collections.addAll(bracketSlots, initialSlots); // `null` entries are our BYEs

        // 3. Create the first round from the padded list
        List<Match> firstRound = new ArrayList<>();
        List<Object> nextRoundAdvancers = new ArrayList<>(); // Can hold Participants (byes) or Matches
        
        for (int i = 0; i < bracketSize; i += 2) {
            Participant p1 = bracketSlots.get(i);
            Participant p2 = bracketSlots.get(i + 1);

            if (p1 != null && p2 != null) { // Standard match
                Match m = new Match(p1, p2);
                firstRound.add(m);
                nextRoundAdvancers.add(m);
            } else if (p1 != null) { // p1 has a bye
                nextRoundAdvancers.add(p1);
            } else if (p2 != null) { // p2 has a bye
                nextRoundAdvancers.add(p2);
            }
        }
        if(!firstRound.isEmpty()) {
            this.rounds.add(firstRound);
        }
        
        // 4. Generate subsequent rounds until a final match is created
        List<Object> currentAdvancers = nextRoundAdvancers;
        while(currentAdvancers.size() > 1) {
            List<Match> nextRoundMatches = new ArrayList<>();
            List<Object> nextAdvancers = new ArrayList<>();
            for(int i = 0; i < currentAdvancers.size(); i+=2) {
                Match newMatch = new Match();
                Object entity1 = currentAdvancers.get(i);
                Object entity2 = currentAdvancers.get(i+1);

                if (entity1 instanceof Participant) newMatch.setParticipant1((Participant)entity1);
                else ((Match)entity1).setNextMatch(newMatch);

                if (entity2 instanceof Participant) newMatch.setParticipant2((Participant)entity2);
                else ((Match)entity2).setNextMatch(newMatch);
                
                nextRoundMatches.add(newMatch);
                nextAdvancers.add(newMatch);
            }
            this.rounds.add(nextRoundMatches);
            currentAdvancers = nextAdvancers;
        }
    }

    /** Helper method to generate the standard seeding order for a bracket of a given size. */
    private int[] getSeedOrder(int size) {
        if (size == 1) return new int[]{1};
        if (size == 2) return new int[]{1, 2};
        
        int[] prevOrder = getSeedOrder(size / 2);
        int[] newOrder = new int[size];
        
        for(int i = 0; i < size / 2; i++) {
            int seed = prevOrder[i];
            newOrder[i*2] = seed;
            newOrder[i*2+1] = size + 1 - seed;
        }
        return newOrder;
    }

    // --- Getters & Setters ---
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
    public List<List<Match>> getRounds() { return rounds; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }
}