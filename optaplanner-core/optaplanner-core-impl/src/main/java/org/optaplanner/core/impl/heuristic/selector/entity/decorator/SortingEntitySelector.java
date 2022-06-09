package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SortingEntitySelector<Solution_> extends AbstractCachingEntitySelector<Solution_> {

    protected final SelectionSorter<Solution_, Object> sorter;

    public SortingEntitySelector(EntitySelector<Solution_> childEntitySelector, SelectionCacheType cacheType,
            SelectionSorter<Solution_, Object> sorter) {
        super(childEntitySelector, cacheType);
        this.sorter = sorter;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        super.constructCache(solverScope);
        sorter.sort(solverScope.getScoreDirector(), cachedEntityList);
        logger.trace("    Sorted cachedEntityList: size ({}), entitySelector ({}).",
                cachedEntityList.size(), this);
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator() {
        return cachedEntityList.iterator();
    }

    @Override
    public ListIterator<Object> listIterator() {
        return cachedEntityList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return cachedEntityList.listIterator(index);
    }

    @Override
    public String toString() {
        return "Sorting(" + childEntitySelector + ")";
    }

}
