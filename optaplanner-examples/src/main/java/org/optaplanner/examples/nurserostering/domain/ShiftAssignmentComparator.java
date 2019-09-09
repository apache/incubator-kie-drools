package org.optaplanner.examples.nurserostering.domain;

import java.util.Comparator;

public class ShiftAssignmentComparator implements Comparator<ShiftAssignment> {

    private static final Comparator<Shift> COMPARATOR =
            Comparator.comparing(Shift::getShiftDate)
                    .thenComparing(a -> a.getShiftType().getStartTimeString())
                    .thenComparing(a -> a.getShiftType().getEndTimeString());

    @Override
    public int compare(ShiftAssignment o1, ShiftAssignment o2) {
        return COMPARATOR.compare(o1.getShift(), o2.getShift());
    }
}
