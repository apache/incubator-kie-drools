package org.optaplanner.core.impl.domain.variable.anchor;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.AbstractVariableDescriptorBasedDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

public final class AnchorVariableDemand<Solution_>
        extends AbstractVariableDescriptorBasedDemand<Solution_, AnchorVariableSupply> {

    public AnchorVariableDemand(VariableDescriptor<Solution_> sourceVariableDescriptor) {
        super(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public AnchorVariableSupply createExternalizedSupply(SupplyManager supplyManager) {
        SingletonInverseVariableSupply inverseVariableSupply = supplyManager
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        return new ExternalizedAnchorVariableSupply<>(variableDescriptor, inverseVariableSupply);
    }

}
