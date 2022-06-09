package org.optaplanner.core.impl.domain.valuerange.descriptor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class FromEntityPropertyValueRangeDescriptor<Solution_>
        extends AbstractFromPropertyValueRangeDescriptor<Solution_> {

    public FromEntityPropertyValueRangeDescriptor(GenuineVariableDescriptor<Solution_> variableDescriptor,
            boolean addNullInValueRange, MemberAccessor memberAccessor) {
        super(variableDescriptor, addNullInValueRange, memberAccessor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isEntityIndependent() {
        return false;
    }

    @Override
    public ValueRange<?> extractValueRange(Solution_ solution, Object entity) {
        return readValueRange(entity);
    }

}
