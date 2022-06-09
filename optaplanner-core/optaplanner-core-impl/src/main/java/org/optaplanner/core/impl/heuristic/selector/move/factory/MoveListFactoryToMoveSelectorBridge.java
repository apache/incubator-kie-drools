package org.optaplanner.core.impl.heuristic.selector.move.factory;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Bridges a {@link MoveListFactory} to a {@link MoveSelector}.
 */
public class MoveListFactoryToMoveSelectorBridge<Solution_> extends AbstractMoveSelector<Solution_>
        implements SelectionCacheLifecycleListener<Solution_> {

    protected final MoveListFactory<Solution_> moveListFactory;
    protected final SelectionCacheType cacheType;
    protected final boolean randomSelection;

    protected List<Move<Solution_>> cachedMoveList = null;

    public MoveListFactoryToMoveSelectorBridge(MoveListFactory<Solution_> moveListFactory,
            SelectionCacheType cacheType, boolean randomSelection) {
        this.moveListFactory = moveListFactory;
        this.cacheType = cacheType;
        this.randomSelection = randomSelection;
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge<>(cacheType, this));
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return true;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        cachedMoveList =
                (List<Move<Solution_>>) moveListFactory.createMoveList(solverScope.getScoreDirector().getWorkingSolution());
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
    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
    }

    @Override
    public long getSize() {
        return cachedMoveList.size();
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return cachedMoveList.iterator();
        } else {
            return new CachedListRandomIterator<>(cachedMoveList, workingRandom);
        }
    }

    @Override
    public String toString() {
        return "MoveListFactory(" + moveListFactory.getClass() + ")";
    }

}
