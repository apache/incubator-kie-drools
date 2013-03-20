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

package org.optaplanner.core.impl.heuristic.selector.value.chained;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.util.RandomUtils;

/**
 * This is the common {@link SubChainSelector} implementation.
 */
public class DefaultSubChainSelector extends AbstractSelector
        implements SubChainSelector, SelectionCacheLifecycleListener {

    protected static final SelectionCacheType CACHE_TYPE = SelectionCacheType.STEP;

    protected final EntityIndependentValueSelector valueSelector;
    protected final boolean randomSelection;

    protected final int minimumSubChainSize;
    protected final int maximumSubChainSize;

    protected List<SubChain> anchorTrailingChainList = null;

    public DefaultSubChainSelector(EntityIndependentValueSelector valueSelector, boolean randomSelection,
            int minimumSubChainSize, int maximumSubChainSize) {
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        this.minimumSubChainSize = minimumSubChainSize;
        this.maximumSubChainSize = maximumSubChainSize;
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
        solverPhaseLifecycleSupport.addEventListener(valueSelector);
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(CACHE_TYPE, this));
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

    public PlanningVariableDescriptor getVariableDescriptor() {
        return valueSelector.getVariableDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return CACHE_TYPE;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        PlanningVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        Class<?> entityClass = variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass();
        long valueSize = valueSelector.getSize();
        // Fail-fast when anchorTrailingChainSize could ever be too big
        if (valueSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a valueSelector (" + valueSelector
                    + ") with valueSize (" + valueSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        // Temporary LinkedList to avoid using a bad initialCapacity
        List<Object> anchorList = new LinkedList<Object>();
        for (Object value : valueSelector) {
            if (!entityClass.isAssignableFrom(value.getClass())) {
                anchorList.add(value);
            }
        }
        anchorTrailingChainList = new ArrayList<SubChain>(anchorList.size());
        int anchorChainInitialCapacity = ((int) valueSize / anchorList.size()) + 1;
        for (Object anchor : anchorList) {
            List<Object> anchorChain = new ArrayList<Object>(anchorChainInitialCapacity);
            Object trailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, anchor);
            while (trailingEntity != null) {
                anchorChain.add(trailingEntity);
                trailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, trailingEntity);
            }
            if (anchorChain.size() >= minimumSubChainSize) {
                anchorTrailingChainList.add(new SubChain(anchorChain));
            }
        }
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        anchorTrailingChainList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        long size = 0L;
        for (SubChain anchorTrailingChain : anchorTrailingChainList) {
            long n = (long) Math.min(maximumSubChainSize, anchorTrailingChain.getEntityList().size())
                    - (long) minimumSubChainSize + 1L;
            size += n * (n + 1L) / 2L;
        }
        return size;
    }

    public Iterator<SubChain> iterator() {
        if (!randomSelection) {
            return new OriginalSubChainIterator(anchorTrailingChainList.iterator());
        } else {
            return new RandomSubChainIterator();
        }
    }

    public ListIterator<SubChain> listIterator() {
        if (!randomSelection) {
            // TODO refactor OriginalSubChainIterator to implement ListIterator
            // https://issues.jboss.org/browse/JBRULES-3586
            throw new UnsupportedOperationException("This class ("
                    + getClass() + ") does not support the listIterator() methods yet. "
                    + "As a result you can only use SubChain based swap moves with randomSelection true. "
                    + " https://issues.jboss.org/browse/JBRULES-3586");
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    public ListIterator<SubChain> listIterator(int index) {
        if (!randomSelection) {
            // TODO refactor OriginalSubChainIterator to implement ListIterator
            // https://issues.jboss.org/browse/JBRULES-3586
            throw new UnsupportedOperationException("This class ("
                    + getClass() + ") does not support the listIterator() methods yet. "
                    + "As a result you can only use SubChain based swap moves with randomSelection true. "
                    + " https://issues.jboss.org/browse/JBRULES-3586");
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    private class OriginalSubChainIterator extends UpcomingSelectionIterator<SubChain> {

        private final Iterator<SubChain> anchorTrailingChainIterator;
        private List<Object> anchorTrailingChain;
        private int anchorTrailingChainSize;
        private int fromIndex; // Inclusive
        private int toIndex; // Exclusive

        public OriginalSubChainIterator(Iterator<SubChain> anchorTrailingChainIterator) {
            this.anchorTrailingChainIterator = anchorTrailingChainIterator;
            anchorTrailingChainSize = -1;
            fromIndex = -1;
            toIndex = -1;
            createUpcomingSelection();
        }

        @Override
        protected void createUpcomingSelection() {
            toIndex++;
            if (toIndex > anchorTrailingChainSize) {
                fromIndex++;
                toIndex = fromIndex + minimumSubChainSize;
                while (toIndex > anchorTrailingChainSize) {
                    if (!anchorTrailingChainIterator.hasNext()) {
                        upcomingSelection = null;
                        return;
                    }
                    anchorTrailingChain = anchorTrailingChainIterator.next().getEntityList();
                    anchorTrailingChainSize = Math.min(maximumSubChainSize, anchorTrailingChain.size());
                    fromIndex = 0;
                    toIndex = fromIndex + minimumSubChainSize;
                }
            }
            upcomingSelection = new SubChain(anchorTrailingChain.subList(fromIndex, toIndex));
        }

    }

    private class RandomSubChainIterator extends UpcomingSelectionIterator<SubChain> {

        private RandomSubChainIterator() {
            if (anchorTrailingChainList.isEmpty()) {
                upcomingSelection = null;
            } else {
                createUpcomingSelection();
            }
        }

        @Override
        protected void createUpcomingSelection() {
            // TODO support SelectionProbabilityWeightFactory, such as FairSelectorProbabilityWeightFactory too
            int anchorTrailingChainListIndex = workingRandom.nextInt(anchorTrailingChainList.size());
            List<Object> anchorTrailingChain = anchorTrailingChainList.get(anchorTrailingChainListIndex).getEntityList();
            // Every SubChain must have same probability. A random fromIndex and random toIndex would not be fair.
            int n = Math.min(maximumSubChainSize, anchorTrailingChain.size()) - minimumSubChainSize + 1;
            long size = ((long) n) * (((long) n) + 1L) / 2L;
            long sizeIndex = RandomUtils.nextLong(workingRandom, size);
            // Black magic to translate sizeIndex into fromIndex and toIndex
            int fromIndex = 0;
            long subChainSize = sizeIndex;
            long maxSize = (long) n;
            while (subChainSize >= maxSize) {
                subChainSize -= maxSize;
                fromIndex++;
                maxSize--;
            }
            int toIndex = fromIndex + ((int) subChainSize) + minimumSubChainSize;
            upcomingSelection = new SubChain(anchorTrailingChain.subList(fromIndex, toIndex));
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelector + ")";
    }

}
