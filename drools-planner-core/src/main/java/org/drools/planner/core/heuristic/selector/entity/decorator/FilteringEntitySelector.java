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

package org.drools.planner.core.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.entity.AbstractEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.score.director.ScoreDirector;

/**
 * TODO This class isn't used because {@link EntitySelector}'s are upgraded to {@link SelectionCacheType#STEP}.
 */
public class FilteringEntitySelector extends AbstractEntitySelector {

    protected final EntitySelector childEntitySelector;
    protected final List<SelectionFilter> entityFilterList;
    protected final boolean bailOutEnabled;

    protected ScoreDirector scoreDirector = null;

    public FilteringEntitySelector(EntitySelector childEntitySelector,
            List<SelectionFilter> entityFilterList) {
        this.childEntitySelector = childEntitySelector;
        this.entityFilterList = entityFilterList;
        bailOutEnabled = childEntitySelector.isNeverEnding();
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        scoreDirector = solverPhaseScope.getScoreDirector();
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        scoreDirector = null;
    }

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
        return new JustInTimeFilteringEntityIterator(childEntitySelector.iterator());
    }

    private class JustInTimeFilteringEntityIterator extends UpcomingSelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;

        public JustInTimeFilteringEntityIterator(Iterator<Object> childEntityIterator) {
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
                                FilteringEntitySelector.this);
                        next = null;
                        break;
                    }
                    attemptsBeforeBailOut--;
                }
                next = childEntityIterator.next();
            } while (!accept(scoreDirector, next));
            upcomingSelection = next;
        }

    }

    protected long determineBailOutSize() {
        return childEntitySelector.getSize() * 10L;
    }

    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented because this class isn't used
        throw new UnsupportedOperationException();
    }

    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented because this class isn't used
        throw new UnsupportedOperationException();
    }

    private boolean accept(ScoreDirector scoreDirector, Object entity) {
        for (SelectionFilter entityFilter : entityFilterList) {
            if (!entityFilter.accept(scoreDirector, entity)) {
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
