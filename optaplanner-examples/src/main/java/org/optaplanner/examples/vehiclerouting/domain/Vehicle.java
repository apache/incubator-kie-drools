package org.optaplanner.examples.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.examples.common.domain.AbstractPersistableJackson;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@PlanningEntity
@JsonIdentityInfo(scope = Vehicle.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vehicle extends AbstractPersistableJackson {

    protected int capacity;
    protected Depot depot;

    @PlanningListVariable(valueRangeProviderRefs = "customerRange")
    protected List<Customer> customers = new ArrayList<>();

    public Vehicle() {
    }

    public Vehicle(long id, int capacity, Depot depot) {
        super(id);
        this.capacity = capacity;
        this.depot = depot;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public Location getLocation() {
        return depot.getLocation();
    }

    @Override
    public String toString() {
        Location location = getLocation();
        if (location.getName() == null) {
            return super.toString();
        }
        return location.getName() + "/" + super.toString();
    }
}
