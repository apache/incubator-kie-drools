/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class NearbyDistanceMatrix {

    private final NearbyDistanceMeter nearbyDistanceMeter;
    private final Map<Object, Object[]> originToDestinationsMap;

    public NearbyDistanceMatrix(NearbyDistanceMeter nearbyDistanceMeter, int originSize) {
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        originToDestinationsMap = new HashMap<>(originSize);
    }

    public void addAllDestinations(Object origin, Iterator<Object> destinationIterator, int destinationSize) {
        Object[] destinations = new Object[destinationSize];
        double[] distances = new double[destinationSize];
        int size = 0;
        Double highestDistance = Double.MAX_VALUE;
        while (destinationIterator.hasNext()) {
            Object destination = destinationIterator.next();
            double distance = nearbyDistanceMeter.getNearbyDistance(origin, destination);
            if (distance < highestDistance || size < destinationSize) {
                int insertIndex = Arrays.binarySearch(distances, 0, size, distance);
                if (insertIndex < 0) {
                    insertIndex = -insertIndex - 1;
                } else {
                    while (insertIndex < size && distances[insertIndex] == distance) {
                        insertIndex++;
                    }
                }
                if (size < destinationSize) {
                    size++;
                }
                System.arraycopy(destinations, insertIndex, destinations, insertIndex + 1,
                        size - insertIndex - 1);
                System.arraycopy(distances, insertIndex, distances, insertIndex + 1,
                        size - insertIndex - 1);
                destinations[insertIndex] = destination;
                distances[insertIndex] = distance;
                highestDistance = distances[size - 1];
            }
        }
        if (size != destinationSize) {
            throw new IllegalStateException("The destinationIterator's size (" + size
                    + ") differs from the expected destinationSize (" + destinationSize + ").");
        }
        originToDestinationsMap.put(origin, destinations);
    }

    public Object getDestination(Object origin, int nearbyIndex) {
        Object[] destinations = originToDestinationsMap.get(origin);
        return destinations[nearbyIndex];
    }

}
