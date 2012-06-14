/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * This is the common {@link EntitySelector} implementation.
 */
public class FromSolutionEntitySelector extends AbstractEntitySelector implements SelectionCacheLifecycleListener {

    protected final PlanningEntityDescriptor entityDescriptor;
    protected final boolean randomSelection;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedEntityList = null;

    public FromSolutionEntitySelector(PlanningEntityDescriptor entityDescriptor, boolean randomSelection,
            SelectionCacheType cacheType) {
        this.entityDescriptor = entityDescriptor;
        this.randomSelection = randomSelection;
        this.cacheType = cacheType;
        if (cacheType != SelectionCacheType.SOLVER && cacheType != SelectionCacheType.PHASE
                && cacheType != SelectionCacheType.STEP) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedEntityList = entityDescriptor.extractEntities(solverScope.getWorkingSolution());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedEntityList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        // TODO what if entityList is still null?
        return (long) cachedEntityList.size();
    }

    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return cachedEntityList.iterator();
        } else {
            return new Iterator<Object>() {
                public boolean hasNext() {
                    return true;
                }

                public Object next() {
                    int index = workingRandom.nextInt(cachedEntityList.size());
                    return cachedEntityList.get(index);
                }

                public void remove() {
                    throw new UnsupportedOperationException("Remove is not supported.");
                }
            };
        }
    }

    @Override
    public String toString() {
        return "FromSolutionEntitySelector(" + entityDescriptor.getPlanningEntityClass().getSimpleName() + ")";
    }

}
