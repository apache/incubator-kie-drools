package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public abstract class AbstractCachingMoveSelector extends AbstractMoveSelector implements SelectionCacheLifecycleListener {

    protected final MoveSelector childMoveSelector;
    protected final SelectionCacheType cacheType;

    protected List<Move> cachedMoveList = null;

    public AbstractCachingMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType) {
        this.childMoveSelector = childMoveSelector;
        this.cacheType = cacheType;
        if (childMoveSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with neverEnding (" + childMoveSelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public MoveSelector getChildMoveSelector() {
        return childMoveSelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childMoveSelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedMoveList = new ArrayList<Move>((int) childSize);
        CollectionUtils.addAll(cachedMoveList, childMoveSelector.iterator());
        logger.trace("    Created cachedMoveList with size ({}) in moveSelector({}).",
                cachedMoveList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedMoveList = null;
    }

    public boolean isContinuous() {
        return false;
    }

    public long getSize() {
        return cachedMoveList.size();
    }

}
