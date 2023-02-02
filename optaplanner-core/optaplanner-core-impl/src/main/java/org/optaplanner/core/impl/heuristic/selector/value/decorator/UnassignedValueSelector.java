package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacer;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 * Only selects values from the child value selector that are unassigned.
 * This used for {@link QueuedValuePlacer}’s recording value selector during Construction Heuristic phase
 * to prevent reassigning of values that are already assigned to a list variable.
 */
public final class UnassignedValueSelector<Solution_> extends AbstractInverseEntityFilteringValueSelector<Solution_> {

    public UnassignedValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        super(childValueSelector);
    }

    @Override
    protected boolean valueFilter(Object value) {
        return inverseVariableSupply.getInverseSingleton(value) == null;
    }

    @Override
    public String toString() {
        return "Unassigned(" + childValueSelector + ")";
    }
}
