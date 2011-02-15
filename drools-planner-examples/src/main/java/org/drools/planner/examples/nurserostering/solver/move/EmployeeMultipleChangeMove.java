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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;

public class EmployeeMultipleChangeMove implements Move, TabuPropertyEnabled {

    private Employee fromEmployee;
    private List<Assignment> assignmentList;
    private Employee toEmployee;

    public EmployeeMultipleChangeMove(Employee fromEmployee, List<Assignment> assignmentList, Employee toEmployee) {
        this.fromEmployee = fromEmployee;
        this.assignmentList = assignmentList;
        this.toEmployee = toEmployee;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(fromEmployee, toEmployee);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new EmployeeMultipleChangeMove(toEmployee, assignmentList, fromEmployee);
    }

    public void doMove(WorkingMemory workingMemory) {
        for (Assignment assignment : assignmentList) {
            if (!assignment.getEmployee().equals(fromEmployee)) {
                throw new IllegalStateException("The assignment (" + assignment + ") should have the same employee ("
                        + fromEmployee + ") as the fromEmployee (" + fromEmployee + ").");
            }
            NurseRosteringMoveHelper.moveEmployee(workingMemory, assignment, toEmployee);
        }
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(assignmentList);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeMultipleChangeMove) {
            EmployeeMultipleChangeMove other = (EmployeeMultipleChangeMove) o;
            return new EqualsBuilder()
                    .append(fromEmployee, other.fromEmployee)
                    .append(assignmentList, other.assignmentList)
                    .append(toEmployee, other.toEmployee)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(fromEmployee)
                .append(assignmentList)
                .append(toEmployee)
                .toHashCode();
    }

    public String toString() {
        return assignmentList + " => " + toEmployee;
    }

}
