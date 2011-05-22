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

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TrailerRoutingLocation")
public class TrailerRoutingLocation extends AbstractPersistable implements Comparable<TrailerRoutingLocation> {

    private String name;
    private TrailerRoutingLocationType locationType;
    // In minutes from midnight
    private int openingTimeInMinutes;
    // In minutes from midnight
    private int closingTimeInMinutes;

    private Map<TrailerRoutingLocation, TrailerRoutingRoute> routeMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrailerRoutingLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(TrailerRoutingLocationType locationType) {
        this.locationType = locationType;
    }

    public int getOpeningTimeInMinutes() {
        return openingTimeInMinutes;
    }

    public void setOpeningTimeInMinutes(int openingTimeInMinutes) {
        this.openingTimeInMinutes = openingTimeInMinutes;
    }

    public int getClosingTimeInMinutes() {
        return closingTimeInMinutes;
    }

    public void setClosingTimeInMinutes(int closingTimeInMinutes) {
        this.closingTimeInMinutes = closingTimeInMinutes;
    }

    public Map<TrailerRoutingLocation, TrailerRoutingRoute> getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(Map<TrailerRoutingLocation, TrailerRoutingRoute> routeMap) {
        this.routeMap = routeMap;
    }

    public TrailerRoutingRoute getRouteTo(TrailerRoutingLocation location) {
        return routeMap.get(location);
    }

    public int compareTo(TrailerRoutingLocation other) {
        return new CompareToBuilder()
                .append(name, other.name)
                .toComparison();
    }

    @Override
    public String toString() {
        return name;
    }

}
