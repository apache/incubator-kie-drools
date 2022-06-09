package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class SingletonListInverseVariableDemand<Solution_> implements Demand<SingletonInverseVariableSupply> {

    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public SingletonListInverseVariableDemand(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public SingletonInverseVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedSingletonListInverseVariableSupply<>(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingletonListInverseVariableDemand)) {
            return false;
        }
        SingletonListInverseVariableDemand<Solution_> other = (SingletonListInverseVariableDemand<Solution_>) o;
        return sourceVariableDescriptor.equals(other.sourceVariableDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SingletonListInverseVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
