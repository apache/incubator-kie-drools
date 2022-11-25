package org.optaplanner.examples.travelingtournament.persistence;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class TravelingTournamentSolutionFileIO extends AbstractJsonSolutionFileIO<TravelingTournament> {

    public TravelingTournamentSolutionFileIO() {
        super(TravelingTournament.class);
    }

    @Override
    public TravelingTournament read(File inputSolutionFile) {
        TravelingTournament travelingTournament = super.read(inputSolutionFile);

        var teamsById = travelingTournament.getTeamList().stream()
                .collect(Collectors.toMap(Team::getId, Function.identity()));
        /*
         * Replace the duplicate team instances in the distanceToTeamMap by references to instances from
         * the teamList.
         */
        for (Team team : travelingTournament.getTeamList()) {
            var newTravelDistanceMap = deduplicateMap(team.getDistanceToTeamMap(),
                    teamsById, Team::getId);
            team.setDistanceToTeamMap(newTravelDistanceMap);
        }
        return travelingTournament;
    }

}
