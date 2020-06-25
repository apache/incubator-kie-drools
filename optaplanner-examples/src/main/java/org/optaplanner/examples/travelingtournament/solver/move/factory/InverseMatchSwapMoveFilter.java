/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
