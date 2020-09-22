/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.domain;

import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@XStreamAlias("CsgBus")
@XStreamInclude({
        Coach.class,
        Shuttle.class
})
public abstract class Bus extends AbstractPersistable implements BusOrStop {

    protected String name;
    protected RoadLocation departureLocation;
    protected int capacity;
    protected int mileageCost;

    // Shadow variables
    protected BusStop nextStop;
    private int passengerQuantityTotal = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoadLocation getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(RoadLocation departureLocation) {
        this.departureLocation = departureLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMileageCost() {
        return mileageCost;
    }

    public void setMileageCost(int mileageCost) {
        this.mileageCost = mileageCost;
    }

    @Override
    public BusStop getNextStop() {
        return nextStop;
    }

    @Override
    public void setNextStop(BusStop nextStop) {
        this.nextStop = nextStop;
    }

    public Integer getPassengerQuantityTotal() {
        return passengerQuantityTotal;
    }

    public void setPassengerQuantityTotal(final Integer passengerQuantityTotal) {
        this.passengerQuantityTotal = passengerQuantityTotal == null ? 0 : passengerQuantityTotal;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public abstract int getSetupCost();

    @Override
    public RoadLocation getLocation() {
        return departureLocation;
    }

    @Override
    public Bus getBus() {
        return this;
    }

    public abstract int getDistanceFromTo(RoadLocation sourceLocation, RoadLocation targetLocation);

    public abstract int getDurationFromTo(RoadLocation sourceLocation, RoadLocation targetLocation);

    public abstract StopOrHub getDestination();

    @Override
    public String toString() {
        return name;
    }

}
