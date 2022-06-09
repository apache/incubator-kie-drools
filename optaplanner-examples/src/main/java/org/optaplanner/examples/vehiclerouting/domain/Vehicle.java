package org.optaplanner.examples.vehiclerouting.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VrpVehicle")
public class Vehicle extends AbstractPersistable implements Standstill {

    protected int capacity;
    protected Depot depot;

    // Shadow variables
    protected Customer nextCustomer;

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

    @Override
    public Customer getNextCustomer() {
        return nextCustomer;
    }

    @Override
    public void setNextCustomer(Customer nextCustomer) {
        this.nextCustomer = nextCustomer;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public Vehicle getVehicle() {
        return this;
    }

    @Override
    public Location getLocation() {
        return depot.getLocation();
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceTo(Standstill standstill) {
        return depot.getDistanceTo(standstill);
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
