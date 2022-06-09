package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public class SelectedCountLimitValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    protected final ValueSelector<Solution_> childValueSelector;
    protected final long selectedCountLimit;

    /**
     * Unlike most of the other {@link ValueSelector} decorations,
     * this one works for an entity dependent {@link ValueSelector} too.
     *
     * @param childValueSelector never null, if any of the {@link EntityIndependentValueSelector} specific methods
     *        are going to be used, this parameter must also implement that interface
     * @param selectedCountLimit at least 0
     */
    public SelectedCountLimitValueSelector(ValueSelector<Solution_> childValueSelector, long selectedCountLimit) {
        this.childValueSelector = childValueSelector;
        this.selectedCountLimit = selectedCountLimit;
        if (selectedCountLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedCountLimit (" + selectedCountLimit + ").");
        }
        phaseLifecycleSupport.addEventListener(childValueSelector);
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
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public long getSize(Object entity) {
        long childSize = childValueSelector.getSize(entity);
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public long getSize() {
        if (!(childValueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("To use the method getSize(), the moveSelector (" + this
                    + ") needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + childValueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        long childSize = ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize();
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return new SelectedCountLimitValueIterator(childValueSelector.iterator(entity));
    }

    @Override
    public Iterator<Object> iterator() {
        return new SelectedCountLimitValueIterator(((EntityIndependentValueSelector<Solution_>) childValueSelector).iterator());
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return new SelectedCountLimitValueIterator(childValueSelector.endingIterator(entity));
    }

    private class SelectedCountLimitValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childValueIterator;
        private long selectedSize;

        public SelectedCountLimitValueIterator(Iterator<Object> childValueIterator) {
            this.childValueIterator = childValueIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedCountLimit && childValueIterator.hasNext();
        }

        @Override
        public Object next() {
            if (selectedSize >= selectedCountLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childValueIterator.next();
        }

    }

    @Override
    public String toString() {
        return "SelectedCountLimit(" + childValueSelector + ")";
    }

}
