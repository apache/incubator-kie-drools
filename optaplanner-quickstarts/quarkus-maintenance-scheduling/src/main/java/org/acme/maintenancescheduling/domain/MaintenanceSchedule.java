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

package org.acme.maintenancescheduling.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

import java.util.List;

@PlanningSolution
public class MaintenanceSchedule {

    @ConstraintConfigurationProvider
    private MaintenanceSchedulingConstraintConfiguration constraintConfiguration =
            new MaintenanceSchedulingConstraintConfiguration();

    @ProblemFactCollectionProperty
    private List<MaintainableUnit> maintainableUnitList;

    @ProblemFactCollectionProperty
    private List<MutuallyExclusiveJobs> mutuallyExclusiveJobsList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "assignedCrewRange")
    private List<MaintenanceCrew> assignedCrewList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeGrainRange")
    private List<TimeGrain> timeGrainList;

    @PlanningEntityCollectionProperty
    private List<MaintenanceJob> maintenanceJobList;

    @PlanningScore
    private HardSoftScore score;

    // Ignored by OptaPlanner, used by the UI to display solve or stop solving button
    private SolverStatus solverStatus;

    public MaintenanceSchedule() {
    }

    public MaintenanceSchedule(List<MaintainableUnit> maintainableUnitList,
            List<MutuallyExclusiveJobs> mutuallyExclusiveJobsList, List<MaintenanceCrew> assignedCrewList,
            List<TimeGrain> timeGrainList, List<MaintenanceJob> maintenanceJobList) {
        this.maintainableUnitList = maintainableUnitList;
        this.mutuallyExclusiveJobsList = mutuallyExclusiveJobsList;
        this.assignedCrewList = assignedCrewList;
        this.timeGrainList = timeGrainList;
        this.maintenanceJobList = maintenanceJobList;
    }

    public MaintenanceSchedulingConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(MaintenanceSchedulingConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    public List<MaintainableUnit> getMaintainableUnitList() {
        return maintainableUnitList;
    }

    public void setMaintainableUnitList(List<MaintainableUnit> maintainableUnitList) {
        this.maintainableUnitList = maintainableUnitList;
    }

    public List<MutuallyExclusiveJobs> getMutuallyExclusiveJobsList() {
        return mutuallyExclusiveJobsList;
    }

    public void setMutuallyExclusiveJobsList(List<MutuallyExclusiveJobs> mutuallyExclusiveJobsList) {
        this.mutuallyExclusiveJobsList = mutuallyExclusiveJobsList;
    }

    public List<MaintenanceCrew> getAssignedCrewList() {
        return assignedCrewList;
    }

    public void setAssignedCrewList(List<MaintenanceCrew> assignedCrewList) {
        this.assignedCrewList = assignedCrewList;
    }

    public List<TimeGrain> getTimeGrainList() {
        return timeGrainList;
    }

    public void setTimeGrainList(List<TimeGrain> timeGrainList) {
        this.timeGrainList = timeGrainList;
    }

    public List<MaintenanceJob> getMaintenanceJobList() {
        return maintenanceJobList;
    }

    public void setMaintenanceJobList(List<MaintenanceJob> maintenanceJobList) {
        this.maintenanceJobList = maintenanceJobList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }
}
