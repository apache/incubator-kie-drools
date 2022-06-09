package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

/**
 * Prevents creating chains without an anchor.
 * <p>
 * Filters out every value that is a planning entity for which the {@link PlanningVariable}
 * (on which this {@link ValueSelector} applies to) is uninitialized.
 * <p>
 * Mainly used for chained planning variables, but supports other planning variables too.
 */
public class InitializedValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    public static <Solution_> ValueSelector<Solution_> create(ValueSelector<Solution_> valueSelector) {
        if (valueSelector instanceof EntityIndependentValueSelector) {
            return new EntityIndependentInitializedValueSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector);
        } else {
            return new InitializedValueSelector<>(valueSelector);
        }
    }

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;
    protected final ValueSelector<Solution_> childValueSelector;
    protected final boolean bailOutEnabled;

    protected InitializedValueSelector(ValueSelector<Solution_> childValueSelector) {
        this.variableDescriptor = childValueSelector.getVariableDescriptor();
        this.childValueSelector = childValueSelector;
        bailOutEnabled = childValueSelector.isNeverEnding();
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
        return childValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childValueSelector.isNeverEnding();
    }

    @Override
    public long getSize(Object entity) {
        // TODO use cached results
        return childValueSelector.getSize(entity);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return new JustInTimeInitializedValueIterator(entity, childValueSelector.iterator(entity));
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return new JustInTimeInitializedValueIterator(entity, childValueSelector.endingIterator(entity));
    }

    protected class JustInTimeInitializedValueIterator extends UpcomingSelectionIterator<Object> {

        private final Iterator<Object> childValueIterator;
        private final long bailOutSize;

        public JustInTimeInitializedValueIterator(Object entity, Iterator<Object> childValueIterator) {
            this(childValueIterator, determineBailOutSize(entity));
        }

        public JustInTimeInitializedValueIterator(Iterator<Object> childValueIterator, long bailOutSize) {
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
                                InitializedValueSelector.this);
                        return noUpcomingSelection();
                    }
                    attemptsBeforeBailOut--;
                }
                next = childValueIterator.next();
            } while (!accept(next));
            return next;
        }

    }

    protected long determineBailOutSize(Object entity) {
        if (!bailOutEnabled) {
            return -1L;
        }
        return childValueSelector.getSize(entity) * 10L;
    }

    protected boolean accept(Object value) {
        return value == null
                || !variableDescriptor.getEntityDescriptor().getEntityClass().isAssignableFrom(value.getClass())
                || variableDescriptor.isInitialized(value);
    }

    @Override
    public String toString() {
        return "Initialized(" + childValueSelector + ")";
    }

}
