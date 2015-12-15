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

package org.optaplanner.examples.nurserostering.solver.move;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class EmployeeChangeMove extends AbstractMove {

    private ShiftAssignment shiftAssignment;
    private Employee toEmployee;

    public EmployeeChangeMove(ShiftAssignment shiftAssignment, Employee toEmployee) {
        this.shiftAssignment = shiftAssignment;
        this.toEmployee = toEmployee;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(shiftAssignment.getEmployee(), toEmployee);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new EmployeeChangeMove(shiftAssignment, shiftAssignment.getEmployee());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        NurseRosteringMoveHelper.moveEmployee(scoreDirector, shiftAssignment, toEmployee);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(shiftAssignment);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toEmployee);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeChangeMove) {
            EmployeeChangeMove other = (EmployeeChangeMove) o;
            return new EqualsBuilder()
                    .append(shiftAssignment, other.shiftAssignment)
                    .append(toEmployee, other.toEmployee)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(shiftAssignment)
                .append(toEmployee)
                .toHashCode();
    }

    public String toString() {
        return shiftAssignment + " {" + shiftAssignment.getEmployee() + " -> " + toEmployee + "}";
    }

}
