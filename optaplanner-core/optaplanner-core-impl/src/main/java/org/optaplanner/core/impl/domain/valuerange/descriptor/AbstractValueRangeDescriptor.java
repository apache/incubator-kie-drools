package org.optaplanner.core.impl.domain.valuerange.descriptor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.valuerange.buildin.composite.NullableCountableValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractValueRangeDescriptor<Solution_> implements ValueRangeDescriptor<Solution_> {

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;
    protected final boolean addNullInValueRange;

    public AbstractValueRangeDescriptor(GenuineVariableDescriptor<Solution_> variableDescriptor,
            boolean addNullInValueRange) {
        this.variableDescriptor = variableDescriptor;
        this.addNullInValueRange = addNullInValueRange;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean mightContainEntity() {
        SolutionDescriptor<Solution_> solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        Class<?> variablePropertyType = variableDescriptor.getVariablePropertyType();
        for (Class<?> entityClass : solutionDescriptor.getEntityClassSet()) {
            if (variablePropertyType.isAssignableFrom(entityClass)) {
                return true;
            }
        }
        return false;
    }

    protected <T> ValueRange<T> doNullInValueRangeWrapping(ValueRange<T> valueRange) {
        if (addNullInValueRange) {
            valueRange = new NullableCountableValueRange<>((CountableValueRange) valueRange);
        }
        return valueRange;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}
