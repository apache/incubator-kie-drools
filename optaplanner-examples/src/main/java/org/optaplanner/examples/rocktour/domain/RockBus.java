package org.optaplanner.examples.rocktour.domain;

import java.time.LocalDate;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class RockBus extends AbstractPersistable implements RockStandstill {

    private RockLocation startLocation;
    private LocalDate startDate;
    private RockLocation endLocation;
    private LocalDate endDate;

    private RockShow nextShow;

    public RockBus() {
    }

    @Override
    public RockLocation getDepartureLocation() {
        return startLocation;
    }

    @Override
    public LocalDate getDepartureDate() {
        return startDate;
    }

    @Override
    public RockTimeOfDay getDepartureTimeOfDay() {
        return RockTimeOfDay.EARLY;
    }

    @Override
    public RockStandstill getHosWeekStart() {
        return this;
    }

    @Override
    public Long getHosWeekDrivingSecondsTotal() {
        return 0L;
    }

    @Override
    public RockLocation getArrivalLocation() {
        return endLocation;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public RockLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(RockLocation startLocation) {
        this.startLocation = startLocation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public RockLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(RockLocation endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public RockShow getNextShow() {
        return nextShow;
    }

    @Override
    public void setNextShow(RockShow nextShow) {
        this.nextShow = nextShow;
    }

}
