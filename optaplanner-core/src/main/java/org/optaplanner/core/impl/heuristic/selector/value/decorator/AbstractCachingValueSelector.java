package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public abstract class AbstractCachingValueSelector extends AbstractValueSelector
        implements SelectionCacheLifecycleListener {

    protected final EntityIndependentValueSelector childValueSelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedValueList = null;

    public AbstractCachingValueSelector(EntityIndependentValueSelector childValueSelector, SelectionCacheType cacheType) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childValueSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public ValueSelector getChildValueSelector() {
        return childValueSelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childValueSelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedValueList = new ArrayList<Object>((int) childSize);
        // TODO Fail-faster if a non FromSolutionPropertyValueSelector is used
        CollectionUtils.addAll(cachedValueList, childValueSelector.iterator());
        logger.trace("    Created cachedValueList with size ({}) in valueSelector({}).",
                cachedValueList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedValueList = null;
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public long getSize(Object entity) {
        return getSize();
    }

    public long getSize() {
        return cachedValueList.size();
    }

}
