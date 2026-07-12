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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;

public class PillarSwapMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    protected final PillarSelector<Solution_> leftPillarSelector;
    protected final PillarSelector<Solution_> rightPillarSelector;
    protected final List<GenuineVariableDescriptor<Solution_>> variableDescriptorList;
    protected final boolean randomSelection;

    public PillarSwapMoveSelector(PillarSelector<Solution_> leftPillarSelector,
            PillarSelector<Solution_> rightPillarSelector,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptorList, boolean randomSelection) {
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
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
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
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move<Solution_> newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove<>(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<>(leftPillarSelector, rightPillarSelector) {
                @Override
                protected Move<Solution_> newSwapSelection(List<Object> leftSubSelection, List<Object> rightSubSelection) {
                    return new PillarSwapMove<>(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftPillarSelector + ", " + rightPillarSelector + ")";
    }

}
