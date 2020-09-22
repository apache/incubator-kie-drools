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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.DepotAngleBusStopDifficultyWeightFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.ShuttlePassengerCountTotalUpdatingVariableListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyWeightFactoryClass = DepotAngleBusStopDifficultyWeightFactory.class)
@XStreamAlias("CsgShuttle")
public class Shuttle extends Bus {

    protected int setupCost;

    // Planning variables: changes during planning, between score calculations.
    protected StopOrHub destination;

    @Override
    public int getSetupCost() {
        return setupCost;
    }

    public void setSetupCost(int setupCost) {
        this.setupCost = setupCost;
    }

    @Override
    @PlanningVariable(valueRangeProviderRefs = { "stopRange", "hubRange" })
    public StopOrHub getDestination() {
        return destination;
    }

    public void setDestination(StopOrHub destination) {
        this.destination = destination;
    }

    @Override
    @CustomShadowVariable(variableListenerClass = ShuttlePassengerCountTotalUpdatingVariableListener.class,
            sources = { @PlanningVariableReference(entityClass = BusStop.class, variableName = "bus"),
                    @PlanningVariableReference(entityClass = Shuttle.class, variableName = "destination") })
    public Integer getPassengerQuantityTotal() {
        return super.getPassengerQuantityTotal();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public int getDistanceFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getShuttleDistanceTo(targetLocation);
    }

    @Override
    public int getDurationFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getShuttleDurationTo(targetLocation);
    }

    public Bus getDestinationBus() {
        if (destination == null) {
            return null;
        }
        if (!(destination instanceof BusStop)) {
            return null;
        }
        return ((BusStop) destination).getBus();
    }

}
