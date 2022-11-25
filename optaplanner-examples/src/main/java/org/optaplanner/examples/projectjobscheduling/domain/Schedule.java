package org.optaplanner.examples.projectjobscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

@PlanningSolution
public class Schedule extends AbstractPersistable {

    private List<Project> projectList;
    private List<Job> jobList;
    private List<ExecutionMode> executionModeList;
    private List<Resource> resourceList;
    private List<ResourceRequirement> resourceRequirementList;

    private List<Allocation> allocationList;

    private HardMediumSoftScore score;

    public Schedule() {
    }

    public Schedule(long id) {
        super(id);
    }

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

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
