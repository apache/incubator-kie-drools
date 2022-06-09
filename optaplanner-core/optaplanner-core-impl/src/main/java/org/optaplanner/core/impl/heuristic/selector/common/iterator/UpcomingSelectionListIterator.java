package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class UpcomingSelectionListIterator<S> extends UpcomingSelectionIterator<S>
        implements ListIterator<S> {

    private int nextListIteratorIndex = 0;

    protected S previousSelection;
    protected boolean previousCreated = false;
    protected boolean hasPreviousSelection = false;

    protected S noPreviousSelection() {
        hasPreviousSelection = false;
        return null;
    }

    protected abstract S createUpcomingSelection();

    protected abstract S createPreviousSelection();

    @Override
    public boolean hasPrevious() {

        if (!previousCreated) {
            previousSelection = createPreviousSelection();
            previousCreated = true;
        }
        return hasPreviousSelection;
    }

    @Override
    public S next() {
        S next = super.next();
        nextListIteratorIndex++;
        hasPreviousSelection = true;
        return next;
    }

    @Override
    public S previous() {
        if (!hasPreviousSelection) {
            throw new NoSuchElementException();
        }
        if (!previousCreated) {
            previousSelection = createPreviousSelection();
        }
        previousCreated = false;
        nextListIteratorIndex--;
        hasUpcomingSelection = true;
        return previousSelection;
    }

    @Override
    public int nextIndex() {
        return nextListIteratorIndex;
    }

    @Override
    public int previousIndex() {
        return nextListIteratorIndex - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The optional operation remove() is not supported.");
    }

    @Override
    public void set(S o) {
        throw new UnsupportedOperationException("The optional operation set(...) is not supported.");
    }

    @Override
    public void add(S o) {
        throw new UnsupportedOperationException("The optional operation add(...) is not supported.");
    }
}
