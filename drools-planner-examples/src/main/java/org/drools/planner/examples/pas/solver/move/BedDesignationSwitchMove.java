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

package org.drools.planner.examples.pas.solver.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.runtime.rule.FactHandle;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;

public class BedDesignationSwitchMove implements Move, TabuPropertyEnabled {

    private BedDesignation leftBedDesignation;
    private BedDesignation rightBedDesignation;

    public BedDesignationSwitchMove(BedDesignation leftBedDesignation, BedDesignation rightBedDesignation) {
        this.leftBedDesignation = leftBedDesignation;
        this.rightBedDesignation = rightBedDesignation;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(leftBedDesignation.getBed(), rightBedDesignation.getBed());
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new BedDesignationSwitchMove(rightBedDesignation, leftBedDesignation);
    }

    public void doMove(WorkingMemory workingMemory) {
        Bed oldLeftBed = leftBedDesignation.getBed();
        Bed oldRightBed = rightBedDesignation.getBed();
        moveBed(workingMemory, leftBedDesignation, oldRightBed);
        moveBed(workingMemory, rightBedDesignation, oldLeftBed);
    }

    // Extract to helper class if other moves are created
    private static void moveBed(WorkingMemory workingMemory, BedDesignation bedDesignation, Bed toBed) {
        FactHandle factHandle = workingMemory.getFactHandle(bedDesignation);
        bedDesignation.setBed(toBed);
        workingMemory.update(factHandle, bedDesignation);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<BedDesignation>asList(leftBedDesignation, rightBedDesignation);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BedDesignationSwitchMove) {
            BedDesignationSwitchMove other = (BedDesignationSwitchMove) o;
            return new EqualsBuilder()
                    .append(leftBedDesignation, other.leftBedDesignation)
                    .append(rightBedDesignation, other.rightBedDesignation)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftBedDesignation)
                .append(rightBedDesignation)
                .toHashCode();
    }

    public String toString() {
        return leftBedDesignation + " <=> " + rightBedDesignation;
    }

}
