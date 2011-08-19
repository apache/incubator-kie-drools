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

package org.drools.planner.examples.traindesign.domain.solver;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.traindesign.domain.RailArc;
import org.drools.planner.examples.traindesign.domain.RailNode;

/**
 * A CrewSegment can be used multiple times on different trains.
 */
public class RailNodeShortestPath extends AbstractPersistable implements Comparable<RailNodeShortestPath> {

    private RailNode origin;
    private RailNode destination;
    private int distance; // in miles * 1000 (to avoid Double rounding errors and BigDecimal)

    private List<RailPath> railPathList;

    public RailNode getOrigin() {
        return origin;
    }

    public void setOrigin(RailNode origin) {
        this.origin = origin;
    }

    public RailNode getDestination() {
        return destination;
    }

    public void setDestination(RailNode destination) {
        this.destination = destination;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<RailPath> getRailPathList() {
        return railPathList;
    }

    public void setRailPathList(List<RailPath> railPathList) {
        this.railPathList = railPathList;
    }

    public void resetRailPathList() {
        railPathList = new ArrayList<RailPath>(2);
    }

    public void addRailPath(RailPath railPath) {
        railPathList.add(railPath);
    }

    public int compareTo(RailNodeShortestPath other) {
        // Note this is relied upon by the Dijkstra algorithm in TrainDesignSolutionImporter
        return new CompareToBuilder()
                .append(distance, other.distance)
                .append(railPathList.size(), other.railPathList.size())
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return origin + "-->" + destination;
    }

}
