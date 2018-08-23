/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.entity.nearby;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class NearEntityNearbyEntitySelector extends AbstractEntitySelector {

    protected final EntitySelector childEntitySelector;
    protected final EntitySelector originEntitySelector;
    protected final NearbyDistanceMeter nearbyDistanceMeter;
    protected final NearbyRandom nearbyRandom;
    protected final boolean randomSelection;
    protected final boolean discardNearbyIndexZero = true; // TODO deactivate me when appropriate

    protected NearbyDistanceMatrix nearbyDistanceMatrix = null;

    public NearEntityNearbyEntitySelector(EntitySelector childEntitySelector, EntitySelector originEntitySelector,
            NearbyDistanceMeter nearbyDistanceMeter, NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childEntitySelector = childEntitySelector;
        this.originEntitySelector = originEntitySelector;
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The entitySelector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        // TODO Remove this limitation
        if (!childEntitySelector.getEntityDescriptor().getEntityClass().isAssignableFrom(
                originEntitySelector.getEntityDescriptor().getEntityClass())) {
            throw new IllegalArgumentException("The entitySelector (" + this
                    + ") has an entityClass ("
                    +  childEntitySelector.getEntityDescriptor().getEntityClass()
                    + ") which is not a superclass of the originEntitySelector's entityClass ("
                    + originEntitySelector.getEntityDescriptor().getEntityClass() + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        phaseLifecycleSupport.addEventListener(originEntitySelector);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        // Cannot be done during solverStarted because
        super.phaseStarted(phaseScope);
        long originSize = originEntitySelector.getSize();
        if (originSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The originEntitySelector (" + originEntitySelector
                    + ") has an entitySize (" + originSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        long childSize = childEntitySelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The childEntitySelector (" + childEntitySelector
                    + ") has an entitySize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        int destinationSize = (int) childSize;
        if (randomSelection) {
            // Reduce RAM memory usage by reducing destinationSize if nearbyRandom will never select a higher value
            int overallSizeMaximum = nearbyRandom.getOverallSizeMaximum();
            if (discardNearbyIndexZero) {
                if (overallSizeMaximum != Integer.MAX_VALUE) {
                    overallSizeMaximum++;
                }
            }
            if (destinationSize > overallSizeMaximum) {
                destinationSize = overallSizeMaximum;
            }
        }
        nearbyDistanceMatrix = new NearbyDistanceMatrix(nearbyDistanceMeter, (int) originSize);
        for (Iterator originIt = originEntitySelector.endingIterator(); originIt.hasNext(); ) {
            final Object origin =  originIt.next();
            nearbyDistanceMatrix.addAllDestinations(origin, childEntitySelector.endingIterator(), destinationSize);
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        nearbyDistanceMatrix = null;
    }

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection;
    }

    @Override
    public long getSize() {
        return childEntitySelector.getSize() - (discardNearbyIndexZero ? 1 : 0);
    }

    @Override
    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return new OriginalNearbyEntityIterator(originEntitySelector.iterator(), childEntitySelector.getSize());
        } else {
            return new RandomNearbyEntityIterator(originEntitySelector.iterator(), childEntitySelector.getSize());
        }
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Object> endingIterator() {
        // TODO It should probably use nearby order
        // It must include the origin entity too
        return childEntitySelector.endingIterator();
    }

    private class OriginalNearbyEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> originEntityIterator;
        private final long childSize;
        private int nextNearbyIndex;

        public OriginalNearbyEntityIterator(Iterator<Object> originEntityIterator, long childSize) {
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
            Object next = nearbyDistanceMatrix.getDestination(origin, nextNearbyIndex);
            nextNearbyIndex++;
            return next;
        }

    }

    private class RandomNearbyEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> originEntityIterator;
        private final int nearbySize;

        public RandomNearbyEntityIterator(Iterator<Object> originEntityIterator, long childSize) {
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
            return nearbyDistanceMatrix.getDestination(origin, nearbyIndex);
        }

    }

}
