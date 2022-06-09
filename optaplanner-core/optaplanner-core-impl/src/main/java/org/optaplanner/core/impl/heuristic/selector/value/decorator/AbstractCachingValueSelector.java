package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public abstract class AbstractCachingValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements SelectionCacheLifecycleListener<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedValueList = null;

    public AbstractCachingValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector,
            SelectionCacheType cacheType) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
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

    public ValueSelector<Solution_> getChildValueSelector() {
        return childValueSelector;
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
        long childSize = childValueSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedValueList = new ArrayList<>((int) childSize);
        // TODO Fail-faster if a non FromSolutionPropertyValueSelector is used
        childValueSelector.iterator().forEachRemaining(cachedValueList::add);
        logger.trace("    Created cachedValueList: size ({}), valueSelector ({}).",
                cachedValueList.size(), this);
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        cachedValueList = null;
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
    public long getSize(Object entity) {
        return getSize();
    }

    public long getSize() {
        return cachedValueList.size();
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return endingIterator();
    }

    public Iterator<Object> endingIterator() {
        return cachedValueList.iterator();
    }

}
