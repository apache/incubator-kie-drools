package org.optaplanner.examples.nurserostering.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class EmployeeMultipleChangeMove extends AbstractMove<NurseRoster> {

    private Employee fromEmployee;
    private List<ShiftAssignment> shiftAssignmentList;
    private Employee toEmployee;

    public EmployeeMultipleChangeMove(Employee fromEmployee, List<ShiftAssignment> shiftAssignmentList, Employee toEmployee) {
        this.fromEmployee = fromEmployee;
        this.shiftAssignmentList = shiftAssignmentList;
        this.toEmployee = toEmployee;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<NurseRoster> scoreDirector) {
        return !Objects.equals(fromEmployee, toEmployee);
    }

    @Override
    public EmployeeMultipleChangeMove createUndoMove(ScoreDirector<NurseRoster> scoreDirector) {
        return new EmployeeMultipleChangeMove(toEmployee, shiftAssignmentList, fromEmployee);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<NurseRoster> scoreDirector) {
        for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
            if (!shiftAssignment.getEmployee().equals(fromEmployee)) {
                throw new IllegalStateException("The shiftAssignment (" + shiftAssignment + ") should have the same employee ("
                        + shiftAssignment.getEmployee() + ") as the fromEmployee (" + fromEmployee + ").");
            }
            NurseRosteringMoveHelper.moveEmployee(scoreDirector, shiftAssignment, toEmployee);
        }
    }

    @Override
    public EmployeeMultipleChangeMove rebase(ScoreDirector<NurseRoster> destinationScoreDirector) {
        return new EmployeeMultipleChangeMove(destinationScoreDirector.lookUpWorkingObject(fromEmployee),
                rebaseList(shiftAssignmentList, destinationScoreDirector),
                destinationScoreDirector.lookUpWorkingObject(toEmployee));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(shiftAssignmentList);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(fromEmployee, toEmployee);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeMultipleChangeMove other = (EmployeeMultipleChangeMove) o;
        return Objects.equals(fromEmployee, other.fromEmployee) &&
                Objects.equals(shiftAssignmentList, other.shiftAssignmentList) &&
                Objects.equals(toEmployee, other.toEmployee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromEmployee, shiftAssignmentList, toEmployee);
    }

    @Override
    public String toString() {
        return shiftAssignmentList + " {? -> " + toEmployee + "}";
    }

}
