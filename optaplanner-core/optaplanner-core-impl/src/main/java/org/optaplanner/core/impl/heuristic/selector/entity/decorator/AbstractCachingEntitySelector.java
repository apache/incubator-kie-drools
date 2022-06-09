package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public abstract class AbstractCachingEntitySelector<Solution_> extends AbstractEntitySelector<Solution_>
        implements SelectionCacheLifecycleListener<Solution_> {

    protected final EntitySelector<Solution_> childEntitySelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedEntityList = null;

    public AbstractCachingEntitySelector(EntitySelector<Solution_> childEntitySelector, SelectionCacheType cacheType) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
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

    public EntitySelector<Solution_> getChildEntitySelector() {
        return childEntitySelector;
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
        long childSize = childEntitySelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<>((int) childSize);
        childEntitySelector.iterator().forEachRemaining(cachedEntityList::add);
        logger.trace("    Created cachedEntityList: size ({}), entitySelector ({}).",
                cachedEntityList.size(), this);
    }

    @Override
    public void disposeCache(SolverScope<Solution_> solverScope) {
        cachedEntityList = null;
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
    public long getSize() {
        return cachedEntityList.size();
    }

    @Override
    public Iterator<Object> endingIterator() {
        return cachedEntityList.iterator();
    }

}
