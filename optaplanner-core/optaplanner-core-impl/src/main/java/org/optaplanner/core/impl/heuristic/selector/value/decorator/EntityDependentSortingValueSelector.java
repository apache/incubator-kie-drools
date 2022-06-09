package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class EntityDependentSortingValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    protected final ValueSelector<Solution_> childValueSelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionSorter<Solution_, Object> sorter;

    protected ScoreDirector<Solution_> scoreDirector = null;

    public EntityDependentSortingValueSelector(ValueSelector<Solution_> childValueSelector,
            SelectionCacheType cacheType, SelectionSorter<Solution_, Object> sorter) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        this.sorter = sorter;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").");
        }
        if (cacheType != SelectionCacheType.STEP) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    public ValueSelector<Solution_> getChildValueSelector() {
        return childValueSelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        scoreDirector = phaseScope.getScoreDirector();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        scoreDirector = null;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public long getSize(Object entity) {
        return childValueSelector.getSize(entity);
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        long childSize = childValueSelector.getSize(entity);
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        List<Object> cachedValueList = new ArrayList<>((int) childSize);
        childValueSelector.iterator(entity).forEachRemaining(cachedValueList::add);
        logger.trace("    Created cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
        sorter.sort(scoreDirector, cachedValueList);
        logger.trace("    Sorted cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
        return cachedValueList.iterator();
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return iterator(entity);
    }

    @Override
    public String toString() {
        return "Sorting(" + childValueSelector + ")";
    }

}
