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

package org.drools.planner.examples.manners2009.solver.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.examples.manners2009.domain.Seat;
import org.drools.planner.examples.manners2009.domain.SeatDesignation;

public class SeatDesignationSwapMove implements Move {

    private SeatDesignation leftSeatDesignation;
    private SeatDesignation rightSeatDesignation;

    public SeatDesignationSwapMove(SeatDesignation leftSeatDesignation, SeatDesignation rightSeatDesignation) {
        this.leftSeatDesignation = leftSeatDesignation;
        this.rightSeatDesignation = rightSeatDesignation;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(leftSeatDesignation.getSeat(), rightSeatDesignation.getSeat());
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new SeatDesignationSwapMove(rightSeatDesignation, leftSeatDesignation);
    }

    public void doMove(ScoreDirector scoreDirector) {
        Seat oldLeftSeat = leftSeatDesignation.getSeat();
        Seat oldRightSeat = rightSeatDesignation.getSeat();
        moveSeat(scoreDirector, leftSeatDesignation, oldRightSeat);
        moveSeat(scoreDirector, rightSeatDesignation, oldLeftSeat);
    }

    // Extract to helper class if other moves are created
    private static void moveSeat(ScoreDirector scoreDirector, SeatDesignation seatDesignation, Seat toSeat) {
        scoreDirector.beforeVariableChanged(seatDesignation, "seat");
        seatDesignation.setSeat(toSeat);
        scoreDirector.afterVariableChanged(seatDesignation, "seat");
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftSeatDesignation, rightSeatDesignation);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftSeatDesignation.getSeat(), rightSeatDesignation.getSeat());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SeatDesignationSwapMove) {
            SeatDesignationSwapMove other = (SeatDesignationSwapMove) o;
            return new EqualsBuilder()
                    .append(leftSeatDesignation, other.leftSeatDesignation)
                    .append(rightSeatDesignation, other.rightSeatDesignation)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftSeatDesignation)
                .append(rightSeatDesignation)
                .toHashCode();
    }

    public String toString() {
        return leftSeatDesignation + " <=> " + rightSeatDesignation;
    }

}
