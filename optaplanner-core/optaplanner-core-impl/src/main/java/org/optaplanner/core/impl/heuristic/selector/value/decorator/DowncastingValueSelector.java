package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Collections;
import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public class DowncastingValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    protected final ValueSelector<Solution_> childValueSelector;
    protected final Class<?> downcastEntityClass;

    public DowncastingValueSelector(ValueSelector<Solution_> childValueSelector, Class<?> downcastEntityClass) {
        this.childValueSelector = childValueSelector;
        this.downcastEntityClass = downcastEntityClass;
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    public ValueSelector<Solution_> getChildValueSelector() {
        return childValueSelector;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childValueSelector.isNeverEnding();
    }

    @Override
    public long getSize(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return 0L;
        }
        return childValueSelector.getSize(entity);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return Collections.emptyIterator();
        }
        return childValueSelector.iterator(entity);
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return Collections.emptyIterator();
        }
        return childValueSelector.endingIterator(entity);
    }

    @Override
    public String toString() {
        return "Downcasting(" + childValueSelector + ")";
    }

}
