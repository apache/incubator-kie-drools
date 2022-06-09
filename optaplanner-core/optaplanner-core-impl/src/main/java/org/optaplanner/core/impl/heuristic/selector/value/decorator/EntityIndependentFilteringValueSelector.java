package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class EntityIndependentFilteringValueSelector<Solution_> extends FilteringValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    public EntityIndependentFilteringValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector,
            List<SelectionFilter<Solution_, Object>> filterList) {
        super(childValueSelector, filterList);
    }

    @Override
    public long getSize() {
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new JustInTimeFilteringValueIterator(((EntityIndependentValueSelector<Solution_>) childValueSelector).iterator(),
                determineBailOutSize());
    }

    protected long determineBailOutSize() {
        if (!bailOutEnabled) {
            return -1L;
        }
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize() * 10L;
    }

}
