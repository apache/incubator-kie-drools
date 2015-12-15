/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.domain.location.segmented;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

/**
 * Assistant for {@link RoadSegmentLocation}.
 * Used with {@link DistanceType#SEGMENTED_ROAD_DISTANCE}.
 */
@XStreamAlias("VrpHubSegmentLocation")
public class HubSegmentLocation extends Location {

    // Prefer Map over array or List because customers might be added and removed in real-time planning.
    protected Map<RoadSegmentLocation, Double> nearbyTravelDistanceMap;
    protected Map<HubSegmentLocation, Double> hubTravelDistanceMap;

    public HubSegmentLocation() {
    }

    public HubSegmentLocation(long id, double latitude, double longitude) {
        super(id, latitude, longitude);
    }

    public Map<RoadSegmentLocation, Double> getNearbyTravelDistanceMap() {
        return nearbyTravelDistanceMap;
    }

    public void setNearbyTravelDistanceMap(Map<RoadSegmentLocation, Double> nearbyTravelDistanceMap) {
        this.nearbyTravelDistanceMap = nearbyTravelDistanceMap;
    }

    public Map<HubSegmentLocation, Double> getHubTravelDistanceMap() {
        return hubTravelDistanceMap;
    }

    public void setHubTravelDistanceMap(Map<HubSegmentLocation, Double> hubTravelDistanceMap) {
        this.hubTravelDistanceMap = hubTravelDistanceMap;
    }

    @Override
    public long getDistanceTo(Location location) {
        double distance;
        if (location instanceof RoadSegmentLocation) {
            distance = getDistanceDouble((RoadSegmentLocation) location);
        } else {
            distance = hubTravelDistanceMap.get((HubSegmentLocation) location);
        }
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (long) (distance * 1000.0 + 0.5);
    }

    public double getDistanceDouble(RoadSegmentLocation location) {
        Double distance = nearbyTravelDistanceMap.get(location);
        if (distance == null) {
            // location isn't nearby
            distance = getShortestDistanceDoubleThroughOtherHub(location);
        }
        return distance;
    }

    protected double getShortestDistanceDoubleThroughOtherHub(RoadSegmentLocation location) {
        double shortestDistance = Double.MAX_VALUE;
        // Don't use location.getHubTravelDistanceMap().keySet() instead because distances aren't always paired
        for (Map.Entry<HubSegmentLocation, Double> otherHubEntry : hubTravelDistanceMap.entrySet()) {
            HubSegmentLocation otherHub = otherHubEntry.getKey();
            Double otherHubNearbyDistance = otherHub.nearbyTravelDistanceMap.get(location);
            if (otherHubNearbyDistance != null) {
                double distance = otherHubEntry.getValue() + otherHubNearbyDistance;
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                }
            }
        }
        return shortestDistance;
    }

}
