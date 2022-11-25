package org.optaplanner.examples.nurserostering.domain.pattern;

import org.optaplanner.examples.nurserostering.domain.ShiftType;

public class ShiftType3DaysPattern extends Pattern {

    private ShiftType dayIndex0ShiftType;
    private ShiftType dayIndex1ShiftType;
    private ShiftType dayIndex2ShiftType;

    public ShiftType3DaysPattern() {
    }

    public ShiftType3DaysPattern(long id, String code) {
        super(id, code);
    }

    public ShiftType getDayIndex0ShiftType() {
        return dayIndex0ShiftType;
    }

    public void setDayIndex0ShiftType(ShiftType dayIndex0ShiftType) {
        this.dayIndex0ShiftType = dayIndex0ShiftType;
    }

    public ShiftType getDayIndex1ShiftType() {
        return dayIndex1ShiftType;
    }

    public void setDayIndex1ShiftType(ShiftType dayIndex1ShiftType) {
        this.dayIndex1ShiftType = dayIndex1ShiftType;
    }

    public ShiftType getDayIndex2ShiftType() {
        return dayIndex2ShiftType;
    }

    public void setDayIndex2ShiftType(ShiftType dayIndex2ShiftType) {
        this.dayIndex2ShiftType = dayIndex2ShiftType;
    }

    @Override
    public String toString() {
        return "Work pattern: " + dayIndex0ShiftType + ", " + dayIndex1ShiftType + ", " + dayIndex2ShiftType;
    }

}
