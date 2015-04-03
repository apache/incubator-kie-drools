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
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CsgBusStartPoint")
public class BusStartPoint extends AbstractPersistable implements BusStandstill {

    protected Bus bus;

    // Shadow variables
    protected BusVisit nextVisit;

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public BusVisit getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(BusVisit nextVisit) {
        this.nextVisit = nextVisit;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public BusStartPoint getStartPoint() {
        return this;
    }

    public RoadLocation getLocation() {
        return bus.getDepartureLocation();
    }

    public boolean isCoach() {
        return bus instanceof Coach;
    }

    public int getSetupCost() {
        return isCoach() ? 0 : ((Shuttle) bus).getSetupCost();
    }

    @Override
    public String toString() {
        return bus.toString();
    }

}
