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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.Assignment;

public class EmployeeChangeMove implements Move, TabuPropertyEnabled {

    private Assignment assignment;
    private Employee toEmployee;

    public EmployeeChangeMove(Assignment assignment, Employee toEmployee) {
        this.assignment = assignment;
        this.toEmployee = toEmployee;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(assignment.getEmployee(), toEmployee);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new EmployeeChangeMove(assignment, assignment.getEmployee());
    }

    public void doMove(WorkingMemory workingMemory) {
        NurseRosteringMoveHelper.moveEmployee(workingMemory, assignment, toEmployee);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(assignment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeChangeMove) {
            EmployeeChangeMove other = (EmployeeChangeMove) o;
            return new EqualsBuilder()
                    .append(assignment, other.assignment)
                    .append(toEmployee, other.toEmployee)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(assignment)
                .append(toEmployee)
                .toHashCode();
    }

    public String toString() {
        return assignment + " => " + toEmployee;
    }

}
