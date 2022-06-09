package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class NearbyDistanceMatrix<Origin, Destination> {

    private final NearbyDistanceMeter<Origin, Destination> nearbyDistanceMeter;
    private final Map<Origin, Destination[]> originToDestinationsMap;
    private final Function<Origin, Iterator<Destination>> destinationIteratorProvider;
    private final ToIntFunction<Origin> destinationSizeFunction;

    public NearbyDistanceMatrix(NearbyDistanceMeter<Origin, Destination> nearbyDistanceMeter, int originSize,
            Function<Origin, Iterator<Destination>> destinationIteratorProvider,
            ToIntFunction<Origin> destinationSizeFunction) {
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        originToDestinationsMap = new HashMap<>(originSize);
        this.destinationIteratorProvider = destinationIteratorProvider;
        this.destinationSizeFunction = destinationSizeFunction;
    }

    public void addAllDestinations(Origin origin) {
        int destinationSize = destinationSizeFunction.applyAsInt(origin);
        Destination[] destinations = (Destination[]) new Object[destinationSize];
        double[] distances = new double[destinationSize];
        Iterator<Destination> destinationIterator = destinationIteratorProvider.apply(origin);
        int size = 0;
        double highestDistance = Double.MAX_VALUE;
        while (destinationIterator.hasNext()) {
            Destination destination = destinationIterator.next();
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

    public Object getDestination(Origin origin, int nearbyIndex) {
        Destination[] destinations = originToDestinationsMap.get(origin);
        if (destinations == null) {
            /*
             * The item may be missing in the distance matrix due to an underlying filtering selector.
             * In such a case, the distance matrix needs to be updated.
             */
            addAllDestinations(origin);
            destinations = originToDestinationsMap.get(origin);
        }
        return destinations[nearbyIndex];
    }

}
