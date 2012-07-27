/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.decider.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.Decider;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A CompositeSelector unions multiple Selectors.
 */
@Deprecated
public class CompositeSelector extends AbstractSelector {

    protected List<Selector> selectorList;

    public void setSelectorList(List<Selector> selectorList) {
        this.selectorList = selectorList;
    }

    @Override
    public void setDecider(Decider decider) {
        super.setDecider(decider);
        for (Selector selector : selectorList) {
            selector.setDecider(decider);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        for (Selector selector : selectorList) {
            selector.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        for (Selector selector : selectorList) {
            selector.phaseStarted(localSearchSolverPhaseScope);
        }
    }

    @Override
    public void stepStarted(LocalSearchStepScope localSearchStepScope) {
        for (Selector selector : selectorList) {
            selector.stepStarted(localSearchStepScope);
        }
    }

    public Iterator<Move> moveIterator(LocalSearchStepScope localSearchStepScope) {
        List<Iterator<Move>> moveIteratorList = new ArrayList<Iterator<Move>>(selectorList.size());
        // TODO unoptimized code: use Selector.moveList(localSearchStepScope) instead
        for (Selector selector : selectorList) {
            Iterator<Move> moveIterator = selector.moveIterator(localSearchStepScope);
            if (moveIterator.hasNext()) {
                moveIteratorList.add(moveIterator);
            }
        }
        return new CompositeSelectorMoveIterator(localSearchStepScope.getWorkingRandom(), moveIteratorList);
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        for (Selector selector : selectorList) {
            selector.stepEnded(localSearchStepScope);
        }
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        for (Selector selector : selectorList) {
            selector.phaseEnded(localSearchSolverPhaseScope);
        }
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        for (Selector selector : selectorList) {
            selector.solvingEnded(solverScope);
        }
    }

    private static class CompositeSelectorMoveIterator implements Iterator<Move> {

        private final List<Iterator<Move>> moveIteratorList;
        private final Random workingRandom;

        public CompositeSelectorMoveIterator(Random workingRandom, List<Iterator<Move>> moveIteratorList) {
            this.moveIteratorList = moveIteratorList;
            this.workingRandom = workingRandom;
        }

        public boolean hasNext() {
            return !moveIteratorList.isEmpty();
        }

        public Move next() {
            int moveIteratorIndex = workingRandom.nextInt(moveIteratorList.size());
            Iterator<Move> moveIterator = moveIteratorList.get(moveIteratorIndex);
            Move next = moveIterator.next();
            if (!moveIterator.hasNext()) {
                moveIteratorList.remove(moveIteratorIndex);
            }
            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException(
                    "The remove method is not supported on CompositeSelectorMoveIterator");
        }

    }

}
