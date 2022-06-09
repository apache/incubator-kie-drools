package org.optaplanner.examples.nurserostering.domain.pattern;

import org.optaplanner.examples.nurserostering.domain.ShiftType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ShiftType2DaysPattern")
public class ShiftType2DaysPattern extends Pattern {

    private ShiftType dayIndex0ShiftType;
    private ShiftType dayIndex1ShiftType;

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

    @Override
    public String toString() {
        return "Work pattern: " + dayIndex0ShiftType + ", " + dayIndex1ShiftType;
    }

}
