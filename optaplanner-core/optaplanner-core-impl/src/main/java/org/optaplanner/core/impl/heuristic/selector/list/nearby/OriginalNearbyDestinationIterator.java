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
import java.util.function.Function;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;

final class OriginalNearbyDestinationIterator extends SelectionIterator<ElementRef> {

    private final NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix;
    private final Iterator<?> replayingOriginValueIterator;
    private final Function<Iterator<?>, Object> originFunction;
    private final Function<Object, ElementRef> elementRefFunction;
    private final long childSize;

    private boolean originSelected = false;
    private boolean originIsNotEmpty;
    private Object origin;

    private int nextNearbyIndex;

    public OriginalNearbyDestinationIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            Iterator<?> replayingOriginValueIterator, Function<Object, ElementRef> elementRefFunction,
            long childSize) {
        this(nearbyDistanceMatrix, replayingOriginValueIterator, Iterator::next, elementRefFunction, childSize);
    }

    public OriginalNearbyDestinationIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            Iterator<?> replayingOriginValueIterator, Function<Iterator<?>, Object> originFunction,
            Function<Object, ElementRef> elementRefFunction, long childSize) {
        this.nearbyDistanceMatrix = nearbyDistanceMatrix;
        this.replayingOriginValueIterator = replayingOriginValueIterator;
        this.originFunction = originFunction;
        this.elementRefFunction = elementRefFunction;
        this.childSize = childSize;
        nextNearbyIndex = 0;
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
        originIsNotEmpty = replayingOriginValueIterator.hasNext();
        origin = originFunction.apply(replayingOriginValueIterator);
        originSelected = true;
    }

    @Override
    public boolean hasNext() {
        selectOrigin();
        return originIsNotEmpty && nextNearbyIndex < childSize;
    }

    @Override
    public ElementRef next() {
        selectOrigin();
        Object next = nearbyDistanceMatrix.getDestination(origin, nextNearbyIndex);
        nextNearbyIndex++;
        return elementRefFunction.apply(next);
    }

}
