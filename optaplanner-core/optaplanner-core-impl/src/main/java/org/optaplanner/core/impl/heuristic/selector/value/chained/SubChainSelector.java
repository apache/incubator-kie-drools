package org.optaplanner.core.impl.heuristic.selector.value.chained;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;

public interface SubChainSelector<Solution_> extends ListIterableSelector<Solution_, SubChain> {

    /**
     * @return never null
     */
    GenuineVariableDescriptor<Solution_> getVariableDescriptor();

}
