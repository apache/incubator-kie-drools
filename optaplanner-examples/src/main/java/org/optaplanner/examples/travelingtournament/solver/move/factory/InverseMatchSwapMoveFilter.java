package org.optaplanner.examples.travelingtournament.solver.move.factory;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class InverseMatchSwapMoveFilter implements SelectionFilter<TravelingTournament, SwapMove> {

    @Override
    public boolean accept(ScoreDirector<TravelingTournament> scoreDirector, SwapMove move) {
        Match leftMatch = (Match) move.getLeftEntity();
        Match rightMatch = (Match) move.getRightEntity();
        return leftMatch.getHomeTeam().equals(rightMatch.getAwayTeam())
                && leftMatch.getAwayTeam().equals(rightMatch.getHomeTeam());
    }

}
