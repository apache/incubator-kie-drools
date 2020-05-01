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

package org.optaplanner.examples.coachshuttlegathering.domain.location;

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CsgRoadLocation")
public class RoadLocation extends AbstractPersistable {

    protected double latitude;
    protected double longitude;

    protected Map<RoadLocation, RoadLocationArc> travelDistanceMap;

    public RoadLocation() {
    }

    public RoadLocation(long id, double latitude, double longitude) {
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<RoadLocation, RoadLocationArc> getTravelDistanceMap() {
        return travelDistanceMap;
    }

    public void setTravelDistanceMap(Map<RoadLocation, RoadLocationArc> travelDistanceMap) {
        this.travelDistanceMap = travelDistanceMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getCoachDistanceTo(RoadLocation location) {
        return travelDistanceMap.get(location).getCoachDistance();
    }

    public int getCoachDurationTo(RoadLocation location) {
        return travelDistanceMap.get(location).getCoachDuration();
    }

    public int getShuttleDistanceTo(RoadLocation location) {
        return travelDistanceMap.get(location).getShuttleDistance();
    }

    public int getShuttleDurationTo(RoadLocation location) {
        return travelDistanceMap.get(location).getShuttleDuration();
    }

    public int getMaximumDistanceTo(RoadLocation location) {
        RoadLocationArc locationArc = travelDistanceMap.get(location);
        return Math.max(locationArc.getCoachDistance(), locationArc.getShuttleDistance());
    }

    public double getAirDistanceDouble(RoadLocation location) {
        // Implementation specified by TSPLIB http://www2.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a sphere
        double latitudeDifference = location.latitude - latitude;
        double longitudeDifference = location.longitude - longitude;
        return Math.sqrt(
                (latitudeDifference * latitudeDifference) + (longitudeDifference * longitudeDifference));
    }

    /**
     * The angle relative to the direction EAST.
     *
     * @param location never null
     * @return in Cartesian coordinates
     */
    public double getAngle(RoadLocation location) {
        // Euclidean distance (Pythagorean theorem) - not correct when the surface is a sphere
        double latitudeDifference = location.latitude - latitude;
        double longitudeDifference = location.longitude - longitude;
        return Math.atan2(latitudeDifference, longitudeDifference);
    }

}
