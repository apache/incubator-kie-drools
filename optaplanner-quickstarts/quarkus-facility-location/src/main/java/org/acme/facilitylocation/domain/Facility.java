package org.acme.facilitylocation.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

/**
 * Facility satisfies consumers' demand. Cumulative demand of all consumers assigned to this facility must not exceed
 * the facility's capacity. This requirement is expressed by the {@link FacilityLocationConstraintProvider#facilityCapacity
 * facility capacity} constraint.
 */
// This is a shadow planning entity, not a genuine planning entity, because it has a shadow variable (consumers).
@PlanningEntity
public class Facility {

    private long id;
    private Location location;
    private long setupCost;
    private long capacity;

    @InverseRelationShadowVariable(sourceVariableName = "facility")
    private List<Consumer> consumers = new ArrayList<>();

    public Facility() {
    }

    public Facility(long id, Location location, long setupCost, long capacity) {
        this.id = id;
        this.location = location;
        this.setupCost = setupCost;
        this.capacity = capacity;
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

    public long getSetupCost() {
        return setupCost;
    }

    public void setSetupCost(long setupCost) {
        this.setupCost = setupCost;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getUsedCapacity() {
        return consumers.stream().mapToLong(Consumer::getDemand).sum();
    }

    public boolean isUsed() {
        return !consumers.isEmpty();
    }

    @Override
    public String toString() {
        return "Facility " + id +
                " ($" + setupCost
                + ", " + capacity + " cap)";
    }
}
