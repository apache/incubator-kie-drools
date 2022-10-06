package org.optaplanner.examples.coachshuttlegathering.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
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
    @ShadowVariable(variableListenerClass = ShuttlePassengerCountTotalUpdatingVariableListener.class,
            sourceEntityClass = BusStop.class, sourceVariableName = "bus")
    @ShadowVariable(variableListenerClass = ShuttlePassengerCountTotalUpdatingVariableListener.class,
            sourceEntityClass = Shuttle.class, sourceVariableName = "destination")
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
