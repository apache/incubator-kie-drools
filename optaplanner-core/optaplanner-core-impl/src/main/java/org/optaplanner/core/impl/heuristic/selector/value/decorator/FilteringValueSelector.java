package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class FilteringValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    public static <Solution_> ValueSelector<Solution_> create(ValueSelector<Solution_> valueSelector,
            List<SelectionFilter<Solution_, Object>> filterList) {
        if (valueSelector instanceof EntityIndependentValueSelector) {
            return new EntityIndependentFilteringValueSelector<>(
                    (EntityIndependentValueSelector<Solution_>) valueSelector,
                    filterList);
        } else {
            return new FilteringValueSelector<>(valueSelector, filterList);
        }
    }

    protected final ValueSelector<Solution_> childValueSelector;
    protected final List<SelectionFilter<Solution_, Object>> filterList;
    protected final boolean bailOutEnabled;

    protected ScoreDirector<Solution_> scoreDirector = null;

    protected FilteringValueSelector(ValueSelector<Solution_> childValueSelector,
            List<SelectionFilter<Solution_, Object>> filterList) {
        this.childValueSelector = childValueSelector;
        this.filterList = filterList;
        bailOutEnabled = childValueSelector.isNeverEnding();
        phaseLifecycleSupport.addEventListener(childValueSelector);
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
        return childValueSelector.getSize(entity);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return new JustInTimeFilteringValueIterator(childValueSelector.iterator(entity),
                determineBailOutSize(entity));
    }

    protected class JustInTimeFilteringValueIterator extends UpcomingSelectionIterator<Object> {

        private final Iterator<Object> childValueIterator;
        private final long bailOutSize;

        public JustInTimeFilteringValueIterator(Iterator<Object> childValueIterator, long bailOutSize) {
            this.childValueIterator = childValueIterator;
            this.bailOutSize = bailOutSize;
        }

        @Override
        protected Object createUpcomingSelection() {
            Object next;
            long attemptsBeforeBailOut = bailOutSize;
            do {
                if (!childValueIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                if (bailOutEnabled) {
                    // if childValueIterator is neverEnding and nothing is accepted, bail out of the infinite loop
                    if (attemptsBeforeBailOut <= 0L) {
                        logger.warn("Bailing out of neverEnding selector ({}) to avoid infinite loop.",
                                FilteringValueSelector.this);
                        return noUpcomingSelection();
                    }
                    attemptsBeforeBailOut--;
                }
                next = childValueIterator.next();
            } while (!accept(scoreDirector, next));
            return next;
        }

    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return new JustInTimeFilteringValueIterator(childValueSelector.endingIterator(entity),
                determineBailOutSize(entity));
    }

    protected long determineBailOutSize(Object entity) {
        if (!bailOutEnabled) {
            return -1L;
        }
        return childValueSelector.getSize(entity) * 10L;
    }

    protected boolean accept(ScoreDirector<Solution_> scoreDirector, Object entity) {
        for (SelectionFilter<Solution_, Object> filter : filterList) {
            if (!filter.accept(scoreDirector, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Filtering(" + childValueSelector + ")";
    }

}
