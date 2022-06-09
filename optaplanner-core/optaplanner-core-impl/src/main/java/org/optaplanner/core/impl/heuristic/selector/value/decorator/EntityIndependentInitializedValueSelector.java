package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class EntityIndependentInitializedValueSelector<Solution_> extends InitializedValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    public EntityIndependentInitializedValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        super(childValueSelector);
    }

    @Override
    public long getSize() {
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new JustInTimeInitializedValueIterator(
                ((EntityIndependentValueSelector<Solution_>) childValueSelector).iterator(), determineBailOutSize());
    }

    protected long determineBailOutSize() {
        if (!bailOutEnabled) {
            return -1L;
        }
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize() * 10L;
    }

}
