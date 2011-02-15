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

package org.drools.planner.examples.nqueens.solver.move;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.FactHandle;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.nqueens.domain.Queen;

public class YChangeMove implements Move, TabuPropertyEnabled {

    private Queen queen;
    private int toY;

    public YChangeMove(Queen queen, int toY) {
        this.queen = queen;
        this.toY = toY;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return queen.getY() != toY;
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new YChangeMove(queen, queen.getY());
    }

    public void doMove(WorkingMemory workingMemory) {
        FactHandle queenHandle = workingMemory.getFactHandle(queen);
        queen.setY(toY);
        workingMemory.update(queenHandle, queen); // after changes are made
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(queen);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof YChangeMove) {
            YChangeMove other = (YChangeMove) o;
            return new EqualsBuilder()
                    .append(queen, other.queen)
                    .append(toY, other.toY)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(queen)
                .append(toY)
                .toHashCode();
    }

    public String toString() {
        return queen + " => " + toY;
    }

}
