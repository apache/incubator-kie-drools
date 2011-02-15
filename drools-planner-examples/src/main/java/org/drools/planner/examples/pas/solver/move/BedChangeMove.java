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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.runtime.rule.FactHandle;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;

public class BedChangeMove implements Move, TabuPropertyEnabled {

    private BedDesignation bedDesignation;
    private Bed toBed;

    public BedChangeMove(BedDesignation bedDesignation, Bed toBed) {
        this.bedDesignation = bedDesignation;
        this.toBed = toBed;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(bedDesignation.getBed(), toBed);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new BedChangeMove(bedDesignation, bedDesignation.getBed());
    }

    public void doMove(WorkingMemory workingMemory) {
        FactHandle factHandle = workingMemory.getFactHandle(bedDesignation);
        bedDesignation.setBed(toBed);
        workingMemory.update(factHandle, bedDesignation);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(bedDesignation);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BedChangeMove) {
            BedChangeMove other = (BedChangeMove) o;
            return new EqualsBuilder()
                    .append(bedDesignation, other.bedDesignation)
                    .append(toBed, other.toBed)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(bedDesignation)
                .append(toBed)
                .toHashCode();
    }

    public String toString() {
        return bedDesignation + " => " + toBed;
    }

}
