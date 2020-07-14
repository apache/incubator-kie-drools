package org.acme.facilitylocation.domain;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Consumer has a demand that can be satisfied by <em>any</em> {@link Facility} with a sufficient capacity.
 * <p/>
 * Closer facilities are preferred as the distance affects travel time, signal quality, etc.
 * This requirement is expressed by the
 * {@link FacilityLocationConstraintProvider#distanceFromFacility distance from facility} constraint.
 * <p/>
 * One of the FLP's goals is to minimize total set-up cost by selecting cheaper facilities. This requirement
 * is expressed by the {@link FacilityLocationConstraintProvider#setupCost setup cost} constraint.
 */
@PlanningEntity
public class Consumer {

    // Approximate Metric Equivalents for Degrees. At the equator for longitude and for latitude anywhere,
    // the following approximations are valid: 1° = 111 km (or 60 nautical miles) 0.1° = 11.1 km.
    public static final double METERS_PER_DEGREE = 111_000;

    private long id;
    private Location location;
    private long demand;

    @PlanningVariable(valueRangeProviderRefs = "facilityRange")
    private Facility facility;

    public Consumer() {
    }

    public Consumer(long id, Location location, long demand) {
        this.id = id;
        this.location = location;
        this.demand = demand;
    }

    public boolean isAssigned() {
        return facility != null;
    }

    /**
     * Get distance from the facility.
     *
     * @return distance in meters
     */
    public long distanceFromFacility() {
        if (facility == null) {
            throw new IllegalStateException("No facility is assigned.");
        }
        double latDiff = facility.getLocation().latitude - this.location.latitude;
        double lngDiff = facility.getLocation().longitude - this.location.longitude;
        return (long) ceil(sqrt(latDiff * latDiff + lngDiff * lngDiff) * METERS_PER_DEGREE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getDemand() {
        return demand;
    }

    public void setDemand(long demand) {
        this.demand = demand;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public String toString() {
        return "Demand " + id + " (" + demand + " dem)";
    }
}
