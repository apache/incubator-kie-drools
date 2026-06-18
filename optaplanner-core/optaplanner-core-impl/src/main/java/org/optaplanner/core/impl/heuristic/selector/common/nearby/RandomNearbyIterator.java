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

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Iterator;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;

public final class RandomNearbyIterator extends SelectionIterator<Object> {

    private final NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix;
    private final NearbyRandom nearbyRandom;
    private final Random workingRandom;
    private final Iterator<Object> replayingIterator;
    private final int nearbySize;
    private final boolean discardNearbyIndexZero;

    private Object origin;

    public RandomNearbyIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix, NearbyRandom nearbyRandom,
            Random workingRandom, Iterator<Object> replayingIterator, long childSize, boolean discardNearbyIndexZero) {
        this.nearbyDistanceMatrix = nearbyDistanceMatrix;
        this.nearbyRandom = nearbyRandom;
        this.workingRandom = workingRandom;
        this.replayingIterator = replayingIterator;
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The valueSelector (" + this
                    + ") has an entitySize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        this.nearbySize = (int) childSize - (discardNearbyIndexZero ? 1 : 0);
        this.discardNearbyIndexZero = discardNearbyIndexZero;
    }

    @Override
    public boolean hasNext() {
        return (origin != null || replayingIterator.hasNext()) && nearbySize > 0;
    }

    @Override
    public Object next() {
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         * If next() on the original recording iterator is not called, origin value from the previous call is stored and used
         * instead.
         * It enables to iterate over multiple nearby entities.
         */
        if (replayingIterator.hasNext()) {
            origin = replayingIterator.next();
        }
        int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
        if (discardNearbyIndexZero) {
            nearbyIndex++;
        }
        return nearbyDistanceMatrix.getDestination(origin, nearbyIndex);
    }

}
