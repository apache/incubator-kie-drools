/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.travelingtournament.solver.smart.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.heuristic.selector.move.factory.MoveListFactory;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.travelingtournament.domain.Match;
import org.drools.planner.examples.travelingtournament.domain.TravelingTournament;
import org.drools.planner.examples.travelingtournament.solver.smart.move.MatchSwapMove;

public class MatchSwapMoveFactory implements MoveListFactory {

    public List<Move> createMoveList(Solution solution) {
        TravelingTournament travelingTournament = (TravelingTournament) solution;
        List<Match> matchList = travelingTournament.getMatchList();
        List<Move> moveList = new ArrayList<Move>(matchList.size() / 2);
        for (Match firstMatch : matchList) {
            for (Match secondMatch : matchList) {
                if (firstMatch.getHomeTeam().equals(secondMatch.getAwayTeam())
                        && firstMatch.getAwayTeam().equals(secondMatch.getHomeTeam())
                        && (firstMatch.getId().compareTo(secondMatch.getId()) < 0)) {
                    MatchSwapMove matchSwapMove = new MatchSwapMove(firstMatch, secondMatch);
                    moveList.add(matchSwapMove);
                    break;
                }
            }
        }
        return moveList;
    }

}
