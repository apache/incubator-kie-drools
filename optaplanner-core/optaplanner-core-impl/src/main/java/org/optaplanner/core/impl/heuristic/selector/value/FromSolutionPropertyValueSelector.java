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
public class FromSolutionPropertyValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    protected final EntityIndependentValueRangeDescriptor<Solution_> valueRangeDescriptor;
    protected final SelectionCacheType minimumCacheType;
    protected final boolean randomSelection;
    protected final boolean valueRangeMightContainEntity;

    protected ValueRange<Object> cachedValueRange = null;
    protected Long cachedEntityListRevision = null;
    protected boolean cachedEntityListIsDirty = false;

    public FromSolutionPropertyValueSelector(EntityIndependentValueRangeDescriptor<Solution_> valueRangeDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        this.valueRangeDescriptor = valueRangeDescriptor;
        this.minimumCacheType = minimumCacheType;
        this.randomSelection = randomSelection;
        valueRangeMightContainEntity = valueRangeDescriptor.mightContainEntity();
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
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
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        InnerScoreDirector<Solution_, ?> scoreDirector = phaseScope.getScoreDirector();
        cachedValueRange = (ValueRange<Object>) valueRangeDescriptor.extractValueRange(scoreDirector.getWorkingSolution());
        if (valueRangeMightContainEntity) {
            cachedEntityListRevision = scoreDirector.getWorkingEntityListRevision();
            cachedEntityListIsDirty = false;
        }
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        if (valueRangeMightContainEntity) {
            InnerScoreDirector<Solution_, ?> scoreDirector = stepScope.getScoreDirector();
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
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
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
