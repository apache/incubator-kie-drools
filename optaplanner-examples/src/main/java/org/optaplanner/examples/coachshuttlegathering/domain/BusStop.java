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

package org.optaplanner.examples.coachshuttlegathering.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.DepotAngleBusStopDifficultyWeightFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.TransportTimeToHubUpdatingVariableListener;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyWeightFactoryClass = DepotAngleBusStopDifficultyWeightFactory.class)
@XStreamAlias("CsgBusStop")
public class BusStop extends AbstractPersistable implements BusOrStop, StopOrHub {

    protected String name;
    protected RoadLocation location;
    protected int passengerQuantity;
    protected int transportTimeLimit;

    // Planning variables: changes during planning, between score calculations.
    protected BusOrStop previousBusOrStop;

    // Shadow variables
    protected BusStop nextStop;
    protected Bus bus;
    protected List<Shuttle> transferShuttleList;
    protected Integer transportTimeToHub;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public RoadLocation getLocation() {
        return location;
    }

    public void setLocation(RoadLocation location) {
        this.location = location;
    }

    public int getPassengerQuantity() {
        return passengerQuantity;
    }

    public void setPassengerQuantity(int passengerQuantity) {
        this.passengerQuantity = passengerQuantity;
    }

    public int getTransportTimeLimit() {
        return transportTimeLimit;
    }

    public void setTransportTimeLimit(int transportTimeLimit) {
        this.transportTimeLimit = transportTimeLimit;
    }

    @PlanningVariable(valueRangeProviderRefs = { "coachRange", "shuttleRange",
            "stopRange" }, graphType = PlanningVariableGraphType.CHAINED)
    public BusOrStop getPreviousBusOrStop() {
        return previousBusOrStop;
    }

    public void setPreviousBusOrStop(BusOrStop previousBusOrStop) {
        this.previousBusOrStop = previousBusOrStop;
    }

    @Override
    public BusStop getNextStop() {
        return nextStop;
    }

    @Override
    public void setNextStop(BusStop nextStop) {
        this.nextStop = nextStop;
    }

    @AnchorShadowVariable(sourceVariableName = "previousBusOrStop")
    @Override
    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public List<Shuttle> getTransferShuttleList() {
        return transferShuttleList;
    }

    @Override
    public void setTransferShuttleList(List<Shuttle> transferShuttleList) {
        this.transferShuttleList = transferShuttleList;
    }

    @CustomShadowVariable(variableListenerClass = TransportTimeToHubUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "nextStop"),
            @PlanningVariableReference(variableName = "bus"),
            @PlanningVariableReference(entityClass = Shuttle.class, variableName = "destination") })
    @Override
    public Integer getTransportTimeToHub() {
        return transportTimeToHub;
    }

    public void setTransportTimeToHub(Integer transportTimeToHub) {
        this.transportTimeToHub = transportTimeToHub;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getDistanceFromPreviousCost() {
        if (previousBusOrStop == null) {
            return 0;
        }
        return getDistanceFrom(previousBusOrStop) * bus.getMileageCost();
    }

    public int getDistanceFrom(BusOrStop busOrStop) {
        return bus.getDistanceFromTo(busOrStop.getLocation(), location);
    }

    public int getDistanceToDestinationCost(StopOrHub destination) {
        return bus.getDistanceFromTo(location, destination.getLocation()) * bus.getMileageCost();
    }

    @Override
    public boolean isVisitedByCoach() {
        return bus != null && bus instanceof Coach;
    }

    public Integer getTransportTimeRemainder() {
        if (transportTimeToHub == null) {
            return null;
        }
        if (passengerQuantity <= 0) {
            return 0;
        }
        return transportTimeLimit - transportTimeToHub;
    }

    public String getTransportLabel() {
        if (passengerQuantity <= 0) {
            return null;
        }
        return (transportTimeToHub == null ? "inf" : transportTimeToHub.toString()) + "/" + transportTimeLimit;
    }

    @Override
    public String toString() {
        return name;
    }

}
