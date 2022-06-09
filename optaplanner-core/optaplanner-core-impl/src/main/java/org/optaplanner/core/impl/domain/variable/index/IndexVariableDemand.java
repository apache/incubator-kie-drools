package org.optaplanner.core.impl.domain.variable.index;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class IndexVariableDemand<Solution_> implements Demand<IndexVariableSupply> {

    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public IndexVariableDemand(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public IndexVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedIndexVariableSupply<>(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndexVariableDemand)) {
            return false;
        }
        IndexVariableDemand<Solution_> other = (IndexVariableDemand<Solution_>) o;
        return sourceVariableDescriptor.equals(other.sourceVariableDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IndexVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
