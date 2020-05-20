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

package org.optaplanner.examples.projectjobscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;
import org.optaplanner.persistence.xstream.api.score.buildin.bendable.BendableScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("PjsSchedule")
public class Schedule extends AbstractPersistable {

    private List<Project> projectList;
    private List<Job> jobList;
    private List<ExecutionMode> executionModeList;
    private List<Resource> resourceList;
    private List<ResourceRequirement> resourceRequirementList;

    private List<Allocation> allocationList;

    @XStreamConverter(BendableScoreXStreamConverter.class)
    private BendableScore score;

    @ProblemFactCollectionProperty
    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @ProblemFactCollectionProperty
    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    @ProblemFactCollectionProperty
    public List<ExecutionMode> getExecutionModeList() {
        return executionModeList;
    }

    public void setExecutionModeList(List<ExecutionMode> executionModeList) {
        this.executionModeList = executionModeList;
    }

    @ProblemFactCollectionProperty
    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    @ProblemFactCollectionProperty
    public List<ResourceRequirement> getResourceRequirementList() {
        return resourceRequirementList;
    }

    public void setResourceRequirementList(List<ResourceRequirement> resourceRequirementList) {
        this.resourceRequirementList = resourceRequirementList;
    }

    @PlanningEntityCollectionProperty
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    @PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 2)
    public BendableScore getScore() {
        return score;
    }

    public void setScore(BendableScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
