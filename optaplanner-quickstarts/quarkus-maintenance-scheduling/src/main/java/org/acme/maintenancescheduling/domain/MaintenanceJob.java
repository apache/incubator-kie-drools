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

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@PlanningEntity
public class MaintenanceJob extends PanacheEntityBase {

    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotBlank
    private String jobName;
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private MaintainableUnit maintainableUnit;
    @NotNull
    private int readyGrainIndex;
    @NotNull
    private int deadlineGrainIndex;
    @NotNull
    private int durationInGrains;

    // TODO: Add configuration option for how long each TimeGrain is
    @PlanningVariable(valueRangeProviderRefs = "timeGrainRange")
    @ManyToOne
    private TimeGrain startingTimeGrain;

    @PlanningVariable(valueRangeProviderRefs = "assignedCrewRange")
    @ManyToOne
    private MaintenanceCrew assignedCrew;

    // TODO: Make it an enum? Range of priorities (not in MVP, but make it easy for users to do)
    @NotNull
    private boolean critical;

    // TODO: Add safetyMargin

    public MaintenanceJob() {
    }

    public MaintenanceJob(String jobName, MaintainableUnit maintainableUnit, int readyGrainIndex,
                          int deadlineGrainIndex, int durationInGrains, boolean critical) {
        this.jobName = jobName;
        this.maintainableUnit = maintainableUnit;
        this.readyGrainIndex = readyGrainIndex;
        this.deadlineGrainIndex = deadlineGrainIndex;
        this.durationInGrains = durationInGrains;
        this.critical = critical;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public MaintainableUnit getMaintainableUnit() {
        return maintainableUnit;
    }

    public void setMaintainableUnit(MaintainableUnit maintainableUnit) {
        this.maintainableUnit = maintainableUnit;
    }

    public int getReadyGrainIndex() {
        return readyGrainIndex;
    }

    public void setReadyGrainIndex(int readyGrainIndex) {
        this.readyGrainIndex = readyGrainIndex;
    }

    public int getDeadlineGrainIndex() {
        return deadlineGrainIndex;
    }

    public void setDeadlineGrainIndex(int deadlineGrainIndex) {
        this.deadlineGrainIndex = deadlineGrainIndex;
    }

    public int getDurationInGrains() {
        return durationInGrains;
    }

    public void setDurationInGrains(int durationInGrains) {
        this.durationInGrains = durationInGrains;
    }

    public TimeGrain getStartingTimeGrain() {
        return startingTimeGrain;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain) {
        this.startingTimeGrain = startingTimeGrain;
    }

    public MaintenanceCrew getAssignedCrew() {
        return assignedCrew;
    }

    public void setAssignedCrew(MaintenanceCrew assignedCrew) {
        this.assignedCrew = assignedCrew;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int calculateOverlap(MaintenanceJob other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return 0;
        }
        int start = startingTimeGrain.getGrainIndex();
        int end = start + durationInGrains;
        int otherStart = other.getStartingTimeGrain().getGrainIndex();
        int otherEnd = otherStart + other.getDurationInGrains();

        if (end < otherStart) {
            return 0;
        } else if (otherEnd < start) {
            return 0;
        }
        return Math.min(end, otherEnd) - Math.max(start, otherStart);
    }

    @Override
    public String toString() {
        return "MaintenanceJob{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", maintainableUnit=" + maintainableUnit +
                ", readyGrainIndex=" + readyGrainIndex +
                ", deadlineGrainIndex=" + deadlineGrainIndex +
                ", durationInGrains=" + durationInGrains +
                ", isCritical=" + critical +
                '}';
    }
}
