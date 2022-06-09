package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ProbabilityValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_>, SelectionCacheLifecycleListener<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector,
            SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        this.probabilityWeightFactory = probabilityWeightFactory;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(childValueSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        phaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge<>(cacheType, this));
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(SolverScope<Solution_> solverScope) {
        cachedEntityMap = new TreeMap<>();
        ScoreDirector<Solution_> scoreDirector = solverScope.getScoreDirector();
        double probabilityWeightOffset = 0L;
        // TODO Fail-faster if a non FromSolutionPropertyValueSelector is used
        for (Object value : childValueSelector) {
            double probabilityWeight = probabilityWeightFactory.createProbabilityWeight(scoreDirector, value);
            cachedEntityMap.put(probabilityWeightOffset, value);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        probabilityWeightTotal = -1.0;
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public long getSize(Object entity) {
        return getSize();
    }

    @Override
    public long getSize() {
        return cachedEntityMap.size();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Object next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Object> entry = cachedEntityMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("The optional operation remove() is not supported.");
            }
        };
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return childValueSelector.endingIterator(entity);
    }

    @Override
    public String toString() {
        return "Probability(" + childValueSelector + ")";
    }

}
