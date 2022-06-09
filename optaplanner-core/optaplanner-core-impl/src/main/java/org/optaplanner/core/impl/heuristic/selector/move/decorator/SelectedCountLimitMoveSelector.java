package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SelectedCountLimitMoveSelector<Solution_> extends AbstractMoveSelector<Solution_> {

    protected final MoveSelector<Solution_> childMoveSelector;
    protected final long selectedCountLimit;

    public SelectedCountLimitMoveSelector(MoveSelector<Solution_> childMoveSelector, long selectedCountLimit) {
        this.childMoveSelector = childMoveSelector;
        this.selectedCountLimit = selectedCountLimit;
        if (selectedCountLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedCountLimit (" + selectedCountLimit + ").");
        }
        phaseLifecycleSupport.addEventListener(childMoveSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

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
        long childSize = childMoveSelector.getSize();
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new SelectedCountLimitMoveIterator(childMoveSelector.iterator());
    }

    private class SelectedCountLimitMoveIterator extends SelectionIterator<Move<Solution_>> {

        private final Iterator<Move<Solution_>> childMoveIterator;
        private long selectedSize;

        public SelectedCountLimitMoveIterator(Iterator<Move<Solution_>> childMoveIterator) {
            this.childMoveIterator = childMoveIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedCountLimit && childMoveIterator.hasNext();
        }

        @Override
        public Move<Solution_> next() {
            if (selectedSize >= selectedCountLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childMoveIterator.next();
        }

    }

    @Override
    public String toString() {
        return "SelectedCountLimit(" + childMoveSelector + ")";
    }

}
