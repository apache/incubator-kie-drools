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
