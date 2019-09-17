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

package org.optaplanner.examples.flightcrewscheduling.domain;

import java.util.Comparator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class FlightAssignment extends AbstractPersistable implements Comparable<FlightAssignment> {

    private static final Comparator<FlightAssignment> PILLAR_SEQUENCE_COMPARATOR =
            Comparator.comparing((FlightAssignment a) -> a.getFlight().getDepartureUTCDateTime())
                    .thenComparing(a -> a.getFlight().getArrivalUTCDateTime())
                    .thenComparing(FlightAssignment::getIndexInFlight);

    private Flight flight;
    private int indexInFlight;
    private Skill requiredSkill;

    @PlanningVariable(valueRangeProviderRefs = {"employeeRange"})
    private Employee employee;

    public FlightAssignment() {
    }

    public long getFlightDurationInMinutes() {
        return flight.getDurationInMinutes();
    }

    @Override
    public String toString() {
        return flight + "-" + indexInFlight;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getIndexInFlight() {
        return indexInFlight;
    }

    public void setIndexInFlight(int indexInFlight) {
        this.indexInFlight = indexInFlight;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    public void setRequiredSkill(Skill requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public int compareTo(FlightAssignment o) {
        return PILLAR_SEQUENCE_COMPARATOR.compare(this, o);
    }
}
