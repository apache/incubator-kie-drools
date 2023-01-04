package org.optaplanner.examples.projectjobscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.projectjobscheduling.domain.solver.DelayStrengthComparator;
import org.optaplanner.examples.projectjobscheduling.domain.solver.ExecutionModeStrengthWeightFactory;
import org.optaplanner.examples.projectjobscheduling.domain.solver.NotSourceOrSinkAllocationFilter;
import org.optaplanner.examples.projectjobscheduling.domain.solver.PredecessorsDoneDateUpdatingVariableListener;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity(pinningFilter = NotSourceOrSinkAllocationFilter.class)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Allocation extends AbstractPersistable implements Labeled {

    private Job job;

    private Allocation sourceAllocation;
    private Allocation sinkAllocation;
    private List<Allocation> predecessorAllocationList;
    private List<Allocation> successorAllocationList;

    // Planning variables: changes during planning, between score calculations.
    private ExecutionMode executionMode;
    private Integer delay; // In days

    // Shadow variables
    private Integer predecessorsDoneDate;

    public Allocation() {
    }

    public Allocation(long id, Job job) {
        super(id);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Allocation getSourceAllocation() {
        return sourceAllocation;
    }

    public void setSourceAllocation(Allocation sourceAllocation) {
        this.sourceAllocation = sourceAllocation;
    }

    public Allocation getSinkAllocation() {
        return sinkAllocation;
    }

    public void setSinkAllocation(Allocation sinkAllocation) {
        this.sinkAllocation = sinkAllocation;
    }

    public List<Allocation> getPredecessorAllocationList() {
        return predecessorAllocationList;
    }

    public void setPredecessorAllocationList(List<Allocation> predecessorAllocationList) {
        this.predecessorAllocationList = predecessorAllocationList;
    }

    public List<Allocation> getSuccessorAllocationList() {
        return successorAllocationList;
    }

    public void setSuccessorAllocationList(List<Allocation> successorAllocationList) {
        this.successorAllocationList = successorAllocationList;
    }

    @PlanningVariable(strengthWeightFactoryClass = ExecutionModeStrengthWeightFactory.class)
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    @PlanningVariable(strengthComparatorClass = DelayStrengthComparator.class)
    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @ShadowVariable(
            variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class, sourceVariableName = "executionMode")
    @ShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class, sourceVariableName = "delay")
    public Integer getPredecessorsDoneDate() {
        return predecessorsDoneDate;
    }

    public void setPredecessorsDoneDate(Integer predecessorsDoneDate) {
        this.predecessorsDoneDate = predecessorsDoneDate;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public Integer getStartDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay);
    }

    @JsonIgnore
    public Integer getEndDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay)
                + (executionMode == null ? 0 : executionMode.getDuration());
    }

    @JsonIgnore
    public Project getProject() {
        return job.getProject();
    }

    @JsonIgnore
    public int getProjectCriticalPathEndDate() {
        return job.getProject().getCriticalPathEndDate();
    }

    @JsonIgnore
    public JobType getJobType() {
        return job.getJobType();
    }

    @Override
    public String getLabel() {
        return "Job " + job.getId();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    @ValueRangeProvider
    @JsonIgnore
    public List<ExecutionMode> getExecutionModeRange() {
        return job.getExecutionModeList();
    }

    @ValueRangeProvider
    @JsonIgnore
    public CountableValueRange<Integer> getDelayRange() {
        return ValueRangeFactory.createIntValueRange(0, 500);
    }

}
