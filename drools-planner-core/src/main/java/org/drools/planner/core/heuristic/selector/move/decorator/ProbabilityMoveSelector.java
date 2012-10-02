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
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.move.AbstractMoveSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.scope.DefaultSolverScope;
import org.drools.planner.core.util.RandomUtils;

public class ProbabilityMoveSelector extends AbstractMoveSelector implements SelectionCacheLifecycleListener {

    protected final MoveSelector childMoveSelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory moveProbabilityWeightFactory;

    protected NavigableMap<Double, Move> cachedMoveMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory moveProbabilityWeightFactory) {
        this.childMoveSelector = childMoveSelector;
        this.cacheType = cacheType;
        this.moveProbabilityWeightFactory = moveProbabilityWeightFactory;
        if (childMoveSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with neverEnding (" + childMoveSelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedMoveMap = new TreeMap<Double, Move>();
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        double probabilityWeightOffset = 0L;
        for (Move entity : childMoveSelector) {
            double probabilityWeight = moveProbabilityWeightFactory.createProbabilityWeight(
                    scoreDirector, entity);
            cachedMoveMap.put(probabilityWeightOffset, entity);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        probabilityWeightTotal = -1.0;
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return true;
    }

    public long getSize() {
        return cachedMoveMap.size();
    }

    public Iterator<Move> iterator() {
        return new Iterator<Move>() {
            public boolean hasNext() {
                return true;
            }

            public Move next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Move> entry = cachedMoveMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove is not supported.");
            }
        };
    }

    @Override
    public String toString() {
        return "Probability(" + childMoveSelector + ")";
    }

}
