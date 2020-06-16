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

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SubChainSwapMoveSelector extends GenericMoveSelector {

    protected final SubChainSelector leftSubChainSelector;
    protected final SubChainSelector rightSubChainSelector;
    protected final GenuineVariableDescriptor variableDescriptor;
    protected final boolean randomSelection;
    protected final boolean selectReversingMoveToo;

    protected SingletonInverseVariableSupply inverseVariableSupply = null;

    public SubChainSwapMoveSelector(SubChainSelector leftSubChainSelector, SubChainSelector rightSubChainSelector,
            boolean randomSelection, boolean selectReversingMoveToo) {
        this.leftSubChainSelector = leftSubChainSelector;
        this.rightSubChainSelector = rightSubChainSelector;
        this.randomSelection = randomSelection;
        this.selectReversingMoveToo = selectReversingMoveToo;
        variableDescriptor = leftSubChainSelector.getVariableDescriptor();
        if (leftSubChainSelector.getVariableDescriptor() != rightSubChainSelector.getVariableDescriptor()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftSubChainSelector's variableDescriptor ("
                    + leftSubChainSelector.getVariableDescriptor()
                    + ") which is not equal to the rightSubChainSelector's variableDescriptor ("
                    + rightSubChainSelector.getVariableDescriptor() + ").");
        }
        phaseLifecycleSupport.addEventListener(leftSubChainSelector);
        if (leftSubChainSelector != rightSubChainSelector) {
            phaseLifecycleSupport.addEventListener(rightSubChainSelector);
        }
    }

    @Override
    public void solvingStarted(SolverScope solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        inverseVariableSupply = supplyManager.demand(new SingletonInverseVariableDemand(variableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return leftSubChainSelector.isCountable() && rightSubChainSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || leftSubChainSelector.isNeverEnding() || rightSubChainSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftSubChainSelector, rightSubChainSelector);
    }

    @Override
    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, SubChain>(leftSubChainSelector, rightSubChainSelector) {
                private Move nextReversingSelection = null;

                @Override
                protected Move createUpcomingSelection() {
                    if (selectReversingMoveToo && nextReversingSelection != null) {
                        Move upcomingSelection = nextReversingSelection;
                        nextReversingSelection = null;
                        return upcomingSelection;
                    }
                    return super.createUpcomingSelection();
                }

                @Override
                protected Move newSwapSelection(SubChain leftSubSelection, SubChain rightSubSelection) {
                    if (selectReversingMoveToo) {
                        nextReversingSelection = new SubChainReversingSwapMove(
                                variableDescriptor, inverseVariableSupply, leftSubSelection, rightSubSelection);
                    }
                    return new SubChainSwapMove(variableDescriptor, inverseVariableSupply, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, SubChain>(leftSubChainSelector, rightSubChainSelector) {
                @Override
                protected Move newSwapSelection(SubChain leftSubSelection, SubChain rightSubSelection) {
                    boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();
                    return reversing
                            ? new SubChainReversingSwapMove(variableDescriptor, inverseVariableSupply, leftSubSelection,
                                    rightSubSelection)
                            : new SubChainSwapMove(variableDescriptor, inverseVariableSupply, leftSubSelection,
                                    rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftSubChainSelector + ", " + rightSubChainSelector + ")";
    }

}
