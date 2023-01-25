package org.optaplanner.core.impl.domain.variable.inverserelation;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.AbstractVariableDescriptorBasedDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public final class SingletonListInverseVariableDemand<Solution_>
        extends AbstractVariableDescriptorBasedDemand<Solution_, SingletonInverseVariableSupply> {

    public SingletonListInverseVariableDemand(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        super(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public SingletonInverseVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedSingletonListInverseVariableSupply<>((ListVariableDescriptor<Solution_>) variableDescriptor);
    }

}
