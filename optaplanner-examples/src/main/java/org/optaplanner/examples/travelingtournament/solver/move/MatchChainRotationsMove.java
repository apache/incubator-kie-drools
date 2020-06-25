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

package org.optaplanner.examples.travelingtournament.solver.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class MatchChainRotationsMove extends AbstractMove<TravelingTournament> {

    private List<Match> firstMatchList;
    private List<Match> secondMatchList;

    public MatchChainRotationsMove(List<Match> firstMatchList, List<Match> secondMatchList) {
        this.firstMatchList = firstMatchList;
        this.secondMatchList = secondMatchList;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TravelingTournament> scoreDirector) {
        return true;
    }

    @Override
    public MatchChainRotationsMove createUndoMove(ScoreDirector<TravelingTournament> scoreDirector) {
        List<Match> inverseFirstMatchList = new ArrayList<>(firstMatchList);
        Collections.reverse(inverseFirstMatchList);
        List<Match> inverseSecondMatchList = new ArrayList<>(secondMatchList);
        Collections.reverse(inverseSecondMatchList);
        return new MatchChainRotationsMove(inverseFirstMatchList, inverseSecondMatchList);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TravelingTournament> scoreDirector) {
        rotateList(scoreDirector, firstMatchList);
        if (!secondMatchList.isEmpty()) { // TODO create SingleMatchListRotateMove
            rotateList(scoreDirector, secondMatchList);
        }
    }

    private void rotateList(ScoreDirector<TravelingTournament> scoreDirector, List<Match> matchList) {
        Iterator<Match> it = matchList.iterator();
        Match previousMatch = it.next();
        Match match = null;
        Day firstDay = previousMatch.getDay();
        while (it.hasNext()) {
            match = it.next();
            TravelingTournamentMoveHelper.moveDay(scoreDirector, previousMatch, match.getDay());
            previousMatch = match;
        }
        TravelingTournamentMoveHelper.moveDay(scoreDirector, match, firstDay);
    }

    @Override
    public MatchChainRotationsMove rebase(ScoreDirector<TravelingTournament> destinationScoreDirector) {
        return new MatchChainRotationsMove(rebaseList(firstMatchList, destinationScoreDirector),
                rebaseList(secondMatchList, destinationScoreDirector));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        List<Match> entities = new ArrayList<>(firstMatchList.size() + secondMatchList.size());
        entities.addAll(firstMatchList);
        entities.addAll(secondMatchList);
        return entities;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        List<Day> values = new ArrayList<>(firstMatchList.size() + secondMatchList.size());
        for (Match match : firstMatchList) {
            values.add(match.getDay());
        }
        for (Match match : secondMatchList) {
            values.add(match.getDay());
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MatchChainRotationsMove other = (MatchChainRotationsMove) o;
        return Objects.equals(firstMatchList, other.firstMatchList) &&
                Objects.equals(secondMatchList, other.secondMatchList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstMatchList, secondMatchList);
    }

    @Override
    public String toString() {
        return "Rotation " + firstMatchList + " & Rotation " + secondMatchList;
    }

}
