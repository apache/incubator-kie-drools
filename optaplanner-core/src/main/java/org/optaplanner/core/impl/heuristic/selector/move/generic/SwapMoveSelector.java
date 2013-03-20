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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Collection;
import java.util.Iterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.optaplanner.core.impl.move.Move;

public class SwapMoveSelector extends GenericMoveSelector {

    protected final EntitySelector leftEntitySelector;
    protected final EntitySelector rightEntitySelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected final boolean anyChained;

    public SwapMoveSelector(EntitySelector leftEntitySelector, EntitySelector rightEntitySelector,
            Collection<PlanningVariableDescriptor> variableDescriptors, boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        PlanningEntityDescriptor leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        PlanningEntityDescriptor rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getPlanningEntityClass().equals(rightEntityDescriptor.getPlanningEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftEntitySelector's entityClass (" + leftEntityDescriptor.getPlanningEntityClass()
                    + ") which is not equal to the rightEntitySelector's entityClass ("
                    + rightEntityDescriptor.getPlanningEntityClass() + ").");
        }
        boolean anyChained = false;
        if (variableDescriptors.isEmpty()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptors (" + variableDescriptors + ") is empty.");
        }
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!leftEntityDescriptor.getPlanningEntityClass().equals(
                    variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor with a entityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the leftEntitySelector's entityClass ("
                        + leftEntityDescriptor.getPlanningEntityClass() + ").");
            }
            if (variableDescriptor.isChained()) {
                anyChained = true;
            }
        }
        this.anyChained = anyChained;
        solverPhaseLifecycleSupport.addEventListener(leftEntitySelector);
        if (leftEntitySelector != rightEntitySelector) {
            solverPhaseLifecycleSupport.addEventListener(rightEntitySelector);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return leftEntitySelector.isContinuous() || rightEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || leftEntitySelector.isNeverEnding() || rightEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftEntitySelector, rightEntitySelector);
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove(variableDescriptors, leftSubSelection, rightSubSelection)
                            : new SwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove(variableDescriptors, leftSubSelection, rightSubSelection)
                            : new SwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}
