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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class FilteringEntitySelector extends AbstractEntitySelector {

    protected final EntitySelector childEntitySelector;
    protected final List<SelectionFilter> filterList;
    protected final boolean bailOutEnabled;

    protected ScoreDirector scoreDirector = null;

    public FilteringEntitySelector(EntitySelector childEntitySelector, List<SelectionFilter> filterList) {
        this.childEntitySelector = childEntitySelector;
        this.filterList = filterList;
        bailOutEnabled = childEntitySelector.isNeverEnding();
        phaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        scoreDirector = phaseScope.getScoreDirector();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        scoreDirector = null;
    }

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return childEntitySelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childEntitySelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return childEntitySelector.getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new JustInTimeFilteringEntityIterator(childEntitySelector.iterator(), determineBailOutSize());
    }

    protected class JustInTimeFilteringEntityIterator extends UpcomingSelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;
        private final long bailOutSize;

        public JustInTimeFilteringEntityIterator(Iterator<Object> childEntityIterator, long bailOutSize) {
            this.childEntityIterator = childEntityIterator;
            this.bailOutSize = bailOutSize;
        }

        @Override
        protected Object createUpcomingSelection() {
            Object next;
            long attemptsBeforeBailOut = bailOutSize;
            do {
                if (!childEntityIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                if (bailOutEnabled) {
                    // if childEntityIterator is neverEnding and nothing is accepted, bail out of the infinite loop
                    if (attemptsBeforeBailOut <= 0L) {
                        logger.warn("Bailing out of neverEnding selector ({}) to avoid infinite loop.",
                                FilteringEntitySelector.this);
                        return noUpcomingSelection();
                    }
                    attemptsBeforeBailOut--;
                }
                next = childEntityIterator.next();
            } while (!accept(scoreDirector, next));
            return next;
        }

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
        return new JustInTimeFilteringEntityIterator(childEntitySelector.endingIterator(), determineBailOutSize());
    }

    protected long determineBailOutSize() {
        if (!bailOutEnabled) {
            return -1L;
        }
        return childEntitySelector.getSize() * 10L;
    }

    protected boolean accept(ScoreDirector scoreDirector, Object entity) {
        for (SelectionFilter filter : filterList) {
            if (!filter.accept(scoreDirector, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Filtering(" + childEntitySelector + ")";
    }

}
