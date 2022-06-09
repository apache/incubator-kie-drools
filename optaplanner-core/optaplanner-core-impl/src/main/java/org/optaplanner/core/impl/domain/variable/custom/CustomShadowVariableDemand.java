package org.optaplanner.core.impl.domain.variable.custom;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

/**
 * Unlike other {@link Demand}s, a custom demand isn't equalized based on its sources, but based on its target.
 * Therefore a custom shadow variable cannot be reused by built-in systems.
 */
public class CustomShadowVariableDemand<Solution_> implements Demand<CustomShadowVariableDemand.NoSupply> {

    private final CustomShadowVariableDescriptor<Solution_> targetShadowVariableDescriptor;

    public CustomShadowVariableDemand(CustomShadowVariableDescriptor<Solution_> targetShadowVariableDescriptor) {
        this.targetShadowVariableDescriptor = targetShadowVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public NoSupply createExternalizedSupply(SupplyManager supplyManager) {
        throw new IllegalArgumentException("A custom shadow variable cannot be externalized.");
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomShadowVariableDemand)) {
            return false;
        }
        CustomShadowVariableDemand<Solution_> other = (CustomShadowVariableDemand<Solution_>) o;
        return targetShadowVariableDescriptor == other.targetShadowVariableDescriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(CustomShadowVariableDemand.class.getName(), targetShadowVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public interface NoSupply extends Supply {
    }
}
