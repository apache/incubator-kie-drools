package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

/**
 * Filters planning values based on their assigned status. The assigned status is determined using the inverse supply.
 * If the inverse entity is not null, the value is assigned, otherwise it is unassigned.
 * A subclass must implement the {@link #valueFilter(Object)} to decide whether assigned or unassigned values will be selected.
 * <p>
 * Does implement {@link EntityIndependentValueSelector} because the question whether a value is assigned or not does not depend
 * on a specific entity.
 */
abstract class AbstractInverseEntityFilteringValueSelector<Solution_>
        extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;

    protected SingletonInverseVariableSupply inverseVariableSupply;

    protected AbstractInverseEntityFilteringValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").\n"
                    + "This is not allowed because " + AbstractInverseEntityFilteringValueSelector.class.getSimpleName()
                    + " cannot decorate a never-ending child value selector.\n"
                    + "This could be a result of using random selection order (which is often the default).");
        }
        this.childValueSelector = childValueSelector;
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    protected abstract boolean valueFilter(Object value);

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        ListVariableDescriptor<Solution_> variableDescriptor =
                (ListVariableDescriptor<Solution_>) childValueSelector.getVariableDescriptor();
        inverseVariableSupply = phaseScope.getScoreDirector().getSupplyManager()
                .demand(new SingletonListInverseVariableDemand<>(variableDescriptor));
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        inverseVariableSupply = null;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        // Because !neverEnding => countable.
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        // Because the childValueSelector is not never-ending.
        return false;
    }

    @Override
    public long getSize(Object entity) {
        return getSize();
    }

    @Override
    public long getSize() {
        return streamUnassignedValues().count();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return streamUnassignedValues().iterator();
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return iterator();
    }

    private Stream<Object> streamUnassignedValues() {
        return StreamSupport.stream(childValueSelector.spliterator(), false)
                // Accept either assigned or unassigned values.
                .filter(this::valueFilter);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        AbstractInverseEntityFilteringValueSelector<?> that = (AbstractInverseEntityFilteringValueSelector<?>) other;
        return Objects.equals(childValueSelector, that.childValueSelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector);
    }
}
