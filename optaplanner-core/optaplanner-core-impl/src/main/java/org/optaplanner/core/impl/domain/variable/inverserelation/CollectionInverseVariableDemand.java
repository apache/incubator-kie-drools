package org.optaplanner.core.impl.domain.variable.inverserelation;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.AbstractVariableDescriptorBasedDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * To get an instance, demand a {@link CollectionInverseVariableDemand} from {@link InnerScoreDirector#getSupplyManager()}.
 */
public final class CollectionInverseVariableDemand<Solution_>
        extends AbstractVariableDescriptorBasedDemand<Solution_, CollectionInverseVariableSupply> {

    public CollectionInverseVariableDemand(VariableDescriptor<Solution_> sourceVariableDescriptor) {
        super(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public CollectionInverseVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedCollectionInverseVariableSupply<>(variableDescriptor);
    }

}
