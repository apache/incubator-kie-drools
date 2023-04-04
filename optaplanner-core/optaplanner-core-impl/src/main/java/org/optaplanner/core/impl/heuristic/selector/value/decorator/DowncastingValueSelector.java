package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public final class DowncastingValueSelector<Solution_>
        extends AbstractDemandEnabledSelector<Solution_>
        implements ValueSelector<Solution_> {

    private final ValueSelector<Solution_> childValueSelector;
    private final Class<?> downcastEntityClass;

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
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        DowncastingValueSelector<?> that = (DowncastingValueSelector<?>) other;
        return Objects.equals(childValueSelector, that.childValueSelector)
                && Objects.equals(downcastEntityClass, that.downcastEntityClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector, downcastEntityClass);
    }

    @Override
    public String toString() {
        return "Downcasting(" + childValueSelector + ")";
    }

}
