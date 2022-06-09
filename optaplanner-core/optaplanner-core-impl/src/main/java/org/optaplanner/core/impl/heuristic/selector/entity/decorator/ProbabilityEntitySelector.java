package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ProbabilityEntitySelector<Solution_> extends AbstractEntitySelector<Solution_>
        implements SelectionCacheLifecycleListener<Solution_> {

    protected final EntitySelector<Solution_> childEntitySelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityEntitySelector(EntitySelector<Solution_> childEntitySelector, SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
        this.probabilityWeightFactory = probabilityWeightFactory;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with neverEnding (" + childEntitySelector.isNeverEnding() + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
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
        for (Object entity : childEntitySelector) {
            double probabilityWeight = probabilityWeightFactory.createProbabilityWeight(
                    scoreDirector, entity);
            cachedEntityMap.put(probabilityWeightOffset, entity);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        probabilityWeightTotal = -1.0;
    }

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }

    @Override
    public long getSize() {
        return cachedEntityMap.size();
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
    public ListIterator<Object> listIterator() {
        throw new IllegalStateException("The selector (" + this
                + ") does not support a ListIterator with randomSelection (true).");
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        throw new IllegalStateException("The selector (" + this
                + ") does not support a ListIterator with randomSelection (true).");
    }

    @Override
    public Iterator<Object> endingIterator() {
        return childEntitySelector.endingIterator();
    }

    @Override
    public String toString() {
        return "Probability(" + childEntitySelector + ")";
    }

}
