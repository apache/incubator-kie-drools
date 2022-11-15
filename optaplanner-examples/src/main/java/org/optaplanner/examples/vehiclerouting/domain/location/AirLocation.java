package org.optaplanner.examples.vehiclerouting.domain.location;

/**
 * The cost between 2 locations is a straight line: the euclidean distance between their GPS coordinates.
 * Used with {@link DistanceType#AIR_DISTANCE}.
 */
public class AirLocation extends Location {

    public AirLocation() {
    }

    public AirLocation(long id, double latitude, double longitude) {
        super(id, latitude, longitude);
    }

    @Override
    public long getDistanceTo(Location location) {
        double distance = getAirDistanceDoubleTo(location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

}
