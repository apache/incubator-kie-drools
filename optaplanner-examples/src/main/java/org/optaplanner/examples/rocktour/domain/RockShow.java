/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.domain;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.util.NavigableSet;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.rocktour.domain.solver.RockShowVariableListener;

@PlanningEntity
public class RockShow extends AbstractPersistable implements RockStandstill {

    private String venueName;
    private RockLocation location;
    private int durationInHalfDay;
    private int revenueOpportunity;
    private boolean required;

    private NavigableSet<LocalDate> availableDateSet;

    @PlanningVariable(valueRangeProviderRefs = { "busRange", "showRange" }, graphType = PlanningVariableGraphType.CHAINED)
    private RockStandstill previousStandstill;

    private RockShow nextShow;

    @AnchorShadowVariable(sourceVariableName = "previousStandstill")
    private RockBus bus;

    @CustomShadowVariable(variableListenerClass = RockShowVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "previousStandstill"),
            @PlanningVariableReference(variableName = "bus") })
    private LocalDate date;

    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "date"))
    private RockTimeOfDay timeOfDay; // There can be 2 shows on the same date (early and late)

    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "date"))
    private RockStandstill hosWeekStart; // HOS stands for Hours of Service regulation

    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "date"))
    private Long hosWeekDrivingSecondsTotal; // HOS stands for Hours of Service regulation

    public RockShow() {
    }

    @Override
    public RockLocation getDepartureLocation() {
        return location;
    }

    @Override
    public LocalDate getDepartureDate() {
        if (date == null) {
            return null;
        }
        return date.plusDays((durationInHalfDay - 1) / 2);
    }

    @Override
    public RockTimeOfDay getDepartureTimeOfDay() {
        return durationInHalfDay % 2 == 0 ? RockTimeOfDay.LATE : timeOfDay;
    }

    @Override
    public RockLocation getArrivalLocation() {
        return location;
    }

    public long getDrivingTimeFromPreviousStandstill() {
        return previousStandstill.getDepartureLocation().getDrivingTimeTo(location);
    }

    public long getDrivingTimeToBusArrivalLocation() {
        return location.getDrivingTimeTo(bus.getArrivalLocation());
    }

    public long getDaysAfterBusDeparture() {
        return DAYS.between(bus.getDepartureDate(), date);
    }

    @Override
    public String toString() {
        return venueName;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public RockLocation getLocation() {
        return location;
    }

    public void setLocation(RockLocation location) {
        this.location = location;
    }

    public int getDurationInHalfDay() {
        return durationInHalfDay;
    }

    public void setDurationInHalfDay(int durationInHalfDay) {
        this.durationInHalfDay = durationInHalfDay;
    }

    public int getRevenueOpportunity() {
        return revenueOpportunity;
    }

    public void setRevenueOpportunity(int revenueOpportunity) {
        this.revenueOpportunity = revenueOpportunity;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public NavigableSet<LocalDate> getAvailableDateSet() {
        return availableDateSet;
    }

    public void setAvailableDateSet(NavigableSet<LocalDate> availableDateSet) {
        this.availableDateSet = availableDateSet;
    }

    public RockStandstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(RockStandstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    @Override
    public RockShow getNextShow() {
        return nextShow;
    }

    @Override
    public void setNextShow(RockShow nextShow) {
        this.nextShow = nextShow;
    }

    public RockBus getBus() {
        return bus;
    }

    public void setBus(RockBus bus) {
        this.bus = bus;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public RockTimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(RockTimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public RockStandstill getHosWeekStart() {
        return hosWeekStart;
    }

    public void setHosWeekStart(RockStandstill hosWeekStart) {
        this.hosWeekStart = hosWeekStart;
    }

    @Override
    public Long getHosWeekDrivingSecondsTotal() {
        return hosWeekDrivingSecondsTotal;
    }

    public void setHosWeekDrivingSecondsTotal(Long hosWeekDrivingSecondsTotal) {
        this.hosWeekDrivingSecondsTotal = hosWeekDrivingSecondsTotal;
    }

}
