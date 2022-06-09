package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class ShufflingEntitySelector<Solution_> extends AbstractCachingEntitySelector<Solution_> {

    public ShufflingEntitySelector(EntitySelector<Solution_> childEntitySelector, SelectionCacheType cacheType) {
        super(childEntitySelector, cacheType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator() {
        Collections.shuffle(cachedEntityList, workingRandom);
        logger.trace("    Shuffled cachedEntityList with size ({}) in entitySelector({}).",
                cachedEntityList.size(), this);
        return cachedEntityList.iterator();
    }

    @Override
    public ListIterator<Object> listIterator() {
        Collections.shuffle(cachedEntityList, workingRandom);
        logger.trace("    Shuffled cachedEntityList with size ({}) in entitySelector({}).",
                cachedEntityList.size(), this);
        return cachedEntityList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // Presumes that listIterator() has already been called and shuffling would be bad
        return cachedEntityList.listIterator(index);
    }

    @Override
    public String toString() {
        return "Shuffling(" + childEntitySelector + ")";
    }

}
