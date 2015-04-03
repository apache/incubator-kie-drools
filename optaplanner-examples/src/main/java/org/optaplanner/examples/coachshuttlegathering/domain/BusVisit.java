/*
 * Copyright 2015 JBoss Inc
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.solver.DepotAngleBusVisitDifficultyWeightFactory;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity(difficultyWeightFactoryClass = DepotAngleBusVisitDifficultyWeightFactory.class)
@XStreamAlias("CsgBusVisit")
public class BusVisit extends AbstractPersistable implements BusStandstill {

    protected BusStop busStop;

    // Planning variables: changes during planning, between score calculations.
    protected BusStandstill previousStandstill;

    // Shadow variables
    protected BusVisit nextVisit;
    protected BusStartPoint startPoint;

    public BusStop getBusStop() {
        return busStop;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }

    @PlanningVariable(valueRangeProviderRefs = {"startPointRange", "visitRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public BusStandstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(BusStandstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    public BusVisit getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(BusVisit nextVisit) {
        this.nextVisit = nextVisit;
    }

    @AnchorShadowVariable(sourceVariableName = "previousStandstill")
    public BusStartPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(BusStartPoint startPoint) {
        this.startPoint = startPoint;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public RoadLocation getLocation() {
        return busStop.getLocation();
    }

    public int getDistanceFromPreviousStandstillCost() {
        if (previousStandstill == null) {
            return 0;
        }
        return getDistanceFrom(previousStandstill) * startPoint.getBus().getMileageCost();
    }

    public int getDistanceFrom(BusStandstill standstill) {
        RoadLocation sourceLocation = standstill.getLocation();
        RoadLocation targetLocation = getLocation();
        return startPoint.isCoach() ? sourceLocation.getCoachDistanceTo(targetLocation)
                : sourceLocation.getShuttleDistanceTo(targetLocation);
    }

    @Override
    public String toString() {
        return busStop.toString();
    }

}
