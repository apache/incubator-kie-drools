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

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;

final class OriginalNearbyValueIterator extends SelectionIterator<Object> {

    private final NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix;
    private final Iterator<Object> replayingIterator;
    private final long childSize;

    private boolean originSelected = false;
    private boolean originIsNotEmpty;
    private Object origin;

    private int nextNearbyIndex;

    public OriginalNearbyValueIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            Iterator<Object> replayingIterator, long childSize, boolean discardNearbyIndexZero) {
        this.nearbyDistanceMatrix = nearbyDistanceMatrix;
        this.replayingIterator = replayingIterator;
        this.childSize = childSize;
        this.nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
    }

    private void selectOrigin() {
        if (originSelected) {
            return;
        }
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         */
        originIsNotEmpty = replayingIterator.hasNext();
        origin = replayingIterator.next();
        originSelected = true;
    }

    @Override
    public boolean hasNext() {
        selectOrigin();
        return originIsNotEmpty && nextNearbyIndex < childSize;
    }

    @Override
    public Object next() {
        selectOrigin();
        Object next = nearbyDistanceMatrix.getDestination(origin, nextNearbyIndex);
        nextNearbyIndex++;
        return next;
    }

}
