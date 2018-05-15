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

import java.time.LocalDate;
import java.util.NavigableSet;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class RockShow extends AbstractPersistable implements RockStandstill {

    private String venueName;
    private RockLocation location;
    private int revenueOpportunity;
    private boolean required;

    private NavigableSet<LocalDate> availableDateSet;

    @PlanningVariable(valueRangeProviderRefs = {"busRange", "showRange"}, graphType = PlanningVariableGraphType.CHAINED)
    private RockStandstill previousStandstill;

    public RockShow() {
    }

    @Override
    public RockLocation getDepartureLocation() {
        return location;
    }

    @Override
    public RockLocation getArrivalLocation() {
        return location;
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
}
