/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;

public class MatchChainRotationsMove extends AbstractMove {

    private List<Match> firstMatchList;
    private List<Match> secondMatchList;

    public MatchChainRotationsMove(List<Match> firstMatchList, List<Match> secondMatchList) {
        this.firstMatchList = firstMatchList;
        this.secondMatchList = secondMatchList;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        List<Match> inverseFirstMatchList = new ArrayList<Match>(firstMatchList);
        Collections.reverse(inverseFirstMatchList);
        List<Match> inverseSecondMatchList = new ArrayList<Match>(secondMatchList);
        Collections.reverse(inverseSecondMatchList);
        return new MatchChainRotationsMove(inverseFirstMatchList, inverseSecondMatchList);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        rotateList(scoreDirector, firstMatchList);
        if (!secondMatchList.isEmpty()) { // TODO create SingleMatchListRotateMove
            rotateList(scoreDirector, secondMatchList);
        }
    }

    private void rotateList(ScoreDirector scoreDirector, List<Match> matchList) {
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

    public Collection<? extends Object> getPlanningEntities() {
        List<Match> entities = new ArrayList<Match>(firstMatchList.size() + secondMatchList.size());
        entities.addAll(firstMatchList);
        entities.addAll(secondMatchList);
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Day> values = new ArrayList<Day>(firstMatchList.size() + secondMatchList.size());
        for (Match match : firstMatchList) {
            values.add(match.getDay());
        }
        for (Match match : secondMatchList) {
            values.add(match.getDay());
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MatchChainRotationsMove) {
            MatchChainRotationsMove other = (MatchChainRotationsMove) o;
            return new EqualsBuilder()
                    .append(firstMatchList, other.firstMatchList)
                    .append(secondMatchList, other.secondMatchList)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(firstMatchList)
                .append(secondMatchList)
                .toHashCode();
    }

    public String toString() {
        return "Rotation " + firstMatchList + " & Rotation " + secondMatchList;
    }

}
