package org.optaplanner.examples.tsp.domain.location;

public enum DistanceType {
    /**
     * Requires that all {@link Location} instances are of type {@link AirLocation}.
     */
    AIR_DISTANCE,
    /**
     * Requires that all {@link Location} instances are of type {@link RoadLocation}.
     */
    ROAD_DISTANCE;
}
