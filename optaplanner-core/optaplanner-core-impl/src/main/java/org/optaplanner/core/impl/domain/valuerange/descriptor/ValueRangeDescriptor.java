package org.optaplanner.core.impl.domain.valuerange.descriptor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface ValueRangeDescriptor<Solution_> {

    /**
     * @return never null
     */
    GenuineVariableDescriptor<Solution_> getVariableDescriptor();

    /**
     * @return true if the {@link ValueRange} is countable
     *         (for example a double value range between 1.2 and 1.4 is not countable)
     */
    boolean isCountable();

    /**
     * If this method return true, this instance is safe to cast to {@link EntityIndependentValueRangeDescriptor},
     * otherwise it requires an entity to determine the {@link ValueRange}.
     *
     * @return true if the {@link ValueRange} is the same for all entities of the same solution
     */
    boolean isEntityIndependent();

    /**
     * @return true if the {@link ValueRange} might contain a planning entity instance
     *         (not necessarily of the same entity class as this entity class of this descriptor.
     */
    boolean mightContainEntity();

    /**
     * @param solution never null
     * @param entity never null. To avoid this parameter,
     *        use {@link EntityIndependentValueRangeDescriptor#extractValueRange} instead.
     * @return never null
     */
    ValueRange<?> extractValueRange(Solution_ solution, Object entity);

}
