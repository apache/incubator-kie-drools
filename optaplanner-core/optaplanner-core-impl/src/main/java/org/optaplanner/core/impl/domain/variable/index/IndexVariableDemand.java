package org.optaplanner.core.impl.domain.variable.index;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.AbstractVariableDescriptorBasedDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public final class IndexVariableDemand<Solution_>
        extends AbstractVariableDescriptorBasedDemand<Solution_, IndexVariableSupply> {

    public IndexVariableDemand(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        super(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public IndexVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        return new ExternalizedIndexVariableSupply<>((ListVariableDescriptor<Solution_>) variableDescriptor);
    }

}
