/*
 * Copyright 2014 JBoss Inc
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
    public int getDistance(Location location) {
        if (location instanceof RoadSegmentLocation) {
            return getDistanceToRoadLocation((RoadSegmentLocation) location);
        } else {
            return getDistanceToHubLocation((HubSegmentLocation) location);
        }
    }

    public int getDistanceToRoadLocation(RoadSegmentLocation location) {
        Double distance = nearbyTravelDistanceMap.get(location);
        if (distance == null) {
            // location isn't nearby
            distance = getShortestDistanceThroughHubs(location);
        }
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (int) (distance * 1000.0 + 0.5);
    }

    protected double getShortestDistanceThroughHubs(RoadSegmentLocation location) {
        double shortestDistance = Double.MAX_VALUE;
        for (HubSegmentLocation otherHub : location.getHubTravelDistanceMap().keySet()) {
            double distance = (this == otherHub ? 0.0 : hubTravelDistanceMap.get(otherHub))
                    + otherHub.nearbyTravelDistanceMap.get(location);
            if (distance < shortestDistance) {
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

    public int getDistanceToHubLocation(HubSegmentLocation location) {
        Double distance = hubTravelDistanceMap.get(location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (int) (distance * 1000.0 + 0.5);
    }

}
