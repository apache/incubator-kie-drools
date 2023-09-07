/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.AbstractNearbyDistanceMatrixDemand;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;

/**
 * Demands a distance matrix where both the origins and nearby destinations are planning values.
 *
 * @param <Solution_>
 * @param <Origin_> planning values
 * @param <Destination_> planning values
 */
final class ListValueNearbyDistanceMatrixDemand<Solution_, Origin_, Destination_>
        extends
        AbstractNearbyDistanceMatrixDemand<Origin_, Destination_, EntityIndependentValueSelector<Solution_>, MimicReplayingValueSelector<Solution_>> {

    private final ToIntFunction<Origin_> destinationSizeFunction;

    public ListValueNearbyDistanceMatrixDemand(
            NearbyDistanceMeter<Origin_, Destination_> meter,
            NearbyRandom random,
            EntityIndependentValueSelector<Solution_> childValueSelector,
            MimicReplayingValueSelector<Solution_> replayingOriginValueSelector,
            ToIntFunction<Origin_> destinationSizeFunction) {
        super(meter, random, childValueSelector, replayingOriginValueSelector);
        this.destinationSizeFunction = destinationSizeFunction;
    }

    @Override
    protected NearbyDistanceMatrix<Origin_, Destination_> supplyNearbyDistanceMatrix() {
        final long childSize = childSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childSize (" + childSize + ") is higher than Integer.MAX_VALUE.");
        }

        long originSize = replayingSelector.getSize();
        if (originSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The originValueSelector (" + replayingSelector
                    + ") has a valueSize (" + originSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        // Destinations: values extracted from a value selector.
        // List variables use entity independent value selectors, so we can pass null to get and ending iterator.
        Function<Origin_, Iterator<Destination_>> destinationIteratorProvider =
                origin -> (Iterator<Destination_>) childSelector.endingIterator(null);
        NearbyDistanceMatrix<Origin_, Destination_> nearbyDistanceMatrix =
                new NearbyDistanceMatrix<>(meter, (int) originSize, destinationIteratorProvider, destinationSizeFunction);
        // Origins: values extracted from a value selector.
        // Replaying selector's ending iterator uses the recording selector's ending iterator. So, again, null is OK here.
        replayingSelector.endingIterator(null)
                .forEachRemaining(origin -> nearbyDistanceMatrix.addAllDestinations((Origin_) origin));
        return nearbyDistanceMatrix;
    }

}
