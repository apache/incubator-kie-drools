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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class ShiftAssignmentSwapMove extends AbstractMove<NurseRoster> {

    private ShiftAssignment leftShiftAssignment;
    private ShiftAssignment rightShiftAssignment;

    public ShiftAssignmentSwapMove(ShiftAssignment leftShiftAssignment, ShiftAssignment rightShiftAssignment) {
        this.leftShiftAssignment = leftShiftAssignment;
        this.rightShiftAssignment = rightShiftAssignment;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<NurseRoster> scoreDirector) {
        return !Objects.equals(leftShiftAssignment.getEmployee(), rightShiftAssignment.getEmployee());
    }

    @Override
    public ShiftAssignmentSwapMove createUndoMove(ScoreDirector<NurseRoster> scoreDirector) {
        return new ShiftAssignmentSwapMove(rightShiftAssignment, leftShiftAssignment);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<NurseRoster> scoreDirector) {
        Employee oldLeftEmployee = leftShiftAssignment.getEmployee();
        Employee oldRightEmployee = rightShiftAssignment.getEmployee();
        NurseRosteringMoveHelper.moveEmployee(scoreDirector, leftShiftAssignment, oldRightEmployee);
        NurseRosteringMoveHelper.moveEmployee(scoreDirector, rightShiftAssignment, oldLeftEmployee);
    }

    @Override
    public ShiftAssignmentSwapMove rebase(ScoreDirector<NurseRoster> destinationScoreDirector) {
        return new ShiftAssignmentSwapMove(destinationScoreDirector.lookUpWorkingObject(leftShiftAssignment),
                destinationScoreDirector.lookUpWorkingObject(rightShiftAssignment));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftShiftAssignment, rightShiftAssignment);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftShiftAssignment.getEmployee(), rightShiftAssignment.getEmployee());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ShiftAssignmentSwapMove) {
            ShiftAssignmentSwapMove other = (ShiftAssignmentSwapMove) o;
            return new EqualsBuilder()
                    .append(leftShiftAssignment, other.leftShiftAssignment)
                    .append(rightShiftAssignment, other.rightShiftAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftShiftAssignment)
                .append(rightShiftAssignment)
                .toHashCode();
    }

    @Override
    public String toString() {
        return leftShiftAssignment + " {" + leftShiftAssignment.getEmployee() + "} <-> "
                + rightShiftAssignment + " {" + rightShiftAssignment.getEmployee() + "}";
    }

}
