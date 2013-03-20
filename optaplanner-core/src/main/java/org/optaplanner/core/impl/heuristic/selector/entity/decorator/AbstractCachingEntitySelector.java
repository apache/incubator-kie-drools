package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public abstract class AbstractCachingEntitySelector extends AbstractEntitySelector implements SelectionCacheLifecycleListener {

    protected final EntitySelector childEntitySelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedEntityList = null;

    public AbstractCachingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with neverEnding (" + childEntitySelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public EntitySelector getChildEntitySelector() {
        return childEntitySelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childEntitySelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedEntityList, childEntitySelector.iterator());
        logger.trace("    Created cachedEntityList with size ({}) in entitySelector({}).",
                cachedEntityList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedEntityList = null;
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public long getSize() {
        return cachedEntityList.size();
    }

}
