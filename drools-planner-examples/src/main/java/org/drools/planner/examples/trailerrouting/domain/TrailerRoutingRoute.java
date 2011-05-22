/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.trailerrouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TrailerRoutingRoute")
public class TrailerRoutingRoute extends AbstractPersistable implements Comparable<TrailerRoutingRoute> {

    private TrailerRoutingLocation leftLocation;
    private TrailerRoutingLocation rightLocation;
    private int distanceInKilometres;
    private int timeInMinutes;

    public TrailerRoutingLocation getLeftLocation() {
        return leftLocation;
    }

    public void setLeftLocation(TrailerRoutingLocation leftLocation) {
        this.leftLocation = leftLocation;
    }

    public TrailerRoutingLocation getRightLocation() {
        return rightLocation;
    }

    public void setRightLocation(TrailerRoutingLocation rightLocation) {
        this.rightLocation = rightLocation;
    }

    public int getDistanceInKilometres() {
        return distanceInKilometres;
    }

    public void setDistanceInKilometres(int distanceInKilometres) {
        this.distanceInKilometres = distanceInKilometres;
    }

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(int timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public int compareTo(TrailerRoutingRoute other) {
        return new CompareToBuilder()
                .append(leftLocation, other.leftLocation)
                .append(rightLocation, other.rightLocation)
                .toComparison();
    }

    @Override
    public String toString() {
        return leftLocation + "<->" + rightLocation;
    }

}
