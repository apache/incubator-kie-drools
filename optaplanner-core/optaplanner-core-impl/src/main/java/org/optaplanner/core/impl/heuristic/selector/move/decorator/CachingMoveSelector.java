package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;

/**
 * A {@link MoveSelector} that caches the result of its child {@link MoveSelector}.
 * <p>
 * Keep this code in sync with {@link CachingEntitySelector} and {@link CachingValueSelector}.
 */
public class CachingMoveSelector<Solution_> extends AbstractCachingMoveSelector<Solution_> {

    protected final boolean randomSelection;

    public CachingMoveSelector(MoveSelector<Solution_> childMoveSelector, SelectionCacheType cacheType,
            boolean randomSelection) {
        super(childMoveSelector, cacheType);
        this.randomSelection = randomSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
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
        return "Caching(" + childMoveSelector + ")";
    }

}
