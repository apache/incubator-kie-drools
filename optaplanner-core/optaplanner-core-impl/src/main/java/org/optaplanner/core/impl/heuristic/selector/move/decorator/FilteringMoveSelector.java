package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class FilteringMoveSelector<Solution_> extends AbstractMoveSelector<Solution_> {

    protected final MoveSelector<Solution_> childMoveSelector;
    protected final SelectionFilter<Solution_, Move<Solution_>> filter;
    protected final boolean bailOutEnabled;

    protected ScoreDirector<Solution_> scoreDirector = null;

    public FilteringMoveSelector(MoveSelector<Solution_> childMoveSelector,
            SelectionFilter<Solution_, Move<Solution_>> filter) {
        this.childMoveSelector = childMoveSelector;
        this.filter = filter;
        bailOutEnabled = childMoveSelector.isNeverEnding();
        phaseLifecycleSupport.addEventListener(childMoveSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        scoreDirector = phaseScope.getScoreDirector();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        scoreDirector = null;
    }

    @Override
    public boolean isCountable() {
        return childMoveSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childMoveSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return childMoveSelector.getSize();
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new JustInTimeFilteringMoveIterator(childMoveSelector.iterator(), determineBailOutSize());
    }

    private class JustInTimeFilteringMoveIterator extends UpcomingSelectionIterator<Move<Solution_>> {

        private final Iterator<Move<Solution_>> childMoveIterator;
        private final long bailOutSize;

        public JustInTimeFilteringMoveIterator(Iterator<Move<Solution_>> childMoveIterator, long bailOutSize) {
            this.childMoveIterator = childMoveIterator;
            this.bailOutSize = bailOutSize;
        }

        @Override
        protected Move<Solution_> createUpcomingSelection() {
            Move<Solution_> next;
            long attemptsBeforeBailOut = bailOutSize;
            do {
                if (!childMoveIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                if (bailOutEnabled) {
                    // if childMoveIterator is neverEnding and nothing is accepted, bail out of the infinite loop
                    if (attemptsBeforeBailOut <= 0L) {
                        logger.warn("Bailing out of neverEnding selector ({}) to avoid infinite loop.",
                                FilteringMoveSelector.this);
                        return noUpcomingSelection();
                    }
                    attemptsBeforeBailOut--;
                }
                next = childMoveIterator.next();
            } while (!accept(scoreDirector, next));
            return next;
        }

    }

    protected long determineBailOutSize() {
        if (!bailOutEnabled) {
            return -1L;
        }
        return childMoveSelector.getSize() * 10L;
    }

    protected boolean accept(ScoreDirector<Solution_> scoreDirector, Move<Solution_> move) {
        if (filter != null) {
            if (!filter.accept(scoreDirector, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Filtering(" + childMoveSelector + ")";
    }

}
