/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class FromEntityPropertyValueSelector<Solution_> extends AbstractValueSelector {

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
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        // type cast in order to avoid SolverLifeCycleListener and all its children needing to be generified
        workingSolution = (Solution_) phaseScope.getWorkingSolution();
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
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
        ValueRange<Object> valueRange = (ValueRange<Object>)
                valueRangeDescriptor.extractValueRange(workingSolution, entity);
        if (!randomSelection) {
            return ((CountableValueRange<Object>) valueRange).createOriginalIterator();
        } else {
            return valueRange.createRandomIterator(workingRandom);
        }
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        ValueRange<Object> valueRange = (ValueRange<Object>)
                valueRangeDescriptor.extractValueRange(workingSolution, entity);
        return ((CountableValueRange<Object>) valueRange).createOriginalIterator();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getVariableDescriptor().getVariableName() + ")";
    }

}
