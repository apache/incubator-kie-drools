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

package org.drools.planner.examples.nurserostering.solver.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;

public class AssignmentSwitchMove implements Move, TabuPropertyEnabled {

    private Assignment leftAssignment;
    private Assignment rightAssignment;

    public AssignmentSwitchMove(Assignment leftAssignment, Assignment rightAssignment) {
        this.leftAssignment = leftAssignment;
        this.rightAssignment = rightAssignment;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(leftAssignment.getEmployee(), rightAssignment.getEmployee());
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new AssignmentSwitchMove(rightAssignment, leftAssignment);
    }

    public void doMove(WorkingMemory workingMemory) {
        Employee oldLeftEmployee = leftAssignment.getEmployee();
        Employee oldRightEmployee = rightAssignment.getEmployee();
        NurseRosteringMoveHelper.moveEmployee(workingMemory, leftAssignment, oldRightEmployee);
        NurseRosteringMoveHelper.moveEmployee(workingMemory, rightAssignment, oldLeftEmployee);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<Assignment>asList(leftAssignment, rightAssignment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof AssignmentSwitchMove) {
            AssignmentSwitchMove other = (AssignmentSwitchMove) o;
            return new EqualsBuilder()
                    .append(leftAssignment, other.leftAssignment)
                    .append(rightAssignment, other.rightAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftAssignment)
                .append(rightAssignment)
                .toHashCode();
    }

    public String toString() {
        return leftAssignment + " <=> " + rightAssignment;
    }

}
