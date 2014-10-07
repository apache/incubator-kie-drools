/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearEntityNearbyMethod;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.solution.WorkingSolutionAware;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class NearEntityNearbyValueSelector extends AbstractValueSelector {

    protected final ValueSelector childValueSelector;
    protected final EntitySelector originEntitySelector;
    protected final NearEntityNearbyMethod nearEntityNearbyMethod;
    protected final NearbyRandom nearbyRandom;
    protected final boolean randomSelection;
    protected final boolean discardNearbyIndexZero = true;// TODO deactivate me when appropriate

    public NearEntityNearbyValueSelector(ValueSelector childValueSelector, EntitySelector originEntitySelector,
            NearEntityNearbyMethod nearEntityNearbyMethod, NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childValueSelector = childValueSelector;
        this.originEntitySelector = originEntitySelector;
        this.nearEntityNearbyMethod = nearEntityNearbyMethod;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        // TODO Remove this limitation
        if (!childValueSelector.getVariableDescriptor().getVariablePropertyType().isAssignableFrom(
                originEntitySelector.getEntityDescriptor().getEntityClass())) {
            throw new IllegalArgumentException("The valueSelector (" + this
                    + ") has a variable property type ("
                    +  childValueSelector.getVariableDescriptor().getVariablePropertyType()
                    + ") which is not a superclass of the originEntitySelector's entityClass ("
                    + originEntitySelector.getEntityDescriptor().getEntityClass() + ").");
        }
        phaseLifecycleSupport.addEventListener(childValueSelector);
        phaseLifecycleSupport.addEventListener(originEntitySelector);
    }

    @Override
    public GenuineVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        if (nearEntityNearbyMethod instanceof WorkingSolutionAware) {
            ((WorkingSolutionAware) nearEntityNearbyMethod).setWorkingSolution(phaseScope.getWorkingSolution());
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        if (nearEntityNearbyMethod instanceof WorkingSolutionAware) {
            ((WorkingSolutionAware) nearEntityNearbyMethod).unsetWorkingSolution();
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    public boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    public long getSize(Object entity) {
        return childValueSelector.getSize(entity) - (discardNearbyIndexZero ? 1 : 0);
    }

    public Iterator<Object> iterator(Object entity) {
        if (!randomSelection) {
            return new OriginalEntityNearbyValueIterator(
                    originEntitySelector.iterator(), childValueSelector.getSize(entity));
        } else {
            return new RandomEntityNearbyValueIterator(
                    originEntitySelector.iterator(), childValueSelector.getSize(entity));
        }
    }

    private class OriginalEntityNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> originEntityIterator;
        private final long childSize;
        private int nextNearbyIndex;

        public OriginalEntityNearbyValueIterator(Iterator<Object> originEntityIterator, long childSize) {
            this.originEntityIterator = originEntityIterator;
            this.childSize = childSize;
            nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
        }

        @Override
        public boolean hasNext() {
            return originEntityIterator.hasNext() && nextNearbyIndex < childSize;
        }

        @Override
        public Object next() {
            Object origin = originEntityIterator.next();
            // TODO if origin != entity, it doesn't know from which entity value range it can pick
            Object next = nearEntityNearbyMethod.getByNearbyIndex(origin, nextNearbyIndex);
            nextNearbyIndex++;
            return next;
        }

    }

    private class RandomEntityNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> originEntityIterator;
        private final int nearbySize;

        public RandomEntityNearbyValueIterator(Iterator<Object> originEntityIterator, long childSize) {
            this.originEntityIterator = originEntityIterator;
            if (childSize > (long) Integer.MAX_VALUE) {
                throw new IllegalStateException("The valueSelector (" + this
                        + ") has an entitySize (" + childSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            nearbySize = (int) childSize - (discardNearbyIndexZero ? 1 : 0);
        }

        @Override
        public boolean hasNext() {
            return originEntityIterator.hasNext() && nearbySize > 0;
        }

        @Override
        public Object next() {
            Object origin = originEntityIterator.next();
            int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
            if (discardNearbyIndexZero) {
                nearbyIndex++;
            }
            return nearEntityNearbyMethod.getByNearbyIndex(origin, nearbyIndex);
        }

    }

}
