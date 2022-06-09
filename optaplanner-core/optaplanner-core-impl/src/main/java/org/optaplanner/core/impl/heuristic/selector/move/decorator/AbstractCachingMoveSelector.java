package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public abstract class AbstractCachingMoveSelector<Solution_> extends AbstractMoveSelector<Solution_>
        implements SelectionCacheLifecycleListener<Solution_> {

    protected final MoveSelector<Solution_> childMoveSelector;
    protected final SelectionCacheType cacheType;

    protected List<Move<Solution_>> cachedMoveList = null;

    public AbstractCachingMoveSelector(MoveSelector<Solution_> childMoveSelector, SelectionCacheType cacheType) {
        this.childMoveSelector = childMoveSelector;
        this.cacheType = cacheType;
        if (childMoveSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with neverEnding (" + childMoveSelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(childMoveSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge<>(cacheType, this));
    }

    public MoveSelector<Solution_> getChildMoveSelector() {
        return childMoveSelector;
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        long childSize = childMoveSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childMoveSelector (" + childMoveSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedMoveList = new ArrayList<>((int) childSize);
        childMoveSelector.iterator().forEachRemaining(cachedMoveList::add);
        logger.trace("    Created cachedMoveList: size ({}), moveSelector ({}).",
                cachedMoveList.size(), this);
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        cachedMoveList = null;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public long getSize() {
        return cachedMoveList.size();
    }

}
