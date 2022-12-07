package org.optaplanner.examples.machinereassignment.optional.score;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MrMachineTransientUsage implements Comparable<MrMachineTransientUsage> {

    private static final Comparator<MrMachineTransientUsage> COMPARATOR = comparing(
            (MrMachineTransientUsage transientUsage) -> transientUsage.getClass().getName())
            .thenComparing(transientUsage -> transientUsage.machineCapacity, comparingLong(MrMachineCapacity::getId))
            .thenComparingLong(transientUsage -> transientUsage.usage);

    private MrMachineCapacity machineCapacity;
    private long usage;

    public MrMachineTransientUsage(MrMachineCapacity machineCapacity, long usage) {
        this.machineCapacity = machineCapacity;
        this.usage = usage;
    }

    public MrMachineCapacity getMachineCapacity() {
        return machineCapacity;
    }

    public long getUsage() {
        return usage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MrMachineTransientUsage other = (MrMachineTransientUsage) o;
        return Objects.equals(machineCapacity, other.machineCapacity) &&
                usage == other.usage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineCapacity, usage);
    }

    public MrMachine getMachine() {
        return machineCapacity.getMachine();
    }

    public MrResource getResource() {
        return machineCapacity.getResource();
    }

    @Override
    public String toString() {
        return getMachine() + "-" + getResource() + "=" + usage;
    }

    @Override
    public int compareTo(MrMachineTransientUsage o) {
        return COMPARATOR.compare(this, o);
    }
}
