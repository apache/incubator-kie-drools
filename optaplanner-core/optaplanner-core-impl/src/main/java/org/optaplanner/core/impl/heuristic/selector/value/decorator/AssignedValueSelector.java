package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ElementRef;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 * Only selects values from the child value selector that are assigned.
 * This is used for {@link ElementDestinationSelector}’s child value selector during Construction Heuristic phase
 * to filter out unassigned values, which cannot be used to build a destination {@link ElementRef}.
 */
public final class AssignedValueSelector<Solution_> extends AbstractInverseEntityFilteringValueSelector<Solution_> {

    public AssignedValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        super(childValueSelector);
    }

    @Override
    protected boolean valueFilter(Object value) {
        return inverseVariableSupply.getInverseSingleton(value) != null;
    }

    @Override
    public String toString() {
        return "Assigned(" + childValueSelector + ")";
    }
}
