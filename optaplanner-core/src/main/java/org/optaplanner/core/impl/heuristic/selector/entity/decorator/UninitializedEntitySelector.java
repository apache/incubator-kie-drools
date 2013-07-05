/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

/**
 * Filters out every entity that is initialized for the specified planning variable.
 */
public class UninitializedEntitySelector extends AbstractEntitySelector {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final EntitySelector childEntitySelector;
    protected final boolean bailOutEnabled;

    public UninitializedEntitySelector(EntitySelector childEntitySelector,
            PlanningVariableDescriptor variableDescriptor) {
        this.childEntitySelector = childEntitySelector;
        this.variableDescriptor = variableDescriptor;
        if (!variableDescriptor.getEntityDescriptor().getPlanningEntityClass().isAssignableFrom(
                childEntitySelector.getEntityDescriptor().getPlanningEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptor (" + variableDescriptor
                    + ")'s class is not assignable from its childEntitySelector (" + childEntitySelector
                    + ")'s class.");
        }
        bailOutEnabled = childEntitySelector.isNeverEnding();
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return childEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return childEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return childEntitySelector.getSize();
    }

    public Iterator<Object> iterator() {
        return new JustInTimeInitializedEntityIterator(childEntitySelector.iterator());
    }

    private class JustInTimeInitializedEntityIterator extends UpcomingSelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;

        public JustInTimeInitializedEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
            createUpcomingSelection();
        }

        @Override
        protected void createUpcomingSelection() {
            Object next;
            long attemptsBeforeBailOut = bailOutEnabled ? determineBailOutSize() : 0L;
            do {
                if (!childEntityIterator.hasNext()) {
                    next = null;
                    break;
                }
                if (bailOutEnabled) {
                    // if childEntityIterator is neverEnding and nothing is accepted, bail out of the infinite loop
                    if (attemptsBeforeBailOut <= 0L) {
                        logger.warn("Bailing out of neverEnding selector ({}) to avoid infinite loop.",
                                UninitializedEntitySelector.this);
                        next = null;
                        break;
                    }
                    attemptsBeforeBailOut--;
                }
                next = childEntityIterator.next();
            } while (!accept(next));
            upcomingSelection = next;
        }

    }

    protected long determineBailOutSize() {
        return childEntitySelector.getSize() * 10L;
    }

    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    public Iterator<Object> endingIterator() {
        return childEntitySelector.endingIterator();
    }

    private boolean accept(Object entity) {
        return !variableDescriptor.isInitialized(entity);
    }

    @Override
    public String toString() {
        return "Initialized(" + childEntitySelector + ")";
    }

}
