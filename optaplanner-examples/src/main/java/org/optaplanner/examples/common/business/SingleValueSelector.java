package org.optaplanner.examples.common.business;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;

final class SingleValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    private final GenuineVariableDescriptor<Solution_> variableDescriptor;
    private final List<Object> objectList;

    public SingleValueSelector(GenuineVariableDescriptor<Solution_> variableDescriptor, Object value) {
        this.variableDescriptor = variableDescriptor;
        this.objectList = Collections.singletonList(value);
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return variableDescriptor;
    }

    @Override
    public long getSize(Object entity) {
        return 1;
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return objectList.iterator();
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return iterator(entity);
    }
}
