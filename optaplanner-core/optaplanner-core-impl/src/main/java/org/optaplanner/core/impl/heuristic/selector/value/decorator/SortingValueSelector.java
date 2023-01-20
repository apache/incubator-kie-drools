package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public final class SortingValueSelector<Solution_>
        extends AbstractCachingValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    protected final SelectionSorter<Solution_, Object> sorter;

    public SortingValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector, SelectionCacheType cacheType,
            SelectionSorter<Solution_, Object> sorter) {
        super(childValueSelector, cacheType);
        this.sorter = sorter;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        super.constructCache(solverScope);
        sorter.sort(solverScope.getScoreDirector(), cachedValueList);
        logger.trace("    Sorted cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return cachedValueList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        if (!super.equals(other))
            return false;
        SortingValueSelector<?> that = (SortingValueSelector<?>) other;
        return Objects.equals(sorter, that.sorter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sorter);
    }

    @Override
    public String toString() {
        return "Sorting(" + childValueSelector + ")";
    }

}
