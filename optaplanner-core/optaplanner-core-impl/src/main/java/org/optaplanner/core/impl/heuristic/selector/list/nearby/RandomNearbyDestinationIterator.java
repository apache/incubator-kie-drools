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

package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;

final class RandomNearbyDestinationIterator extends SelectionIterator<ElementRef> {

    private final NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix;
    private final NearbyRandom nearbyRandom;
    private final Random workingRandom;
    private final Iterator<?> replayingOriginValueIterator;
    private final Function<Iterator<?>, Object> originFunction;
    private final Function<Object, ElementRef> elementRefFunction;
    private final int nearbySize;

    public RandomNearbyDestinationIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            NearbyRandom nearbyRandom, Random workingRandom, Iterator<Object> replayingOriginValueIterator,
            Function<Object, ElementRef> elementRefFunction, long childSize) {
        this(nearbyDistanceMatrix, nearbyRandom, workingRandom, replayingOriginValueIterator, Iterator::next,
                elementRefFunction, childSize);
    }

    public RandomNearbyDestinationIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            NearbyRandom nearbyRandom, Random workingRandom, Iterator<?> replayingOriginValueIterator,
            Function<Iterator<?>, Object> originFunction, Function<Object, ElementRef> elementRefFunction,
            long childSize) {
        this.nearbyDistanceMatrix = nearbyDistanceMatrix;
        this.nearbyRandom = nearbyRandom;
        this.workingRandom = workingRandom;
        this.replayingOriginValueIterator = replayingOriginValueIterator;
        this.originFunction = originFunction;
        this.elementRefFunction = elementRefFunction;
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The destinationSelector (" + this
                    + ") has a destinationSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        nearbySize = (int) childSize;
    }

    @Override
    public boolean hasNext() {
        return replayingOriginValueIterator.hasNext() && nearbySize > 0;
    }

    @Override
    public ElementRef next() {
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         */
        Object origin = originFunction.apply(replayingOriginValueIterator);
        int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
        Object next = nearbyDistanceMatrix.getDestination(origin, nearbyIndex);
        return elementRefFunction.apply(next);
    }

}
