package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public final class SelectedCountLimitEntitySelector<Solution_> extends AbstractEntitySelector<Solution_> {

    private final EntitySelector<Solution_> childEntitySelector;
    private final boolean randomSelection;
    private final long selectedCountLimit;

    public SelectedCountLimitEntitySelector(EntitySelector<Solution_> childEntitySelector, boolean randomSelection,
            long selectedCountLimit) {
        this.childEntitySelector = childEntitySelector;
        this.randomSelection = randomSelection;
        this.selectedCountLimit = selectedCountLimit;
        if (selectedCountLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedCountLimit (" + selectedCountLimit + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
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
    public long getSize() {
        long childSize = childEntitySelector.getSize();
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public Iterator<Object> iterator() {
        return new SelectedCountLimitEntityIterator(childEntitySelector.iterator());
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Object> endingIterator() {
        if (randomSelection) {
            // With random selection, the first n elements can differ between iterator calls,
            // so it's illegal to only return the first n elements in original order (that breaks NearbySelection)
            return childEntitySelector.endingIterator();
        } else {
            return new SelectedCountLimitEntityIterator(childEntitySelector.endingIterator());
        }
    }

    private class SelectedCountLimitEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;
        private long selectedSize;

        public SelectedCountLimitEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedCountLimit && childEntityIterator.hasNext();
        }

        @Override
        public Object next() {
            if (selectedSize >= selectedCountLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childEntityIterator.next();
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        SelectedCountLimitEntitySelector<?> that = (SelectedCountLimitEntitySelector<?>) other;
        return randomSelection == that.randomSelection && selectedCountLimit == that.selectedCountLimit
                && Objects.equals(childEntitySelector, that.childEntitySelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childEntitySelector, randomSelection, selectedCountLimit);
    }

    @Override
    public String toString() {
        return "SelectedCountLimit(" + childEntitySelector + ")";
    }

}
