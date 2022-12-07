package org.optaplanner.examples.machinereassignment.optional.score;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MrMachineUsage implements Comparable<MrMachineUsage> {

    private static final Comparator<MrMachineUsage> COMPARATOR = comparing(
            (MrMachineUsage machineUsage) -> machineUsage.getClass().getName())
            .thenComparing(machineUsage -> machineUsage.machineCapacity, comparingLong(MrMachineCapacity::getId))
            .thenComparingLong(machineUsage -> machineUsage.usage);

    private MrMachineCapacity machineCapacity;
    private long usage;

    public MrMachineUsage(MrMachineCapacity machineCapacity, long usage) {
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
        final MrMachineUsage other = (MrMachineUsage) o;
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

    public boolean isTransientlyConsumed() {
        return machineCapacity.getResource().isTransientlyConsumed();
    }

    public long getLoadCostWeight() {
        return machineCapacity.getResource().getLoadCostWeight();
    }

    public long getMaximumAvailable() {
        return machineCapacity.getMaximumCapacity() - usage;
    }

    public long getSafetyAvailable() {
        return machineCapacity.getSafetyCapacity() - usage;
    }

    @Override
    public String toString() {
        return getMachine() + "-" + getResource() + "=" + usage;
    }

    @Override
    public int compareTo(MrMachineUsage o) {
        return COMPARATOR.compare(this, o);
    }
}
