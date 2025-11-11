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
    private String rules;

    public Tournament(String tournamentName, List<Participant> initialParticipants) {
        this.tournamentName = tournamentName;
        this.participants = new ArrayList<>(initialParticipants);
        this.rounds = new ArrayList<>();
        this.rules = "1. All matches are Best of 3.\n2. No substitutions allowed.\n3. Organizer's decision is final.";
    }

    /**
     * Main Bracket Generation.
     */
    public void generateBracket(String bracketType) {
        if (participants == null || participants.size() < 2) return;
        rounds.clear();

        switch (bracketType) {
            case "Single Elimination":
                generateSingleElimination();
                break;
                
            case "Double Elimination":
                JOptionPane.showMessageDialog(null, "Mag hintay ka jan.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                break;
            
            // Natamad pako i lagay sa UI
            case "Round Robin":
                JOptionPane.showMessageDialog(null, "Mag hintay ka jan.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case "Group Stage":
                JOptionPane.showMessageDialog(null, "Mag hintay ka jan.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            default:
                JOptionPane.showMessageDialog(null, "The selected bracket type is not recognized.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    /**
     * -- SEEDING LOGIC --
     * Generates a standard single-elimination bracket.
     */
    private void generateSingleElimination() {
        List<Participant> seededParticipants = new ArrayList<>(this.participants);
        int numParticipants = seededParticipants.size();

        // 1. Calculate bracket size (the next power of 2)
        int bracketSize = (int) Math.pow(2, Math.ceil(Math.log(numParticipants) / Math.log(2)));

        // 2. Create a list of all potential slots in the first round.
        List<Participant> roundOneSlots = new ArrayList<>(Collections.nCopies(bracketSize, null));

        // 3. Place participants into the slots according to standard seeding rules.
        if (numParticipants > 0) {
            List<Integer> currentSeeds = new ArrayList<>();
            currentSeeds.add(1);
            
            while (currentSeeds.size() * 2 <= bracketSize) {
                List<Integer> nextSeeds = new ArrayList<>();
                int sum = currentSeeds.size() * 2 + 1;
                for (int seed : currentSeeds) {
                    nextSeeds.add(seed);
                    nextSeeds.add(sum - seed);
                }
                currentSeeds = nextSeeds;
            }
            
            for(int i = 0; i < numParticipants; i++) {
                int seedToPlace = i + 1;
                int slotIndex = currentSeeds.indexOf(seedToPlace);
                roundOneSlots.set(slotIndex, seededParticipants.get(i));
            }
        }
        
        // 4. Create the first round of matches from the slots.
        List<Match> firstRoundMatches = new ArrayList<>();
        List<Object> advancingEntities = new ArrayList<>(); // Can hold Participants (byes) or Matches
        
        for (int i = 0; i < bracketSize; i += 2) {
            Participant p1 = roundOneSlots.get(i);
            Participant p2 = roundOneSlots.get(i + 1);

            if (p1 != null && p2 != null) { // A standard match
                Match m = new Match(p1, p2);
                firstRoundMatches.add(m);
                advancingEntities.add(m);
            } else if (p1 != null) { // p1 has a bye (AKA free Win)
                advancingEntities.add(p1);
            } else if (p2 != null) { // p2 has a bye
                advancingEntities.add(p2);
            }
            // If both are null (in a very large empty bracket),just do nothing.
        }
        if (!firstRoundMatches.isEmpty()) {
            this.rounds.add(firstRoundMatches);
        }
        
        // 5. Generate all subsequent rounds from the advancing entities.
        List<Object> currentAdvancers = advancingEntities;
        while (currentAdvancers.size() > 1) {
            List<Match> nextRoundMatches = new ArrayList<>();
            List<Object> nextRoundAdvancers = new ArrayList<>();
            for (int i = 0; i < currentAdvancers.size(); i += 2) {
                Match newMatch = new Match();
                Object entity1 = currentAdvancers.get(i);
                Object entity2 = currentAdvancers.get(i + 1);

                if (entity1 instanceof Participant) newMatch.setParticipant1((Participant) entity1);
                else ((Match) entity1).setNextMatch(newMatch);

                if (entity2 instanceof Participant) newMatch.setParticipant2((Participant) entity2);
                else ((Match) entity2).setNextMatch(newMatch);
                
                nextRoundMatches.add(newMatch);
                nextRoundAdvancers.add(newMatch);
            }
            this.rounds.add(nextRoundMatches);
            currentAdvancers = nextRoundAdvancers;
        }
    }

    // --- Getters & Setters ---
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
    public List<List<Match>> getRounds() { return rounds; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }
}