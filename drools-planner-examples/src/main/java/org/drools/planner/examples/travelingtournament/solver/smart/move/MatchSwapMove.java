/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.travelingtournament.solver.smart.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.examples.travelingtournament.domain.Day;
import org.drools.planner.examples.travelingtournament.domain.Match;
import org.drools.planner.examples.travelingtournament.solver.move.TravelingTournamentMoveHelper;

public class MatchSwapMove implements Move {

    private Match leftMatch;
    private Match rightMatch;

    public MatchSwapMove(Match leftMatch, Match rightMatch) {
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return this;
    }

    public void doMove(ScoreDirector scoreDirector) {
        Day oldLeftDay = leftMatch.getDay();
        Day oldRightDay = rightMatch.getDay();
        TravelingTournamentMoveHelper.moveDay(scoreDirector, leftMatch, oldRightDay);
        TravelingTournamentMoveHelper.moveDay(scoreDirector, rightMatch, oldLeftDay);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftMatch, rightMatch);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftMatch.getDay(), rightMatch.getDay());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MatchSwapMove) {
            MatchSwapMove other = (MatchSwapMove) o;
            return new EqualsBuilder()
                    .append(leftMatch, other.leftMatch)
                    .append(rightMatch, other.rightMatch)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftMatch)
                .append(rightMatch)
                .toHashCode();
    }

    public String toString() {
        return leftMatch + " <=> " + rightMatch;
    }

}
