package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Collections;
import java.util.Iterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class ShufflingMoveSelector<Solution_> extends AbstractCachingMoveSelector<Solution_> {

    public ShufflingMoveSelector(MoveSelector<Solution_> childMoveSelector, SelectionCacheType cacheType) {
        super(childMoveSelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        Collections.shuffle(cachedMoveList, workingRandom);
        logger.trace("    Shuffled cachedMoveList with size ({}) in moveSelector({}).",
                cachedMoveList.size(), this);
        return cachedMoveList.iterator();
    }

    @Override
    public String toString() {
        return "Shuffling(" + childMoveSelector + ")";
    }

}
