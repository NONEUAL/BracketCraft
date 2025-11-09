package bracketcraft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds all data for a tournament and contains the logic for generating
 * a single-elimination bracket based on a chosen seeding option.
 */
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
     * Generates the entire bracket structure.
     * @param seedingOption A string, either "Random" or "Seeded", that determines
     *                      how the initial matchups are created.
     */
    public void generateBracket(String seedingOption) {
        if (participants == null || participants.size() < 2) return;
        rounds.clear();

        List<Participant> bracketParticipants = new ArrayList<>(participants);

        // --- Seeding Logic ---
        if ("Random".equalsIgnoreCase(seedingOption)) {
            Collections.shuffle(bracketParticipants);
        }
        // If "Seeded", we use the list in its provided order (assuming it's pre-sorted).

        int numParticipants = bracketParticipants.size();
        int bracketSize = (int) Math.pow(2, Math.ceil(Math.log(numParticipants) / Math.log(2)));
        int numByes = bracketSize - numParticipants;
        int numRound1Participants = numParticipants - numByes;

        // --- Create Round 1 ---
        // This round only contains matches that are actually played.
        List<Match> round1Matches = new ArrayList<>();
        for (int i = 0; i < numRound1Participants / 2; i++) {
            Match match = new Match();
            // Pull participants from the end of the list for Round 1
            match.setParticipant1(bracketParticipants.remove(bracketParticipants.size() - 1));
            match.setParticipant2(bracketParticipants.remove(bracketParticipants.size() - 1));
            round1Matches.add(match);
        }
        if (!round1Matches.isEmpty()) {
            rounds.add(round1Matches);
        }

        // --- Prepare for Subsequent Rounds ---
        // 'bracketParticipants' now only contains those with byes.
        // Advancing entities can be Participants (with byes) or Matches (from Round 1).
        List<Object> advancingEntities = new ArrayList<>();
        advancingEntities.addAll(bracketParticipants);
        advancingEntities.addAll(round1Matches);

        // Loop to build all subsequent rounds from the advancing entities
        while (advancingEntities.size() > 1) {
            List<Match> nextRoundMatches = new ArrayList<>();
            for (int i = 0; i < advancingEntities.size(); i += 2) {
                Match newMatch = new Match();
                // Link the first entity (either a bye participant or a previous match)
                Object entity1 = advancingEntities.get(i);
                if (entity1 instanceof Participant) newMatch.setParticipant1((Participant) entity1);
                else ((Match) entity1).setNextMatch(newMatch);

                // Link the second entity
                Object entity2 = advancingEntities.get(i + 1);
                if (entity2 instanceof Participant) newMatch.setParticipant2((Participant) entity2);
                else ((Match) entity2).setNextMatch(newMatch);
                
                nextRoundMatches.add(newMatch);
            }
            rounds.add(nextRoundMatches);
            // The next loop will build from the matches we just created
            advancingEntities.clear();
            advancingEntities.addAll(nextRoundMatches);
        }
    }

    // --- Getters ---
    public String getTournamentName() { return tournamentName; }
    public List<List<Match>> getRounds() { return rounds; }
}