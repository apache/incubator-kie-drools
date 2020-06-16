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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SwapMoveSelector extends GenericMoveSelector {

    protected final EntitySelector leftEntitySelector;
    protected final EntitySelector rightEntitySelector;
    protected final List<GenuineVariableDescriptor> variableDescriptorList;
    protected final boolean randomSelection;

    protected final boolean anyChained;
    protected List<SingletonInverseVariableSupply> inverseVariableSupplyList = null;

    public SwapMoveSelector(EntitySelector leftEntitySelector, EntitySelector rightEntitySelector,
            List<GenuineVariableDescriptor> variableDescriptorList, boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.variableDescriptorList = variableDescriptorList;
        this.randomSelection = randomSelection;
        EntityDescriptor leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        EntityDescriptor rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getEntityClass().equals(rightEntityDescriptor.getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftEntitySelector's entityClass (" + leftEntityDescriptor.getEntityClass()
                    + ") which is not equal to the rightEntitySelector's entityClass ("
                    + rightEntityDescriptor.getEntityClass() + ").");
        }
        boolean anyChained = false;
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptors (" + variableDescriptorList + ") is empty.");
        }
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
            if (!variableDescriptor.getEntityDescriptor().getEntityClass().isAssignableFrom(
                    leftEntityDescriptor.getEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor with a entityClass ("
                        + variableDescriptor.getEntityDescriptor().getEntityClass()
                        + ") which is not equal or a superclass to the leftEntitySelector's entityClass ("
                        + leftEntityDescriptor.getEntityClass() + ").");
            }
            if (variableDescriptor.isChained()) {
                anyChained = true;
            }
        }
        this.anyChained = anyChained;
        phaseLifecycleSupport.addEventListener(leftEntitySelector);
        if (leftEntitySelector != rightEntitySelector) {
            phaseLifecycleSupport.addEventListener(rightEntitySelector);
        }
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return !anyChained;
    }

    @Override
    public void solvingStarted(SolverScope solverScope) {
        super.solvingStarted(solverScope);
        if (anyChained) {
            inverseVariableSupplyList = new ArrayList<>(variableDescriptorList.size());
            SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
                SingletonInverseVariableSupply inverseVariableSupply;
                if (variableDescriptor.isChained()) {
                    inverseVariableSupply = supplyManager.demand(
                            new SingletonInverseVariableDemand(variableDescriptor));
                } else {
                    inverseVariableSupply = null;
                }
                inverseVariableSupplyList.add(inverseVariableSupply);
            }
        }
    }

    @Override
    public void solvingEnded(SolverScope solverScope) {
        super.solvingEnded(solverScope);
        if (anyChained) {
            inverseVariableSupplyList = null;
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return leftEntitySelector.isCountable() && rightEntitySelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || leftEntitySelector.isNeverEnding() || rightEntitySelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftEntitySelector, rightEntitySelector);
    }

    @Override
    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove(variableDescriptorList, inverseVariableSupplyList, leftSubSelection,
                                    rightSubSelection)
                            : new SwapMove(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove(variableDescriptorList, inverseVariableSupplyList, leftSubSelection,
                                    rightSubSelection)
                            : new SwapMove(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}
