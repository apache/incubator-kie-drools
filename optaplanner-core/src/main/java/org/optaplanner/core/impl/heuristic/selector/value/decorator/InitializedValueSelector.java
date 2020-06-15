/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class InitializedValueSelector extends AbstractValueSelector {

    public static ValueSelector create(ValueSelector valueSelector) {
        if (valueSelector instanceof EntityIndependentValueSelector) {
            return new EntityIndependentInitializedValueSelector((EntityIndependentValueSelector) valueSelector);
        } else {
            return new InitializedValueSelector(valueSelector);
        }
    }

    protected final GenuineVariableDescriptor variableDescriptor;
    protected final ValueSelector childValueSelector;
    protected final boolean bailOutEnabled;

    protected InitializedValueSelector(ValueSelector childValueSelector) {
        this.variableDescriptor = childValueSelector.getVariableDescriptor();
        this.childValueSelector = childValueSelector;
        bailOutEnabled = childValueSelector.isNeverEnding();
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public GenuineVariableDescriptor getVariableDescriptor() {
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
