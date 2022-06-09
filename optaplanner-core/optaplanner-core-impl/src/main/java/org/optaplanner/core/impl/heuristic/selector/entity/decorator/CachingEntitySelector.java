package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;

/**
 * A {@link EntitySelector} that caches the result of its child {@link EntitySelector}.
 * <p>
 * Keep this code in sync with {@link CachingValueSelector} and {@link CachingMoveSelector}.
 */
public class CachingEntitySelector<Solution_> extends AbstractCachingEntitySelector<Solution_> {

    protected final boolean randomSelection;

    public CachingEntitySelector(EntitySelector<Solution_> childEntitySelector, SelectionCacheType cacheType,
            boolean randomSelection) {
        super(childEntitySelector, cacheType);
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
    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return cachedEntityList.iterator();
        } else {
            return new CachedListRandomIterator<>(cachedEntityList, workingRandom);
        }
    }

    @Override
    public ListIterator<Object> listIterator() {
        if (!randomSelection) {
            return cachedEntityList.listIterator();
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        if (!randomSelection) {
            return cachedEntityList.listIterator(index);
        } else {
            throw new IllegalStateException("The selector (" + this
                    + ") does not support a ListIterator with randomSelection (" + randomSelection + ").");
        }
    }

    @Override
    public String toString() {
        return "Caching(" + childEntitySelector + ")";
    }

}
