/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.factory;

import java.util.Iterator;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

/**
 * Bridges a {@link MoveIteratorFactory} to a {@link MoveSelector}.
 */
public class MoveIteratorFactoryToMoveSelectorBridge<Solution_> extends AbstractMoveSelector<Solution_> {

    protected final MoveIteratorFactory<Solution_, ?> moveIteratorFactory;
    protected final boolean randomSelection;

    protected ScoreDirector<Solution_> scoreDirector = null;

    public MoveIteratorFactoryToMoveSelectorBridge(MoveIteratorFactory<Solution_, ?> moveIteratorFactory,
            boolean randomSelection) {
        this.moveIteratorFactory = moveIteratorFactory;
        this.randomSelection = randomSelection;
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return true;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        scoreDirector = phaseScope.getScoreDirector();
        super.phaseStarted(phaseScope);
        moveIteratorFactory.phaseStarted(scoreDirector);
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        moveIteratorFactory.phaseEnded(scoreDirector);
        super.phaseEnded(phaseScope);
        scoreDirector = null;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection;
    }

    @Override
    public long getSize() {
        long size = moveIteratorFactory.getSize(scoreDirector);
        if (size < 0L) {
            throw new IllegalStateException("The moveIteratorFactoryClass (" + moveIteratorFactory.getClass()
                    + ") has size (" + size
                    + ") which is negative, but a correct size is required in this Solver configuration.");
        }
        return size;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return (Iterator<Move<Solution_>>) moveIteratorFactory.createOriginalMoveIterator(scoreDirector);
        } else {
            return (Iterator<Move<Solution_>>) moveIteratorFactory.createRandomMoveIterator(scoreDirector,
                    workingRandom);
        }
    }

    @Override
    public String toString() {
        return "MoveIteratorFactory(" + moveIteratorFactory.getClass() + ")";
    }

}
