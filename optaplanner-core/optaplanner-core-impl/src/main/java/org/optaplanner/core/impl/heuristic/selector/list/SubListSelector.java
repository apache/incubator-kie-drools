package org.optaplanner.core.impl.heuristic.selector.list;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;

public interface SubListSelector<Solution_> extends IterableSelector<Solution_, SubList> {

    ListVariableDescriptor<Solution_> getVariableDescriptor();

    Iterator<Object> endingValueIterator();

    long getValueCount();
}
