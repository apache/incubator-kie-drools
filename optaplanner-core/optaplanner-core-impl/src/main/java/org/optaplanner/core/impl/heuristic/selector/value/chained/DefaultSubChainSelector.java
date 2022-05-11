/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.value.chained;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * This is the common {@link SubChainSelector} implementation.
 */
public class DefaultSubChainSelector<Solution_> extends AbstractSelector<Solution_>
        implements SubChainSelector<Solution_>, SelectionCacheLifecycleListener<Solution_> {

    protected static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

    protected final EntityIndependentValueSelector<Solution_> valueSelector;
    protected final boolean randomSelection;

    protected SingletonInverseVariableSupply inverseVariableSupply;

    // The sub selection here is a sequence. For example from ABCDE, it can select BCD, but not ACD.
    protected final int minimumSubChainSize;
    protected final int maximumSubChainSize;

    protected List<SubChain> anchorTrailingChainList = null;

    public DefaultSubChainSelector(EntityIndependentValueSelector<Solution_> valueSelector, boolean randomSelection,
            int minimumSubChainSize, int maximumSubChainSize) {
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        if (!valueSelector.getVariableDescriptor().isChained()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ")'s valueSelector (" + valueSelector
                    + ") must have a chained variableDescriptor chained ("
                    + valueSelector.getVariableDescriptor().isChained() + ").");
        }
        if (valueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a valueSelector (" + valueSelector
                    + ") with neverEnding (" + valueSelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(valueSelector);
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge<>(CACHE_TYPE, this));
        this.minimumSubChainSize = minimumSubChainSize;
        this.maximumSubChainSize = maximumSubChainSize;
        if (minimumSubChainSize < 1) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s minimumSubChainSize (" + minimumSubChainSize
                    + ") must be at least 1.");
        }
        if (minimumSubChainSize > maximumSubChainSize) {
            throw new IllegalStateException("The minimumSubChainSize (" + minimumSubChainSize
                    + ") must be at least maximumSubChainSize (" + maximumSubChainSize + ").");
        }
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return valueSelector.getVariableDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        inverseVariableSupply = supplyManager.demand(new SingletonInverseVariableDemand<>(variableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        InnerScoreDirector<Solution_, ?> scoreDirector = solverScope.getScoreDirector();
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        long valueSize = valueSelector.getSize();
        // Fail-fast when anchorTrailingChainList.size() could ever be too big
        if (valueSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a valueSelector (" + valueSelector
                    + ") with valueSize (" + valueSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        List<Object> anchorList = new ArrayList<>();
        for (Object value : valueSelector) {
            if (variableDescriptor.isValuePotentialAnchor(value)) {
                anchorList.add(value);
            }
        }
        int anchorListSize = Math.max(anchorList.size(), 1);
        anchorTrailingChainList = new ArrayList<>(anchorListSize);
        int anchorChainInitialCapacity = ((int) valueSize / anchorListSize) + 1;
        for (Object anchor : anchorList) {
            List<Object> anchorChain = new ArrayList<>(anchorChainInitialCapacity);
            Object trailingEntity = inverseVariableSupply.getInverseSingleton(anchor);
            while (trailingEntity != null) {
                anchorChain.add(trailingEntity);
                trailingEntity = inverseVariableSupply.getInverseSingleton(trailingEntity);
            }
            if (anchorChain.size() >= minimumSubChainSize) {
                anchorTrailingChainList.add(new SubChain(anchorChain));
            }
        }
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        anchorTrailingChainList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection;
    }

    @Override
    public long getSize() {
        long selectionSize = 0L;
        for (SubChain anchorTrailingChain : anchorTrailingChainList) {
            selectionSize += calculateSubChainSelectionSize(anchorTrailingChain);
        }
        return selectionSize;
    }

    protected long calculateSubChainSelectionSize(SubChain anchorTrailingChain) {
        long anchorTrailingChainSize = anchorTrailingChain.getSize();
        long n = anchorTrailingChainSize - minimumSubChainSize + 1L;
        long m = (maximumSubChainSize >= anchorTrailingChainSize)
                ? 0L
                : anchorTrailingChainSize - maximumSubChainSize;
        return (n * (n + 1L) / 2L) - (m * (m + 1L) / 2L);
    }

    @Override
    public Iterator<SubChain> iterator() {
        if (!randomSelection) {
            return new OriginalSubChainIterator(anchorTrailingChainList.listIterator());
        } else {
            return new RandomSubChainIterator();
        }
    }

    @Override
    public ListIterator<SubChain> listIterator() {
        if (!randomSelection) {
            return new OriginalSubChainIterator(anchorTrailingChainList.listIterator());
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public ListIterator<SubChain> listIterator(int index) {
        if (!randomSelection) {
            // TODO Implement more efficient ListIterator https://issues.redhat.com/browse/PLANNER-37
            OriginalSubChainIterator it = new OriginalSubChainIterator(anchorTrailingChainList.listIterator());
            for (int i = 0; i < index; i++) {
                it.next();
            }
            return it;
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    private class OriginalSubChainIterator extends UpcomingSelectionIterator<SubChain>
            implements ListIterator<SubChain> {

        private final ListIterator<SubChain> anchorTrailingChainIterator;
        private List<Object> anchorTrailingChain;
        private int fromIndex; // Inclusive
        private int toIndex; // Exclusive

        private int nextListIteratorIndex;

        public OriginalSubChainIterator(ListIterator<SubChain> anchorTrailingChainIterator) {
            this.anchorTrailingChainIterator = anchorTrailingChainIterator;
            fromIndex = 0;
            toIndex = 1;
            anchorTrailingChain = Collections.emptyList();
            nextListIteratorIndex = 0;
        }

        @Override
        protected SubChain createUpcomingSelection() {
            toIndex++;
            if (toIndex - fromIndex > maximumSubChainSize || toIndex > anchorTrailingChain.size()) {
                fromIndex++;
                toIndex = fromIndex + minimumSubChainSize;
                // minimumSubChainSize <= maximumSubChainSize so (toIndex - fromIndex > maximumSubChainSize) is true
                while (toIndex > anchorTrailingChain.size()) {
                    if (!anchorTrailingChainIterator.hasNext()) {
                        return noUpcomingSelection();
                    }
                    anchorTrailingChain = anchorTrailingChainIterator.next().getEntityList();
                    fromIndex = 0;
                    toIndex = fromIndex + minimumSubChainSize;
                }
            }
            return new SubChain(anchorTrailingChain.subList(fromIndex, toIndex));
        }

        @Override
        public SubChain next() {
            nextListIteratorIndex++;
            return super.next();
        }

        @Override
        public int nextIndex() {
            return nextListIteratorIndex;
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("The operation hasPrevious() is not supported."
                    + " See https://issues.redhat.com/browse/PLANNER-37");
        }

        @Override
        public SubChain previous() {
            throw new UnsupportedOperationException("The operation previous() is not supported."
                    + " See https://issues.redhat.com/browse/PLANNER-37");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("The operation previousIndex() is not supported."
                    + " See https://issues.redhat.com/browse/PLANNER-37");
        }

        @Override
        public void set(SubChain subChain) {
            throw new UnsupportedOperationException("The optional operation set() is not supported.");
        }

        @Override
        public void add(SubChain subChain) {
            throw new UnsupportedOperationException("The optional operation add() is not supported.");
        }
    }

    private class RandomSubChainIterator extends UpcomingSelectionIterator<SubChain> {

        private RandomSubChainIterator() {
            if (anchorTrailingChainList.isEmpty()) {
                upcomingSelection = noUpcomingSelection();
                upcomingCreated = true;
            }
        }

        @Override
        protected SubChain createUpcomingSelection() {
            SubChain anchorTrailingChain = selectAnchorTrailingChain();
            // Every SubChain has the same probability (from this point on at least).
            // A random fromIndex and random toIndex would not be fair.
            long selectionSize = calculateSubChainSelectionSize(anchorTrailingChain);
            long selectionIndex = RandomUtils.nextLong(workingRandom, selectionSize);
            // Black magic to translate selectionIndex into fromIndex and toIndex
            long fromIndex = selectionIndex;
            long subChainSize = minimumSubChainSize;
            long countInThatSize = anchorTrailingChain.getSize() - subChainSize + 1;
            while (fromIndex >= countInThatSize) {
                fromIndex -= countInThatSize;
                subChainSize++;
                countInThatSize--;
                if (countInThatSize <= 0) {
                    throw new IllegalStateException("Impossible if calculateSubChainSelectionSize() works correctly.");
                }
            }
            return anchorTrailingChain.subChain((int) fromIndex, (int) (fromIndex + subChainSize));
        }

        private SubChain selectAnchorTrailingChain() {
            // Known issue/compromise: Every SubChain should have same probability, but doesn't.
            // Instead, every anchorTrailingChain has the same probability.
            int anchorTrailingChainListIndex = workingRandom.nextInt(anchorTrailingChainList.size());
            return anchorTrailingChainList.get(anchorTrailingChainListIndex);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelector + ")";
    }

}
