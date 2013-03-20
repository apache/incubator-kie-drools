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
import java.util.List;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.move.Move;

public class PillarSwapMoveSelector extends GenericMoveSelector {

    protected final PillarSelector leftPillarSelector;
    protected final PillarSelector rightPillarSelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    public PillarSwapMoveSelector(PillarSelector leftPillarSelector, PillarSelector rightPillarSelector,
            Collection<PlanningVariableDescriptor> variableDescriptors, boolean randomSelection) {
        this.leftPillarSelector = leftPillarSelector;
        this.rightPillarSelector = rightPillarSelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        Class<?> leftEntityClass = leftPillarSelector.getEntityDescriptor().getPlanningEntityClass();
        if (!leftEntityClass.equals(rightPillarSelector.getEntityDescriptor().getPlanningEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftPillarSelector's entityClass (" + leftEntityClass
                    + ") which is not equal to the rightPillarSelector's entityClass ("
                    + rightPillarSelector.getEntityDescriptor().getPlanningEntityClass() + ").");
        }
        if (variableDescriptors.isEmpty()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptors (" + variableDescriptors + ") is empty.");
        }
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!leftEntityClass.equals(
                    variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") with a entityClass (" + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the leftPillarSelector's entityClass (" + leftEntityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        solverPhaseLifecycleSupport.addEventListener(leftPillarSelector);
        if (leftPillarSelector != rightPillarSelector) {
            solverPhaseLifecycleSupport.addEventListener(rightPillarSelector);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return leftPillarSelector.isContinuous() || rightPillarSelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || leftPillarSelector.isNeverEnding() || rightPillarSelector.isNeverEnding();
    }

    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftPillarSelector, rightPillarSelector);
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, List<Object>>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, List<Object>>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftPillarSelector + ", " + rightPillarSelector + ")";
    }

}
