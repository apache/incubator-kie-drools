package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;

/**
 * IMPORTANT: The constructor of any subclass of this abstract class, should never call any of its child
 * {@link Selector}'s {@link Iterator#hasNext()} or {@link Iterator#next()} methods,
 * because that can cause descendant {@link Selector}s to be selected too early
 * (which breaks {@link MimicReplayingEntitySelector}).
 *
 * @param <S> Selection type, for example a {@link Move} class, an entity class or a value class.
 */
public abstract class UpcomingSelectionIterator<S> extends SelectionIterator<S> {

    protected boolean upcomingCreated = false;
    protected boolean hasUpcomingSelection = true;
    protected S upcomingSelection;

    @Override
    public boolean hasNext() {
        if (!upcomingCreated) {
            upcomingSelection = createUpcomingSelection();
            upcomingCreated = true;
        }
        return hasUpcomingSelection;
    }

    @Override
    public S next() {
        if (!hasUpcomingSelection) {
            throw new NoSuchElementException();
        }
        if (!upcomingCreated) {
            upcomingSelection = createUpcomingSelection();
        }
        upcomingCreated = false;
        return upcomingSelection;
    }

    protected abstract S createUpcomingSelection();

    protected S noUpcomingSelection() {
        hasUpcomingSelection = false;
        return null;
    }

    @Override
    public String toString() {
        if (!upcomingCreated) {
            return "Next upcoming (?)";
        } else if (!hasUpcomingSelection) {
            return "No next upcoming";
        } else {
            return "Next upcoming (" + upcomingSelection + ")";
        }
    }

}
