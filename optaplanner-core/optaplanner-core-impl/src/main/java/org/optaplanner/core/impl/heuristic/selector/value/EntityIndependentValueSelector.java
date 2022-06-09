package org.optaplanner.core.impl.heuristic.selector.value;

import org.optaplanner.core.impl.heuristic.selector.IterableSelector;

/**
 * @see FromSolutionPropertyValueSelector
 */
public interface EntityIndependentValueSelector<Solution_> extends ValueSelector<Solution_>,
        IterableSelector<Solution_, Object> {

}
