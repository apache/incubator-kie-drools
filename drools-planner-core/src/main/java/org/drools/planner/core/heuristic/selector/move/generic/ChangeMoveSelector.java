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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.value.ValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.generic.GenericChangeMove;

public class ChangeMoveSelector extends GenericMoveSelector {

    private final EntitySelector entitySelector;
    private final ValueSelector valueSelector;
    protected final boolean randomSelection;
    protected final SelectionCacheType cacheType;

    public ChangeMoveSelector(EntitySelector entitySelector, ValueSelector valueSelector,
            boolean randomSelection, SelectionCacheType cacheType) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        this.cacheType = cacheType;
        PlanningVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        if (variableDescriptor.isChained()) {
            // TODO support chained
            throw new UnsupportedOperationException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ")'s planningVariableDescriptor (" + variableDescriptor.getVariableName()
                    + ") is chained and can therefor not use the moveSelector (" + getClass() + ").");
        }
        if (cacheType != SelectionCacheType.JUST_IN_TIME) {
            throw new UnsupportedOperationException(); // TODO FIXME
        }
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        solverPhaseLifecycleSupport.addEventListener(valueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return entitySelector.isContinuous() || valueSelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    public long getSize() {
        return entitySelector.getSize() * valueSelector.getSize();
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalChangeMoveIterator();
        } else {
            return new RandomChangeMoveIterator();
        }
    }

    private class OriginalChangeMoveIterator implements Iterator<Move> {

        private Iterator<Object> entityIterator;
        private ValueIterator valueIterator;

        private Object entity;
        private Move upcomingMove;

        private OriginalChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any entity parameter
            if (!entityIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingMove = null;
            } else {
                entity = entityIterator.next();
                createUpcomingMove();
            }
        }

        private void createUpcomingMove() {
            while (!valueIterator.hasNext(entity)) {
                if (!entityIterator.hasNext()) {
                    upcomingMove = null;
                    return;
                }
                entity = entityIterator.next();
                valueIterator = valueSelector.iterator();
            }
            Object toValue = valueIterator.next(entity);
            upcomingMove = new GenericChangeMove(entity, valueSelector.getVariableDescriptor(), toValue);
        }

        public boolean hasNext() {
            return upcomingMove != null;
        }

        public Move next() {
            if (upcomingMove == null) {
                throw new NoSuchElementException();
            }
            Move move = upcomingMove;
            createUpcomingMove();
            return move;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    private class RandomChangeMoveIterator implements Iterator<Move> {

        private Iterator<Object> entityIterator;
        private ValueIterator valueIterator;

        private Move upcomingMove;

        private RandomChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any entity parameter
            if (!entityIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingMove = null;
            } else {
                createUpcomingMove();
            }
        }

        private void createUpcomingMove() {
            // Ideally, this code should have read:
            //     Object entity = entityIterator.next();
            //     Object toValue = valueIterator.next(entity);
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!entityIterator.hasNext()) {
                entityIterator = entitySelector.iterator();
            }
            Object entity = entityIterator.next();
            int entityIteratorCreationCount = 0;
            // This loop is mostly only relevant when the entityIterator or valueIterator is non-random or shuffled
            while (!valueIterator.hasNext(entity)) {
                // First try to reset the valueIterator to get a next value
                valueIterator = valueSelector.iterator();
                // If that's not sufficient (that entity has an empty value list), then use the next entity
                if (!valueIterator.hasNext(entity)) {
                    if (!entityIterator.hasNext()) {
                        entityIterator = entitySelector.iterator();
                        entityIteratorCreationCount++;
                        if (entityIteratorCreationCount >= 2) {
                            // All entity-value combinations have been tried (some even more than once)
                            upcomingMove = null;
                            return;
                        }
                    }
                    entity = entityIterator.next();
                }
            }
            Object toValue = valueIterator.next(entity);
            upcomingMove = new GenericChangeMove(entity, valueSelector.getVariableDescriptor(), toValue);
        }

        public boolean hasNext() {
            return upcomingMove != null;
        }

        public Move next() {
            if (upcomingMove == null) {
                throw new NoSuchElementException();
            }
            Move move = upcomingMove;
            createUpcomingMove();
            return move;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

}
