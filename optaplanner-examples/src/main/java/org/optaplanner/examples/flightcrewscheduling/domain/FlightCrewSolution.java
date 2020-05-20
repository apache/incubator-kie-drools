/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.time.LocalDate;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningSolution
public class FlightCrewSolution extends AbstractPersistable {

    private LocalDate scheduleFirstUTCDate;
    private LocalDate scheduleLastUTCDate;

    @ProblemFactProperty
    private FlightCrewParametrization parametrization;

    @ProblemFactCollectionProperty
    private List<Skill> skillList;

    @ProblemFactCollectionProperty
    private List<Airport> airportList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "employeeRange")
    private List<Employee> employeeList;

    @ProblemFactCollectionProperty
    private List<Flight> flightList;

    @PlanningEntityCollectionProperty
    private List<FlightAssignment> flightAssignmentList;

    @PlanningScore
    private HardSoftLongScore score = null;

    public FlightCrewSolution() {
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public LocalDate getScheduleFirstUTCDate() {
        return scheduleFirstUTCDate;
    }

    public void setScheduleFirstUTCDate(LocalDate scheduleFirstUTCDate) {
        this.scheduleFirstUTCDate = scheduleFirstUTCDate;
    }

    public LocalDate getScheduleLastUTCDate() {
        return scheduleLastUTCDate;
    }

    public void setScheduleLastUTCDate(LocalDate scheduleLastUTCDate) {
        this.scheduleLastUTCDate = scheduleLastUTCDate;
    }

    public FlightCrewParametrization getParametrization() {
        return parametrization;
    }

    public void setParametrization(FlightCrewParametrization parametrization) {
        this.parametrization = parametrization;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public List<Airport> getAirportList() {
        return airportList;
    }

    public void setAirportList(List<Airport> airportList) {
        this.airportList = airportList;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }

    public List<FlightAssignment> getFlightAssignmentList() {
        return flightAssignmentList;
    }

    public void setFlightAssignmentList(List<FlightAssignment> flightAssignmentList) {
        this.flightAssignmentList = flightAssignmentList;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

}
