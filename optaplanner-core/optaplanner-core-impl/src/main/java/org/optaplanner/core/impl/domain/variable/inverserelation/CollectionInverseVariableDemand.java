package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * To get an instance, demand a {@link CollectionInverseVariableDemand} from {@link InnerScoreDirector#getSupplyManager()}.
 */
public class CollectionInverseVariableDemand<Solution_> implements Demand<CollectionInverseVariableSupply> {

    protected final VariableDescriptor<Solution_> sourceVariableDescriptor;

    public CollectionInverseVariableDemand(VariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public CollectionInverseVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedCollectionInverseVariableSupply<>(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionInverseVariableDemand)) {
            return false;
        }
        CollectionInverseVariableDemand<Solution_> other = (CollectionInverseVariableDemand<Solution_>) o;
        if (!sourceVariableDescriptor.equals(other.sourceVariableDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(CollectionInverseVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
