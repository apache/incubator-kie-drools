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

package org.drools.planner.core.heuristic.selector.move.factory;

import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.AbstractMoveSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Bridges a {@link MoveIteratorFactory} to a {@link MoveSelector}.
 */
public class MoveIteratorFactoryToMoveSelectorBridge extends AbstractMoveSelector {

    protected final MoveIteratorFactory moveIteratorFactory;
    protected final boolean randomSelection;

    protected ScoreDirector scoreDirector = null;

    public MoveIteratorFactoryToMoveSelectorBridge(MoveIteratorFactory moveIteratorFactory, boolean randomSelection) {
        this.moveIteratorFactory = moveIteratorFactory;
        this.randomSelection = randomSelection;
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
        return false;
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        long size = moveIteratorFactory.getSize(scoreDirector);
        if (size < 0L) {
            throw new IllegalStateException("The moveIteratorFactoryClass (" + moveIteratorFactory.getClass()
                    + ") does not know its size (" + size
                    + ") but a correct size is required in this Solver configuration.");
        }
        return size;
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return moveIteratorFactory.createOriginalMoveIterator(scoreDirector);
        } else {
            return moveIteratorFactory.createRandomMoveIterator(scoreDirector, workingRandom);
        }
    }

    @Override
    public String toString() {
        return "MoveIteratorFactory(" + moveIteratorFactory.getClass() + ")";
    }

}
