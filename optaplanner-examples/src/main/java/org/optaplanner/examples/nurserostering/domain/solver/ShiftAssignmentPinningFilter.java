package org.optaplanner.examples.nurserostering.domain.solver;

import org.optaplanner.core.api.domain.entity.PinningFilter;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;

public class ShiftAssignmentPinningFilter implements PinningFilter<NurseRoster, ShiftAssignment> {

    @Override
    public boolean accept(NurseRoster nurseRoster, ShiftAssignment shiftAssignment) {
        ShiftDate shiftDate = shiftAssignment.getShift().getShiftDate();
        return !nurseRoster.getNurseRosterParametrization().isInPlanningWindow(shiftDate);
    }

}
