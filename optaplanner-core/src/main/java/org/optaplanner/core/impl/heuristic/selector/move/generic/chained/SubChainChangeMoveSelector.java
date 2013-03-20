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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.move.Move;

public class SubChainChangeMoveSelector extends GenericMoveSelector {

    protected final SubChainSelector subChainSelector;
    protected final EntityIndependentValueSelector valueSelector;
    protected final boolean randomSelection;
    protected final boolean selectReversingMoveToo;

    public SubChainChangeMoveSelector(SubChainSelector subChainSelector, EntityIndependentValueSelector valueSelector,
            boolean randomSelection, boolean selectReversingMoveToo) {
        this.subChainSelector = subChainSelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        this.selectReversingMoveToo = selectReversingMoveToo;
        if (subChainSelector.getVariableDescriptor() != valueSelector.getVariableDescriptor()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a subChainSelector (" + subChainSelector
                    + ") with variableDescriptor (" + subChainSelector.getVariableDescriptor()
                    + ") which is not the same as the valueSelector (" + valueSelector
                    +")'s variableDescriptor(" + valueSelector.getVariableDescriptor() + ").");
        }
        if (!randomSelection) {
            if (subChainSelector.isNeverEnding()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a subChainSelector (" + subChainSelector
                        + ") with neverEnding (" + subChainSelector.isNeverEnding() + ").");
            }
            if (valueSelector.isNeverEnding()) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a valueSelector (" + valueSelector
                        + ") with neverEnding (" + valueSelector.isNeverEnding() + ").");
            }
        }
        solverPhaseLifecycleSupport.addEventListener(subChainSelector);
        solverPhaseLifecycleSupport.addEventListener(valueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return subChainSelector.isContinuous() || valueSelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        return subChainSelector.getSize() * valueSelector.getSize();
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalSubChainChangeMoveIterator();
        } else {
            return new RandomSubChainChangeMoveIterator();
        }
    }

    private class OriginalSubChainChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<SubChain> subChainIterator;
        private Iterator<Object> valueIterator = null;

        private SubChain upcomingSubChain;

        private Move nextReversingSelection = null;

        private OriginalSubChainChangeMoveIterator() {
            subChainIterator = subChainSelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any entity parameter
            if (!subChainIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingSelection = null;
            } else {
                upcomingSubChain = subChainIterator.next();
                createUpcomingSelection();
            }
        }

        protected void createUpcomingSelection() {
            if (selectReversingMoveToo && nextReversingSelection != null) {
                upcomingSelection = nextReversingSelection;
                nextReversingSelection = null;
                return;
            }
            while (!valueIterator.hasNext()) {
                if (!subChainIterator.hasNext()) {
                    upcomingSelection = null;
                    return;
                }
                upcomingSubChain = subChainIterator.next();
                valueIterator = valueSelector.iterator();
            }
            Object toValue = valueIterator.next();
            upcomingSelection = new SubChainChangeMove(upcomingSubChain, valueSelector.getVariableDescriptor(), toValue);
            if (selectReversingMoveToo) {
                nextReversingSelection = new SubChainReversingChangeMove(
                        upcomingSubChain, valueSelector.getVariableDescriptor(), toValue);
            }
        }

    }

    private class RandomSubChainChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<SubChain> subChainIterator;
        private Iterator<Object> valueIterator;

        private RandomSubChainChangeMoveIterator() {
            subChainIterator = subChainSelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any subChain parameter
            if (!subChainIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingSelection = null;
            } else {
                createUpcomingSelection();
            }
        }

        protected void createUpcomingSelection() {
            // Ideally, this code should have read:
            //     SubChain subChain = subChainIterator.next();
            //     Object toValue = valueIterator.next(subChain);
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!subChainIterator.hasNext()) {
                subChainIterator = subChainSelector.iterator();
            }
            SubChain subChain = subChainIterator.next();
            int subChainIteratorCreationCount = 0;
            // This loop is mostly only relevant when the subChainIterator or valueIterator is non-random or shuffled
            while (!valueIterator.hasNext()) {
                // First try to reset the valueIterator to get a next value
                valueIterator = valueSelector.iterator();
                // If that's not sufficient (that subChain has an empty value list), then use the next subChain
                if (!valueIterator.hasNext()) {
                    if (!subChainIterator.hasNext()) {
                        subChainIterator = subChainSelector.iterator();
                        subChainIteratorCreationCount++;
                        if (subChainIteratorCreationCount >= 2) {
                            // All subChain-value combinations have been tried (some even more than once)
                            upcomingSelection = null;
                            return;
                        }
                    }
                    subChain = subChainIterator.next();
                }
            }
            Object toValue = valueIterator.next();
            boolean reversing = selectReversingMoveToo ? workingRandom.nextBoolean() : false;
            upcomingSelection = reversing
                    ? new SubChainReversingChangeMove(subChain, valueSelector.getVariableDescriptor(), toValue)
                    : new SubChainChangeMove(subChain, valueSelector.getVariableDescriptor(), toValue);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelector + ", " + valueSelector + ")";
    }

}
