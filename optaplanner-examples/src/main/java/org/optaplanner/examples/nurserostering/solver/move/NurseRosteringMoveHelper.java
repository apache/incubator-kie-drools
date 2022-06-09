package org.optaplanner.examples.nurserostering.solver.move;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class NurseRosteringMoveHelper {

    public static void moveEmployee(ScoreDirector<NurseRoster> scoreDirector, ShiftAssignment shiftAssignment,
            Employee toEmployee) {
        scoreDirector.beforeVariableChanged(shiftAssignment, "employee");
        shiftAssignment.setEmployee(toEmployee);
        scoreDirector.afterVariableChanged(shiftAssignment, "employee");
    }

    private NurseRosteringMoveHelper() {
    }

}
