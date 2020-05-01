/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.valuerange.descriptor.EntityIndependentValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * This is the common {@link ValueSelector} implementation.
 */
public class FromSolutionPropertyValueSelector extends AbstractValueSelector
        implements EntityIndependentValueSelector {

    protected final EntityIndependentValueRangeDescriptor valueRangeDescriptor;
    protected final SelectionCacheType minimumCacheType;
    protected final boolean randomSelection;
    protected final boolean valueRangeMightContainEntity;

    protected ValueRange<Object> cachedValueRange = null;
    protected Long cachedEntityListRevision = null;
    protected boolean cachedEntityListIsDirty = false;

    public FromSolutionPropertyValueSelector(EntityIndependentValueRangeDescriptor valueRangeDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        this.valueRangeDescriptor = valueRangeDescriptor;
        this.minimumCacheType = minimumCacheType;
        this.randomSelection = randomSelection;
        valueRangeMightContainEntity = valueRangeDescriptor.mightContainEntity();
    }

    @Override
    public GenuineVariableDescriptor getVariableDescriptor() {
        return valueRangeDescriptor.getVariableDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        SelectionCacheType intrinsicCacheType = valueRangeMightContainEntity
                ? SelectionCacheType.STEP
                : SelectionCacheType.PHASE;
        return (intrinsicCacheType.compareTo(minimumCacheType) > 0)
                ? intrinsicCacheType
                : minimumCacheType;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        InnerScoreDirector scoreDirector = phaseScope.getScoreDirector();
        cachedValueRange = (ValueRange<Object>) valueRangeDescriptor.extractValueRange(scoreDirector.getWorkingSolution());
        if (valueRangeMightContainEntity) {
            cachedEntityListRevision = scoreDirector.getWorkingEntityListRevision();
            cachedEntityListIsDirty = false;
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        if (valueRangeMightContainEntity) {
            InnerScoreDirector scoreDirector = stepScope.getScoreDirector();
            if (scoreDirector.isWorkingEntityListDirty(cachedEntityListRevision)) {
                if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
                    cachedEntityListIsDirty = true;
                } else {
                    cachedValueRange = (ValueRange<Object>) valueRangeDescriptor
                            .extractValueRange(scoreDirector.getWorkingSolution());
                    cachedEntityListRevision = scoreDirector.getWorkingEntityListRevision();
                }
            }
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        cachedValueRange = null;
        if (valueRangeMightContainEntity) {
            cachedEntityListRevision = null;
            cachedEntityListIsDirty = false;
        }
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
        return getSize();
    }

    @Override
    public long getSize() {
        return ((CountableValueRange<?>) cachedValueRange).getSize();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        checkCachedEntityListIsDirty();
        if (!randomSelection) {
            return ((CountableValueRange<Object>) cachedValueRange).createOriginalIterator();
        } else {
            return cachedValueRange.createRandomIterator(workingRandom);
        }
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return endingIterator();
    }

    public Iterator<Object> endingIterator() {
        return ((CountableValueRange<Object>) cachedValueRange).createOriginalIterator();
    }

    private void checkCachedEntityListIsDirty() {
        if (cachedEntityListIsDirty) {
            throw new IllegalStateException("The selector (" + this + ") with minimumCacheType (" + minimumCacheType
                    + ")'s workingEntityList became dirty between steps but is still used afterwards.");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getVariableDescriptor().getVariableName() + ")";
    }

}
