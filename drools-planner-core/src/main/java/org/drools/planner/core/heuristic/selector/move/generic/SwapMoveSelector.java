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
import java.util.ListIterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.EmptyListIterator;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.generic.GenericChainedSwapMove;
import org.drools.planner.core.move.generic.GenericSwapMove;

public class SwapMoveSelector extends GenericMoveSelector {

    protected final EntitySelector leftEntitySelector;
    protected final EntitySelector rightEntitySelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected final boolean leftEqualsRight;
    protected final boolean anyChained;

    public SwapMoveSelector(EntitySelector leftEntitySelector, EntitySelector rightEntitySelector,
            boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.randomSelection = randomSelection;
        leftEqualsRight = (leftEntitySelector == rightEntitySelector);
        PlanningEntityDescriptor leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        PlanningEntityDescriptor rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getPlanningEntityClass().equals(rightEntityDescriptor.getPlanningEntityClass())) {
            throw new IllegalStateException("The moveSelector (" + this.getClass()
                    + ") has a leftEntitySelector's planningEntityClass ("
                    + leftEntityDescriptor.getPlanningEntityClass()
                    + ") which is not equal to the rightEntitySelector's planningEntityClass ("
                    + rightEntityDescriptor.getPlanningEntityClass() + ").");
        }
        variableDescriptors = leftEntityDescriptor.getPlanningVariableDescriptors();
        boolean anyChained = false;
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (variableDescriptor.isChained()) {
                anyChained = true;
            }
        }
        this.anyChained = anyChained;
        solverPhaseLifecycleSupport.addEventListener(leftEntitySelector);
        solverPhaseLifecycleSupport.addEventListener(rightEntitySelector);
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
        if (!leftEqualsRight) {
            return (long) leftEntitySelector.getSize() * (long) rightEntitySelector.getSize();
        } else {
            long leftSize = (long) leftEntitySelector.getSize();
            return leftSize * (leftSize - 1L) / 2L;
        }
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalSwapMoveIterator();
        } else {
            return new RandomSwapMoveIterator();
        }
    }

    private class OriginalSwapMoveIterator extends UpcomingSelectionIterator<Move> {

        private ListIterator<Object> leftEntityIterator;
        private ListIterator<Object> rightEntityIterator;

        private Object leftEntity;

        private OriginalSwapMoveIterator() {
            leftEntityIterator = leftEntitySelector.listIterator();
            rightEntityIterator = IteratorUtils.emptyListIterator();
            createUpcomingSelection();
        }

        protected void createUpcomingSelection() {
            if (!rightEntityIterator.hasNext()) {
                if (!leftEntityIterator.hasNext()) {
                    upcomingSelection = null;
                    return;
                }
                leftEntity = leftEntityIterator.next();

                if (!leftEqualsRight) {
                    rightEntityIterator = rightEntitySelector.listIterator();
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
                    rightEntityIterator = rightEntitySelector.listIterator(leftEntityIterator.nextIndex());
                    // rightEntityIterator's first hasNext() always returns true because of the nextIndex()
                }
            }
            Object rightEntity = rightEntityIterator.next();
            upcomingSelection = anyChained
                    ? new GenericChainedSwapMove(variableDescriptors, leftEntity, rightEntity)
                    : new GenericSwapMove(variableDescriptors, leftEntity, rightEntity);
        }

    }

    private class RandomSwapMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<Object> leftEntityIterator;
        private Iterator<Object> rightEntityIterator;

        private RandomSwapMoveIterator() {
            leftEntityIterator = leftEntitySelector.iterator();
            rightEntityIterator = rightEntitySelector.iterator();
            if (!leftEntityIterator.hasNext() || !rightEntityIterator.hasNext()) {
                upcomingSelection = null;
            } else {
                createUpcomingSelection();
            }
        }

        protected void createUpcomingSelection() {
            // Ideally, this code should have read:
            //     Object leftEntity = leftEntityIterator.next();
            //     Object rightEntity = rightEntityIterator.next(entity);
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!leftEntityIterator.hasNext()) {
                leftEntityIterator = leftEntitySelector.iterator();
            }
            Object leftEntity = leftEntityIterator.next();
            if (!rightEntityIterator.hasNext()) {
                rightEntityIterator = rightEntitySelector.iterator();
            }
            Object rightEntity = rightEntityIterator.next();
            upcomingSelection = anyChained
                    ? new GenericChainedSwapMove(variableDescriptors, leftEntity, rightEntity)
                    : new GenericSwapMove(variableDescriptors, leftEntity, rightEntity);
        }

    }

    @Override
    public String toString() {
        return "SwapMoveSelector(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}
