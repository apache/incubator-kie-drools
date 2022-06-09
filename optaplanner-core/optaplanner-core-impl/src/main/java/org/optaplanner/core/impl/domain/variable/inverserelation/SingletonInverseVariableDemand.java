package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class SingletonInverseVariableDemand<Solution_> implements Demand<SingletonInverseVariableSupply> {

    protected final VariableDescriptor<Solution_> sourceVariableDescriptor;

    public SingletonInverseVariableDemand(VariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public SingletonInverseVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedSingletonInverseVariableSupply<>(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingletonInverseVariableDemand)) {
            return false;
        }
        SingletonInverseVariableDemand<Solution_> other = (SingletonInverseVariableDemand<Solution_>) o;
        if (!sourceVariableDescriptor.equals(other.sourceVariableDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(SingletonInverseVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
