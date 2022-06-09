package org.optaplanner.core.impl.heuristic.selector.value;

import java.util.Iterator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

/**
 * This is the common {@link ValueSelector} implementation.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class FromEntityPropertyValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    protected final ValueRangeDescriptor<Solution_> valueRangeDescriptor;
    protected final boolean randomSelection;

    protected Solution_ workingSolution;

    public FromEntityPropertyValueSelector(ValueRangeDescriptor<Solution_> valueRangeDescriptor, boolean randomSelection) {
        this.valueRangeDescriptor = valueRangeDescriptor;
        this.randomSelection = randomSelection;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return valueRangeDescriptor.getVariableDescriptor();
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // type cast in order to avoid SolverLifeCycleListener and all its children needing to be generified
        workingSolution = phaseScope.getWorkingSolution();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        workingSolution = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return valueRangeDescriptor.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    @Override
    public long getSize(Object entity) {
        ValueRange<?> valueRange = valueRangeDescriptor.extractValueRange(workingSolution, entity);
        return ((CountableValueRange<?>) valueRange).getSize();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        ValueRange<Object> valueRange = (ValueRange<Object>) valueRangeDescriptor.extractValueRange(workingSolution, entity);
        if (!randomSelection) {
            return ((CountableValueRange<Object>) valueRange).createOriginalIterator();
        } else {
            return valueRange.createRandomIterator(workingRandom);
        }
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        ValueRange<Object> valueRange = (ValueRange<Object>) valueRangeDescriptor.extractValueRange(workingSolution, entity);
        return ((CountableValueRange<Object>) valueRange).createOriginalIterator();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getVariableDescriptor().getVariableName() + ")";
    }

}
