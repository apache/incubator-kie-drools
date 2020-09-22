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
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.CoachPassengerCountTotalUpdatingVariableListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CsgCoach")
@PlanningEntity
public class Coach extends Bus {

    protected int stopLimit;
    protected BusHub destination;

    public int getStopLimit() {
        return stopLimit;
    }

    public void setStopLimit(int stopLimit) {
        this.stopLimit = stopLimit;
    }

    public void setDestination(BusHub destination) {
        this.destination = destination;
    }

    @Override
    @CustomShadowVariable(variableListenerClass = CoachPassengerCountTotalUpdatingVariableListener.class,
            sources = { @PlanningVariableReference(entityClass = BusStop.class, variableName = "bus") })
    public Integer getPassengerQuantityTotal() {
        return super.getPassengerQuantityTotal();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public int getSetupCost() {
        return 0;
    }

    @Override
    public int getDistanceFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getCoachDistanceTo(targetLocation);
    }

    @Override
    public int getDurationFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getCoachDurationTo(targetLocation);
    }

    @Override
    public StopOrHub getDestination() {
        return destination;
    }

    public int getDistanceToDestinationCost() {
        return getDistanceFromTo(departureLocation, destination.getLocation()) * getMileageCost();
    }

}
