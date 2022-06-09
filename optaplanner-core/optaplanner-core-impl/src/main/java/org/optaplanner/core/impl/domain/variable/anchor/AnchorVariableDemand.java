package org.optaplanner.core.impl.domain.variable.anchor;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public class AnchorVariableDemand<Solution_> implements Demand<AnchorVariableSupply> {

    protected final VariableDescriptor<Solution_> sourceVariableDescriptor;

    public AnchorVariableDemand(VariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public AnchorVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        SingletonInverseVariableSupply inverseVariableSupply = supplyManager
                .demand(new SingletonInverseVariableDemand<>(sourceVariableDescriptor));
        return new ExternalizedAnchorVariableSupply<>(sourceVariableDescriptor, inverseVariableSupply);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnchorVariableDemand)) {
            return false;
        }
        AnchorVariableDemand<Solution_> other = (AnchorVariableDemand<Solution_>) o;
        if (!sourceVariableDescriptor.equals(other.sourceVariableDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(AnchorVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
