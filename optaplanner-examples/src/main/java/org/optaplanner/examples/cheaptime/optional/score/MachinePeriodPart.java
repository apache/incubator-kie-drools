package org.optaplanner.examples.cheaptime.optional.score;

import static java.util.Comparator.comparing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.MachineCapacity;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;

public final class MachinePeriodPart implements Comparable<MachinePeriodPart> {

    private static final Comparator<MachinePeriodPart> COMPARATOR = comparing(
            (MachinePeriodPart machinePeriodPart) -> machinePeriodPart.machine.getIndex())
                    .thenComparingInt(MachinePeriodPart::getPeriod)
                    .thenComparing(MachinePeriodPart::isActive)
                    .thenComparingInt(machinePeriodPart -> machinePeriodPart.resourceAvailableList.length);

    private final Machine machine;
    private final int period;

    private boolean active;
    private int[] resourceAvailableList;
    private int resourceInShortTotal;

    public MachinePeriodPart(Machine machine, int period, List<TaskAssignment> taskAssignmentList) {
        this.machine = machine;
        this.period = period;

        active = false;

        resourceAvailableList = machine.getMachineCapacityList()
                .stream()
                .mapToInt(MachineCapacity::getCapacity)
                .toArray();

        for (TaskAssignment taskAssignment : taskAssignmentList) {
            addTaskAssignment(taskAssignment);
        }
    }

    public void addTaskAssignment(TaskAssignment taskAssignment) {
        active = true;
        Task task = taskAssignment.getTask();
        for (int i = 0; i < resourceAvailableList.length; i++) {
            TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
            int currentResourceAvailable = resourceAvailableList[i];
            if (currentResourceAvailable < 0) {
                resourceInShortTotal -= currentResourceAvailable;
            }
            resourceAvailableList[i] -= taskRequirement.getResourceUsage();
            int resourceAvailable = resourceAvailableList[i];
            if (resourceAvailable < 0) {
                resourceInShortTotal += resourceAvailable;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int[] getResourceAvailableList() {
        return resourceAvailableList;
    }

    public void setResourceAvailableList(int[] resourceAvailableList) {
        this.resourceAvailableList = resourceAvailableList;
    }

    public int getResourceInShortTotal() {
        return resourceInShortTotal;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MachinePeriodPart other = (MachinePeriodPart) o;
        return Objects.equals(machine, other.machine) &&
                period == other.period &&
                active == other.active &&
                Arrays.equals(resourceAvailableList, other.resourceAvailableList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machine, period, active, Arrays.hashCode(resourceAvailableList));
    }

    @Override
    public int compareTo(MachinePeriodPart other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return machine + ", period = " + period + ", active = " + active
                + ", resourceAvailableList = " + Arrays.toString(resourceAvailableList);
    }
}
