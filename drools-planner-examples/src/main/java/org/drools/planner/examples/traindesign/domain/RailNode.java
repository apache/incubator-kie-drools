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

package org.drools.planner.examples.traindesign.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.traindesign.domain.solver.RailNodeShortestPath;
import org.drools.planner.examples.traindesign.domain.solver.RailPath;

@XStreamAlias("RailNode")
public class RailNode extends AbstractPersistable {

    private String code;
    private int blockSwapCost;

    private List<RailArc> originatingRailArcList;
    @XStreamOmitField
    private Map<RailNode, RailNodeShortestPath> shortestPathMap;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getBlockSwapCost() {
        return blockSwapCost;
    }

    public void setBlockSwapCost(int blockSwapCost) {
        this.blockSwapCost = blockSwapCost;
    }

    public List<RailArc> getOriginatingRailArcList() {
        return originatingRailArcList;
    }

    public void setOriginatingRailArcList(List<RailArc> originatingRailArcList) {
        this.originatingRailArcList = originatingRailArcList;
    }

    public Map<RailNode, RailNodeShortestPath> getShortestPathMap() {
        return shortestPathMap;
    }

    public RailNodeShortestPath getShortestPathTo(RailNode other) {
        return shortestPathMap.get(other);
    }

    public void setShortestPathMap(Map<RailNode, RailNodeShortestPath> shortestPathMap) {
        this.shortestPathMap = shortestPathMap;
    }

    @Override
    public String toString() {
        return code;
    }

    public void initializeShortestPathMap(List<RailNode> railNodeList) {
        shortestPathMap = new HashMap<RailNode, RailNodeShortestPath>(
                railNodeList.size());
        // Dijkstra algorithm
        List<RailNodeShortestPath> unvisitedShortestPathList = new ArrayList<RailNodeShortestPath>(
                railNodeList.size());

        RailNodeShortestPath originShortestPath = new RailNodeShortestPath();
        originShortestPath.setOrigin(this);
        originShortestPath.setDestination(this);
        originShortestPath.setDistance(0);
        originShortestPath.resetRailPathList();
        RailPath originRailPath = new RailPath(new ArrayList<RailArc>(0));
        originShortestPath.addRailPath(originRailPath);
        shortestPathMap.put(this, originShortestPath);
        unvisitedShortestPathList.add(originShortestPath);

        while (!unvisitedShortestPathList.isEmpty()) {
            RailNodeShortestPath campingShortestPath = unvisitedShortestPathList.remove(0);
            for (RailArc nextRailArc : campingShortestPath.getDestination().getOriginatingRailArcList()) {
                RailNode nextNode = nextRailArc.getDestination();
                int nextDistance = campingShortestPath.getDistance() + nextRailArc.getDistance();

                RailNodeShortestPath nextShortestPath = shortestPathMap.get(nextNode);
                if (nextShortestPath == null) {
                    nextShortestPath = new RailNodeShortestPath();
                    nextShortestPath.setOrigin(this);
                    nextShortestPath.setDestination(nextNode);
                    nextShortestPath.setDistance(Integer.MAX_VALUE);
                    shortestPathMap.put(nextNode, nextShortestPath);
                    unvisitedShortestPathList.add(nextShortestPath);
                }
                if (nextDistance <= nextShortestPath.getDistance()) {
                    if (nextDistance < nextShortestPath.getDistance()) {
                        nextShortestPath.setDistance(nextDistance);
                        nextShortestPath.resetRailPathList();
                    }
                    for (RailPath campingRailPath : campingShortestPath.getRailPathList()) {
                        List<RailArc> railArcList = new ArrayList<RailArc>(campingRailPath.getRailArcList());
                        railArcList.add(nextRailArc);
                        RailPath nextRailPath = new RailPath(railArcList);
                        nextShortestPath.addRailPath(nextRailPath);
                    }
                }
            }
            Collections.sort(unvisitedShortestPathList, new Comparator<RailNodeShortestPath>() {
                public int compare(RailNodeShortestPath a, RailNodeShortestPath b) {
                    return new CompareToBuilder()
                            .append(a.getDistance(), b.getDistance())
                            .append(a.getRailPathList().size(), b.getRailPathList().size())
                            .append(a.getId(), b.getId())
                            .toComparison();
                }
            });
        }
    }

}
