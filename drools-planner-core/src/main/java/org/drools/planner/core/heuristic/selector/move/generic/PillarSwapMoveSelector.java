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

package org.drools.planner.core.heuristic.selector.move.generic;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.IteratorUtils;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.entity.pillar.PillarSelector;
import org.drools.planner.core.heuristic.selector.move.iterator.AbstractRandomSwappingMoveIterator;
import org.drools.planner.core.move.Move;

public class PillarSwapMoveSelector extends GenericMoveSelector {

    protected final PillarSelector leftPillarSelector;
    protected final PillarSelector rightPillarSelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected final boolean leftEqualsRight;

    public PillarSwapMoveSelector(PillarSelector leftPillarSelector, PillarSelector rightPillarSelector,
            Collection<PlanningVariableDescriptor> variableDescriptors, boolean randomSelection) {
        this.leftPillarSelector = leftPillarSelector;
        this.rightPillarSelector = rightPillarSelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        leftEqualsRight = (leftPillarSelector == rightPillarSelector);
        Class<?> leftEntityClass = leftPillarSelector.getEntityDescriptor().getPlanningEntityClass();
        if (!leftEntityClass.equals(rightPillarSelector.getEntityDescriptor().getPlanningEntityClass())) {
            throw new IllegalStateException("The moveSelector (" + this.getClass()
                    + ") has a leftPillarSelector's planningEntityClass (" + leftEntityClass
                    + ") which is not equal to the rightPillarSelector's planningEntityClass ("
                    + rightPillarSelector.getEntityDescriptor().getPlanningEntityClass() + ").");
        }
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!leftEntityClass.equals(
                    variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass())) {
                throw new IllegalStateException("The moveSelector (" + this.getClass()
                        + ") has a variableDescriptor (" + variableDescriptor + ") with a planningEntityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the leftPillarSelector's planningEntityClass ("
                        + leftEntityClass + ").");
            }
            if (variableDescriptor.isChained()) {
                throw new IllegalStateException("The moveSelector (" + this.getClass()
                        + ") has a variableDescriptor (" + variableDescriptor
                        + ") which is chained (" + variableDescriptor.isChained() + ").");
            }
        }
        solverPhaseLifecycleSupport.addEventListener(leftPillarSelector);
        solverPhaseLifecycleSupport.addEventListener(rightPillarSelector);
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
        if (!leftEqualsRight) {
            return (long) leftPillarSelector.getSize() * (long) rightPillarSelector.getSize();
        } else {
            long leftSize = (long) leftPillarSelector.getSize();
            return leftSize * (leftSize - 1L) / 2L;
        }
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalPillarSwapMoveIterator();
        } else {
            return new RandomPillarSwapMoveIterator();
        }
    }

    private class OriginalPillarSwapMoveIterator extends UpcomingSelectionIterator<Move> {

        private ListIterator<List<Object>> leftEntityIterator; // TODO Rename to leftPillarIterator
        private ListIterator<List<Object>> rightEntityIterator;

        private List<Object> leftEntity;

        private OriginalPillarSwapMoveIterator() {
            leftEntityIterator = leftPillarSelector.listIterator();
            rightEntityIterator = IteratorUtils.emptyListIterator();
            createUpcomingSelection();
        }

        @Override
        protected void createUpcomingSelection() {
            if (!rightEntityIterator.hasNext()) {
                if (!leftEntityIterator.hasNext()) {
                    upcomingSelection = null;
                    return;
                }
                leftEntity = leftEntityIterator.next();

                if (!leftEqualsRight) {
                    rightEntityIterator = rightPillarSelector.listIterator();
                    if (!rightEntityIterator.hasNext()) {
                        upcomingSelection = null;
                        return;
                    }
                } else {
                    // Select A-B, A-C, B-C. Do not select B-A, C-A, C-B. Do not select A-A, B-B, C-C.
                    if (!leftEntityIterator.hasNext()) {
                        upcomingSelection = null;
                        return;
                    }
                    rightEntityIterator = rightPillarSelector.listIterator(leftEntityIterator.nextIndex());
                    // rightEntityIterator's first hasNext() always returns true because of the nextIndex()
                }
            }
            List<Object> rightEntity = rightEntityIterator.next();
            upcomingSelection = new PillarSwapMove(variableDescriptors, leftEntity, rightEntity);
        }

    }

    private class RandomPillarSwapMoveIterator extends AbstractRandomSwappingMoveIterator<List<Object>> {

        private RandomPillarSwapMoveIterator() {
            super(leftPillarSelector, rightPillarSelector);
        }

        @Override
        protected Move newSwappingMove(List<Object> leftSubSelection, List<Object> rightSubSelection) {
            return new PillarSwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftPillarSelector + ", " + rightPillarSelector + ")";
    }

}
