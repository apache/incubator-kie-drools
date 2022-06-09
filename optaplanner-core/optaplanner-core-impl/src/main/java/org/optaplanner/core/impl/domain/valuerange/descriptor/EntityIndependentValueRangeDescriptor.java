package org.optaplanner.core.impl.domain.valuerange.descriptor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface EntityIndependentValueRangeDescriptor<Solution_> extends ValueRangeDescriptor<Solution_> {

    /**
     * As specified by {@link ValueRangeDescriptor#extractValueRange}.
     *
     * @param solution never null
     * @return never null
     * @see ValueRangeDescriptor#extractValueRange
     */
    ValueRange<?> extractValueRange(Solution_ solution);

}
