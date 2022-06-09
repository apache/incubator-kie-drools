package org.optaplanner.examples.travelingtournament.solver.move;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class TravelingTournamentMoveHelper {

    public static void moveDay(ScoreDirector<TravelingTournament> scoreDirector, Match match, Day toDay) {
        scoreDirector.beforeVariableChanged(match, "day");
        match.setDay(toDay);
        scoreDirector.afterVariableChanged(match, "day");
    }

    private TravelingTournamentMoveHelper() {
    }

}
