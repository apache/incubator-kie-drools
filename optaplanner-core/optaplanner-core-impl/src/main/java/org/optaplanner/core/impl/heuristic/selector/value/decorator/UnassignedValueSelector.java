/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.Spliterators;
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
 * Discards planning values that are already assigned to a list variable.
 * <p>
 * Only returns values from the child value selector that are unassigned.
 * This prevents reassigning of values that are already assigned to a list variable during Construction Heuristics.
 * <p>
 * Does implement {@link EntityIndependentValueSelector} because the question whether a value is assigned or not does not depend
 * on a specific entity.
 */
public class UnassignedValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;

    protected SingletonInverseVariableSupply inverseVariableSupply;

    public UnassignedValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").\n"
                    + "This is not allowed because " + UnassignedValueSelector.class.getSimpleName()
                    + " cannot decorate a never-ending child value selector.\n"
                    + "This could be a result of using random selection order (which is often the default).");
        }
        this.childValueSelector = childValueSelector;
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

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
        return StreamSupport
                .stream(Spliterators.spliterator(childValueSelector.iterator(), childValueSelector.getSize(), 0), false)
                // Skip assigned values.
                .filter(value -> inverseVariableSupply.getInverseSingleton(value) == null);
    }

    @Override
    public String toString() {
        return "Unassigned(" + childValueSelector + ")";
    }
}
