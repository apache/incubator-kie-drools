package org.optaplanner.examples.cheaptime.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.cheaptime.domain.solver.TaskAssignmentDifficultyComparator;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyComparatorClass = TaskAssignmentDifficultyComparator.class)
@XStreamAlias("CtTaskAssignment")
public class TaskAssignment extends AbstractPersistable {

    private Task task;

    // Planning variables: changes during planning, between score calculations.
    private Machine machine;
    private Integer startPeriod;

    public TaskAssignment() {

    }

    public TaskAssignment(Task task, Machine machine, Period start) {
        super(task.getId());
        this.task = task;
        this.machine = machine;
        this.startPeriod = start.getIndex();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @PlanningVariable(valueRangeProviderRefs = { "machineRange" })
    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @PlanningVariable(valueRangeProviderRefs = { "startPeriodRange" })
    public Integer getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * The startPeriod is included and the endPeriod is excluded.
     *
     * @return null if {@link #getStartPeriod()} is null
     */
    public Integer getEndPeriod() {
        if (startPeriod == null) {
            return null;
        }
        return startPeriod + task.getDuration();
    }

    public String getLabel() {
        return task.getLabel();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    @ValueRangeProvider(id = "startPeriodRange")
    public CountableValueRange<Integer> getStartPeriodRange() {
        return ValueRangeFactory.createIntValueRange(task.getStartPeriodRangeFrom(), task.getStartPeriodRangeTo());
    }

}
