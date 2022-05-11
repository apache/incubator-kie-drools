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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Collections;
import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SubChainChangeMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    protected final SubChainSelector<Solution_> subChainSelector;
    protected final EntityIndependentValueSelector<Solution_> valueSelector;
    protected final boolean randomSelection;
    protected final boolean selectReversingMoveToo;

    protected SingletonInverseVariableSupply inverseVariableSupply = null;

    public SubChainChangeMoveSelector(SubChainSelector<Solution_> subChainSelector,
            EntityIndependentValueSelector<Solution_> valueSelector, boolean randomSelection,
            boolean selectReversingMoveToo) {
        this.subChainSelector = subChainSelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        this.selectReversingMoveToo = selectReversingMoveToo;
        if (subChainSelector.getVariableDescriptor() != valueSelector.getVariableDescriptor()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a subChainSelector (" + subChainSelector
                    + ") with variableDescriptor (" + subChainSelector.getVariableDescriptor()
                    + ") which is not the same as the valueSelector (" + valueSelector
                    + ")'s variableDescriptor(" + valueSelector.getVariableDescriptor() + ").");
        }
        if (!randomSelection) {
            if (subChainSelector.isNeverEnding()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a subChainSelector (" + subChainSelector
                        + ") with neverEnding (" + subChainSelector.isNeverEnding() + ").");
            }
            if (valueSelector.isNeverEnding()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a valueSelector (" + valueSelector
                        + ") with neverEnding (" + valueSelector.isNeverEnding() + ").");
            }
        }
        phaseLifecycleSupport.addEventListener(subChainSelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        inverseVariableSupply =
                supplyManager.demand(new SingletonInverseVariableDemand<>(valueSelector.getVariableDescriptor()));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return subChainSelector.isCountable() && valueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection;
    }

    @Override
    public long getSize() {
        return subChainSelector.getSize() * valueSelector.getSize();
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return new OriginalSubChainChangeMoveIterator();
        } else {
            return new RandomSubChainChangeMoveIterator();
        }
    }

    private class OriginalSubChainChangeMoveIterator extends UpcomingSelectionIterator<Move<Solution_>> {

        private final Iterator<SubChain> subChainIterator;
        private Iterator<Object> valueIterator = null;

        private SubChain upcomingSubChain;

        private Move<Solution_> nextReversingSelection = null;

        private OriginalSubChainChangeMoveIterator() {
            subChainIterator = subChainSelector.iterator();
            // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
            valueIterator = Collections.emptyIterator();
        }

        @Override
        protected Move<Solution_> createUpcomingSelection() {
            if (selectReversingMoveToo && nextReversingSelection != null) {
                Move<Solution_> upcomingSelection = nextReversingSelection;
                nextReversingSelection = null;
                return upcomingSelection;
            }

            if (!valueIterator.hasNext()) {
                if (!subChainIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                upcomingSubChain = subChainIterator.next();
                valueIterator = valueSelector.iterator();
                if (!valueIterator.hasNext()) {
                    // valueSelector is completely empty
                    return noUpcomingSelection();
                }
            }
            Object toValue = valueIterator.next();

            Move<Solution_> upcomingSelection = new SubChainChangeMove<>(upcomingSubChain,
                    valueSelector.getVariableDescriptor(), inverseVariableSupply, toValue);
            if (selectReversingMoveToo) {
                nextReversingSelection = new SubChainReversingChangeMove<>(upcomingSubChain,
                        valueSelector.getVariableDescriptor(), inverseVariableSupply, toValue);
            }
            return upcomingSelection;
        }

    }

    private class RandomSubChainChangeMoveIterator extends UpcomingSelectionIterator<Move<Solution_>> {

        private Iterator<SubChain> subChainIterator;
        private Iterator<Object> valueIterator;

        private RandomSubChainChangeMoveIterator() {
            subChainIterator = subChainSelector.iterator();
            valueIterator = valueSelector.iterator();
            // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
            valueIterator = Collections.emptyIterator();
        }

        @Override
        protected Move<Solution_> createUpcomingSelection() {
            // Ideally, this code should have read:
            //     SubChain subChain = subChainIterator.next();
            //     Object toValue = valueIterator.next();
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!subChainIterator.hasNext()) {
                subChainIterator = subChainSelector.iterator();
                if (!subChainIterator.hasNext()) {
                    // subChainSelector is completely empty
                    return noUpcomingSelection();
                }
            }
            SubChain subChain = subChainIterator.next();

            if (!valueIterator.hasNext()) {
                valueIterator = valueSelector.iterator();
                if (!valueIterator.hasNext()) {
                    // valueSelector is completely empty
                    return noUpcomingSelection();
                }
            }
            Object toValue = valueIterator.next();

            boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();
            return reversing
                    ? new SubChainReversingChangeMove<>(subChain, valueSelector.getVariableDescriptor(),
                            inverseVariableSupply, toValue)
                    : new SubChainChangeMove<>(subChain, valueSelector.getVariableDescriptor(), inverseVariableSupply,
                            toValue);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelector + ", " + valueSelector + ")";
    }

}
