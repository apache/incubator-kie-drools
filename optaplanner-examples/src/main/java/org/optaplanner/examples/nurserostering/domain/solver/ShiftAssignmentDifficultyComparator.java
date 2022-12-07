package org.optaplanner.examples.nurserostering.domain.solver;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Collections;
import java.util.Comparator;

import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftType;

public class ShiftAssignmentDifficultyComparator implements Comparator<ShiftAssignment> {

    private static final Comparator<Shift> COMPARATOR = comparing(Shift::getShiftDate,
            Collections.reverseOrder(comparing(ShiftDate::getDate)))
            .thenComparing(Shift::getShiftType, comparingLong(ShiftType::getId).reversed())
            .thenComparingInt(Shift::getRequiredEmployeeSize);

    @Override
    public int compare(ShiftAssignment a, ShiftAssignment b) {
        Shift aShift = a.getShift();
        Shift bShift = b.getShift();
        return COMPARATOR.compare(aShift, bShift);
    }
}
