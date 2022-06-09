package org.optaplanner.examples.cheaptime.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtTask")
public class Task extends AbstractPersistable {

    private long powerConsumptionMicros;
    private int duration;
    private int startPeriodRangeFrom; // Inclusive
    private int startPeriodRangeTo; // Exclusive

    // Order is equal to resourceList so Resource.getIndex() can be used for the index
    private List<TaskRequirement> taskRequirementList;

    public Task() {

    }

    public Task(long id, Period startInclusive, Period endExclusive, int duration, long powerConsumptionMicros,
            TaskRequirement... taskRequirements) {
        super(id);
        this.startPeriodRangeFrom = startInclusive.getIndex();
        this.startPeriodRangeTo = endExclusive.getIndex();
        this.duration = duration;
        this.powerConsumptionMicros = powerConsumptionMicros;
        this.taskRequirementList = Arrays.stream(taskRequirements)
                .collect(Collectors.toList());
    }

    public long getPowerConsumptionMicros() {
        return powerConsumptionMicros;
    }

    public void setPowerConsumptionMicros(long powerConsumptionMicros) {
        this.powerConsumptionMicros = powerConsumptionMicros;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStartPeriodRangeFrom() {
        return startPeriodRangeFrom;
    }

    public void setStartPeriodRangeFrom(int startPeriodRangeFrom) {
        this.startPeriodRangeFrom = startPeriodRangeFrom;
    }

    public int getStartPeriodRangeTo() {
        return startPeriodRangeTo;
    }

    public void setStartPeriodRangeTo(int startPeriodRangeTo) {
        this.startPeriodRangeTo = startPeriodRangeTo;
    }

    public List<TaskRequirement> getTaskRequirementList() {
        return taskRequirementList;
    }

    public void setTaskRequirementList(List<TaskRequirement> taskRequirementList) {
        this.taskRequirementList = taskRequirementList;
    }

    public int getUsage(Resource resource) {
        return taskRequirementList.get(resource.getIndex()).getResourceUsage();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getResourceUsageMultiplicand() {
        int multiplicand = 1;
        for (TaskRequirement taskRequirement : taskRequirementList) {
            multiplicand *= taskRequirement.getResourceUsage();
        }
        return multiplicand;
    }

    public String getLabel() {
        return "Task " + id;
    }

}
