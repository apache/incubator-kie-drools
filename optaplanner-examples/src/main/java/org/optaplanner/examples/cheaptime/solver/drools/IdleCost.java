package org.optaplanner.examples.cheaptime.solver.drools;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.cheaptime.domain.Machine;

public class IdleCost {

    private final Machine machine;
    private final int activePeriodAfterIdle;
    private final long cost;

    public IdleCost(Machine machine, int activePeriodAfterIdle, long cost) {
        this.machine = machine;
        this.activePeriodAfterIdle = activePeriodAfterIdle;
        this.cost = cost;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getActivePeriodAfterIdle() {
        return activePeriodAfterIdle;
    }

    public long getCost() {
        return cost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof IdleCost) {
            IdleCost other = (IdleCost) o;
            return new EqualsBuilder()
                    .append(machine, other.machine)
                    .append(activePeriodAfterIdle, other.activePeriodAfterIdle)
                    .append(cost, other.cost)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(machine)
                .append(activePeriodAfterIdle)
                .append(cost)
                .toHashCode();
    }

    public int compareTo(IdleCost other) {
        return new CompareToBuilder()
                .append(machine, other.machine)
                .append(activePeriodAfterIdle, other.activePeriodAfterIdle)
                .append(cost, other.cost)
                .toComparison();
    }

    @Override
    public String toString() {
        return "machine = " + machine + ", activePeriodAfterIdle = " + activePeriodAfterIdle + ", cost = " + cost;
    }

}
