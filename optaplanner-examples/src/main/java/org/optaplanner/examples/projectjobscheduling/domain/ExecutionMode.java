package org.optaplanner.examples.projectjobscheduling.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ExecutionMode extends AbstractPersistable {

    private Job job;
    private int duration; // In days

    public ExecutionMode() {
    }

    public ExecutionMode(long id, Job job) {
        super(id);
        this.job = job;
    }

    private List<ResourceRequirement> resourceRequirementList;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<ResourceRequirement> getResourceRequirementList() {
        return resourceRequirementList;
    }

    public void setResourceRequirementList(List<ResourceRequirement> resourceRequirementList) {
        this.resourceRequirementList = resourceRequirementList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
