/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;

public class PillarSwapMoveSelector extends GenericMoveSelector {

    protected final PillarSelector leftPillarSelector;
    protected final PillarSelector rightPillarSelector;
    protected final List<GenuineVariableDescriptor> variableDescriptorList;
    protected final boolean randomSelection;

    public PillarSwapMoveSelector(PillarSelector leftPillarSelector, PillarSelector rightPillarSelector,
            List<GenuineVariableDescriptor> variableDescriptorList, boolean randomSelection) {
        this.leftPillarSelector = leftPillarSelector;
        this.rightPillarSelector = rightPillarSelector;
        this.variableDescriptorList = variableDescriptorList;
        this.randomSelection = randomSelection;
        Class<?> leftEntityClass = leftPillarSelector.getEntityDescriptor().getEntityClass();
        if (!leftEntityClass.equals(rightPillarSelector.getEntityDescriptor().getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftPillarSelector's entityClass (" + leftEntityClass
                    + ") which is not equal to the rightPillarSelector's entityClass ("
                    + rightPillarSelector.getEntityDescriptor().getEntityClass() + ").");
        }
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptors (" + variableDescriptorList + ") is empty.");
        }
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
            if (!leftEntityClass.equals(
                    variableDescriptor.getEntityDescriptor().getEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") with a entityClass (" + variableDescriptor.getEntityDescriptor().getEntityClass()
                        + ") which is not equal to the leftPillarSelector's entityClass (" + leftEntityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        phaseLifecycleSupport.addEventListener(leftPillarSelector);
        if (leftPillarSelector != rightPillarSelector) {
            phaseLifecycleSupport.addEventListener(rightPillarSelector);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return leftPillarSelector.isCountable() && rightPillarSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || leftPillarSelector.isNeverEnding() || rightPillarSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftPillarSelector, rightPillarSelector);
    }

    @Override
    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, List<Object>>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, List<Object>>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftPillarSelector + ", " + rightPillarSelector + ")";
    }

}
