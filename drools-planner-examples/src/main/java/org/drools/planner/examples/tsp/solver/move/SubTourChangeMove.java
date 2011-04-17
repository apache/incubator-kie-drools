/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.solver.move;

import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.tsp.domain.CityAssignment;

public class SubTourChangeMove implements Move, TabuPropertyEnabled {

    private CityAssignment startCityAssignment;
    private CityAssignment endCityAssignment;

    private CityAssignment toAfterCityAssignment;

    public SubTourChangeMove(CityAssignment startCityAssignment, CityAssignment endCityAssignment, CityAssignment toAfterCityAssignment) {
        this.startCityAssignment = startCityAssignment;
        this.endCityAssignment = endCityAssignment;
        this.toAfterCityAssignment = toAfterCityAssignment;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        CityAssignment nextCityAssignment = startCityAssignment;
        if (ObjectUtils.equals(startCityAssignment, toAfterCityAssignment.getNextCityAssignment())) {
            return false;
        }
        while (!ObjectUtils.equals(nextCityAssignment, endCityAssignment)) {
            if (ObjectUtils.equals(nextCityAssignment, toAfterCityAssignment)) {
                return false;
            }
            nextCityAssignment = nextCityAssignment.getNextCityAssignment();
        }
        if (ObjectUtils.equals(endCityAssignment, toAfterCityAssignment)) {
            return false;
        }
        return true;
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new SubTourChangeMove(startCityAssignment, endCityAssignment,
                startCityAssignment.getPreviousCityAssignment());
    }

    public void doMove(WorkingMemory workingMemory) {
        CityAssignment newPreviousCityAssignment = toAfterCityAssignment;
        CityAssignment newNextCityAssignment = newPreviousCityAssignment.getNextCityAssignment();
        CityAssignment originalPreviousCityAssignment = startCityAssignment.getPreviousCityAssignment();
        CityAssignment originalNextCityAssignment = endCityAssignment.getNextCityAssignment();
        TspMoveHelper.moveCityAssignmentAfterCityAssignment(workingMemory, newPreviousCityAssignment, startCityAssignment);
        TspMoveHelper.moveCityAssignmentAfterCityAssignment(workingMemory, endCityAssignment, newNextCityAssignment);
        TspMoveHelper.moveCityAssignmentAfterCityAssignment(workingMemory, originalPreviousCityAssignment, originalNextCityAssignment);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(startCityAssignment.getCity());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SubTourChangeMove) {
            SubTourChangeMove other = (SubTourChangeMove) o;
            return new EqualsBuilder()
                    .append(startCityAssignment, other.startCityAssignment)
                    .append(endCityAssignment, other.endCityAssignment)
                    .append(toAfterCityAssignment, other.toAfterCityAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(startCityAssignment)
                .append(endCityAssignment)
                .append(toAfterCityAssignment)
                .toHashCode();
    }

    public String toString() {
        return startCityAssignment + "-" + endCityAssignment + " => after " + toAfterCityAssignment;
    }

}
