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

import java.util.Iterator;

import com.google.common.collect.Iterators;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public class ChangeMoveSelector extends GenericMoveSelector {

    protected final EntitySelector entitySelector;
    protected final ValueSelector valueSelector;
    protected final boolean randomSelection;

    protected final boolean chained;

    public ChangeMoveSelector(EntitySelector entitySelector, ValueSelector valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        chained = variableDescriptor.isChained();
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return entitySelector.isCountable() && valueSelector.isCountable();
    }

    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    public long getSize() {
        if (valueSelector instanceof IterableSelector) {
            return entitySelector.getSize() * ((IterableSelector) valueSelector).getSize();
        } else {
            long size = 0;
            for (Iterator it = entitySelector.endingIterator(); it.hasNext(); ) {
                Object entity =  it.next();
                size += valueSelector.getSize(entity);
            }
            return size;
        }
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalChangeMoveIterator();
        } else {
            return new RandomChangeMoveIterator();
        }
    }

    private class OriginalChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<Object> entityIterator;
        private Iterator<Object> valueIterator;

        private Object upcomingEntity;

        private OriginalChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
            valueIterator = Iterators.emptyIterator();
        }

        @Override
        protected Move createUpcomingSelection() {
            while (!valueIterator.hasNext()) {
                if (!entityIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                upcomingEntity = entityIterator.next();
                valueIterator = valueSelector.iterator(upcomingEntity);
            }
            Object toValue = valueIterator.next();

            return chained
                    ? new ChainedChangeMove(upcomingEntity, valueSelector.getVariableDescriptor(), toValue)
                    : new ChangeMove(upcomingEntity, valueSelector.getVariableDescriptor(), toValue);
        }

    }

    private class RandomChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<Object> entityIterator;

        private RandomChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
        }

        @Override
        protected Move createUpcomingSelection() {
            // Ideally, this code should have read:
            //     Object entity = entityIterator.next();
            //     Iterator<Object> valueIterator = valueSelector.iterator(entity);
            //     Object toValue = valueIterator.next();
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!entityIterator.hasNext()) {
                entityIterator = entitySelector.iterator();
                if (!entityIterator.hasNext()) {
                    return noUpcomingSelection();
                }
            }
            Object entity = entityIterator.next();

            Iterator<Object> valueIterator = valueSelector.iterator(entity);
            int entityIteratorCreationCount = 0;
            // This loop is mostly only relevant when the entityIterator or valueIterator is non-random or shuffled
            while (!valueIterator.hasNext()) {
                // Try the next entity
                if (!entityIterator.hasNext()) {
                    entityIterator = entitySelector.iterator();
                    entityIteratorCreationCount++;
                    if (entityIteratorCreationCount >= 2) {
                        // All entity-value combinations have been tried (some even more than once)
                        return noUpcomingSelection();
                    }
                }
                entity = entityIterator.next();
                valueIterator = valueSelector.iterator(entity);
            }
            Object toValue = valueIterator.next();

            return chained
                    ? new ChainedChangeMove(entity, valueSelector.getVariableDescriptor(), toValue)
                    : new ChangeMove(entity, valueSelector.getVariableDescriptor(), toValue);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}
