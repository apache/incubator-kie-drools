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

package org.drools.planner.core.heuristic.selector.move.decorator;

import java.util.Iterator;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.common.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.move.AbstractMoveSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.score.director.ScoreDirector;

public class JustInTimeFilteringMoveSelector extends AbstractMoveSelector {

    protected final MoveSelector childMoveSelector;

    protected final SelectionFilter moveFilter;

    protected ScoreDirector scoreDirector = null;

    public JustInTimeFilteringMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType,
            SelectionFilter moveFilter) {
        this.childMoveSelector = childMoveSelector;
        this.moveFilter = moveFilter;
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        if (cacheType != SelectionCacheType.JUST_IN_TIME) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
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

    public boolean isContinuous() {
        return childMoveSelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return childMoveSelector.isNeverEnding();
    }

    public long getSize() {
        return childMoveSelector.getSize();
    }

    public Iterator<Move> iterator() {
        return new JustInTimeFilteringMoveIterator(childMoveSelector.iterator());
    }

    private class JustInTimeFilteringMoveIterator extends UpcomingSelectionIterator<Move> {

        private final Iterator<Move> childMoveIterator;

        public JustInTimeFilteringMoveIterator(Iterator<Move> childMoveIterator) {
            this.childMoveIterator = childMoveIterator;
            createUpcomingSelection();
        }

        @Override
        protected void createUpcomingSelection() {
            Move next;
            do {
                if (!childMoveIterator.hasNext()) {
                    upcomingSelection = null;
                    return;
                }
                next = childMoveIterator.next();
            } while (!moveFilter.accept(scoreDirector, next));
            upcomingSelection = next;
        }

    }

    @Override
    public String toString() {
        return "Filtering(" + childMoveSelector + ")";
    }

}
