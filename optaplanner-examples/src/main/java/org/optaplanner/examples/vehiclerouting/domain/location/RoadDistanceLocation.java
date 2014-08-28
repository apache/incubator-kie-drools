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

package org.optaplanner.examples.vehiclerouting.domain.location;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VrpRoadDistanceLocation")
public class RoadDistanceLocation extends Location {

    // Prefer Map over array or List because customers might be added and removed in real-time planning.
    protected Map<RoadDistanceLocation, Double> travelDistanceMap;

    public Map<RoadDistanceLocation, Double> getTravelDistanceMap() {
        return travelDistanceMap;
    }

    public void setTravelDistanceMap(Map<RoadDistanceLocation, Double> travelDistanceMap) {
        this.travelDistanceMap = travelDistanceMap;
    }

    @Override
    public int getDistance(Location location) {
        double distance = travelDistanceMap.get((RoadDistanceLocation) location);
        // Multiplied by 1000 to avoid floating point arithmetic rounding errors
        return (int) (distance * 1000.0 + 0.5);
    }

}
