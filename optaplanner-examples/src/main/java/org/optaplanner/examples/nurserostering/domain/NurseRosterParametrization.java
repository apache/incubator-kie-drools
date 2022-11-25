package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class NurseRosterParametrization extends AbstractPersistable {

    private ShiftDate firstShiftDate;
    private ShiftDate lastShiftDate;

    private ShiftDate planningWindowStart;

    public NurseRosterParametrization() {
    }

    public NurseRosterParametrization(long id, ShiftDate firstShiftDate, ShiftDate lastShiftDate,
            ShiftDate planningWindowStart) {
        super(id);
        this.firstShiftDate = firstShiftDate;
        this.lastShiftDate = lastShiftDate;
        this.planningWindowStart = planningWindowStart;
    }

    public ShiftDate getFirstShiftDate() {
        return firstShiftDate;
    }

    public void setFirstShiftDate(ShiftDate firstShiftDate) {
        this.firstShiftDate = firstShiftDate;
    }

    public ShiftDate getLastShiftDate() {
        return lastShiftDate;
    }

    public void setLastShiftDate(ShiftDate lastShiftDate) {
        this.lastShiftDate = lastShiftDate;
    }

    @JsonIgnore
    public int getFirstShiftDateDayIndex() {
        return firstShiftDate.getDayIndex();
    }

    @JsonIgnore
    public int getLastShiftDateDayIndex() {
        return lastShiftDate.getDayIndex();
    }

    public ShiftDate getPlanningWindowStart() {
        return planningWindowStart;
    }

    public void setPlanningWindowStart(ShiftDate planningWindowStart) {
        this.planningWindowStart = planningWindowStart;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isInPlanningWindow(ShiftDate shiftDate) {
        return planningWindowStart.getDayIndex() <= shiftDate.getDayIndex();
    }

    @Override
    public String toString() {
        return firstShiftDate + " - " + lastShiftDate;
    }

}
