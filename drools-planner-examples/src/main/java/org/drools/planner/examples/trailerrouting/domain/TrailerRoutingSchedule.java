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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TrailerRoutingSchedule")
public class TrailerRoutingSchedule extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private String name;

    private List<TrailerRoutingLocation> locationList;
    private List<TrailerRoutingResourceKind> resourceKindList;
    private List<TrailerRoutingDriver> driverList;
    private List<TrailerRoutingTruck> truckList;
    private List<TrailerRoutingTrailer> trailerList;
    private List<TrailerRoutingLocationResourceKindRejection> locationResourceKindRejectionList;
    private List<TrailerRoutingOrder> orderList;

    private List<TrailerRoutingOrderAssignment> orderAssignmentList;

    private HardAndSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TrailerRoutingLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<TrailerRoutingLocation> locationList) {
        this.locationList = locationList;
    }

    public List<TrailerRoutingResourceKind> getResourceKindList() {
        return resourceKindList;
    }

    public void setResourceKindList(List<TrailerRoutingResourceKind> resourceKindList) {
        this.resourceKindList = resourceKindList;
    }

    public List<TrailerRoutingDriver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<TrailerRoutingDriver> driverList) {
        this.driverList = driverList;
    }

    public List<TrailerRoutingTruck> getTruckList() {
        return truckList;
    }

    public void setTruckList(List<TrailerRoutingTruck> truckList) {
        this.truckList = truckList;
    }

    public List<TrailerRoutingTrailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<TrailerRoutingTrailer> trailerList) {
        this.trailerList = trailerList;
    }

    public List<TrailerRoutingLocationResourceKindRejection> getLocationResourceKindRejectionList() {
        return locationResourceKindRejectionList;
    }

    public void setLocationResourceKindRejectionList(List<TrailerRoutingLocationResourceKindRejection> locationResourceKindRejectionList) {
        this.locationResourceKindRejectionList = locationResourceKindRejectionList;
    }

    public List<TrailerRoutingOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TrailerRoutingOrder> orderList) {
        this.orderList = orderList;
    }

    public List<TrailerRoutingOrderAssignment> getOrderAssignmentList() {
        return orderAssignmentList;
    }

    public void setOrderAssignmentList(List<TrailerRoutingOrderAssignment> orderAssignmentList) {
        this.orderAssignmentList = orderAssignmentList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (orderAssignmentList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(locationList);
        facts.addAll(resourceKindList);
        facts.addAll(driverList);
        facts.addAll(truckList);
        facts.addAll(trailerList);
        facts.addAll(locationResourceKindRejectionList);
        facts.addAll(orderList);
        if (isInitialized()) {
            facts.addAll(orderAssignmentList);
        }
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #orderAssignmentList}.
     */
    public TrailerRoutingSchedule cloneSolution() {
        TrailerRoutingSchedule clone = new TrailerRoutingSchedule();
        clone.id = id;
        clone.name = name;
        clone.locationList = locationList;
        clone.resourceKindList = resourceKindList;
        clone.driverList = driverList;
        clone.truckList = truckList;
        clone.trailerList = trailerList;
        clone.locationResourceKindRejectionList = locationResourceKindRejectionList;
        clone.orderList = orderList;
        List<TrailerRoutingOrderAssignment> clonedOrderAssignmentList
                = new ArrayList<TrailerRoutingOrderAssignment>(orderAssignmentList.size());
        for (TrailerRoutingOrderAssignment orderAssignment : orderAssignmentList) {
            TrailerRoutingOrderAssignment clonedOrderAssignment = orderAssignment.clone();
            clonedOrderAssignmentList.add(clonedOrderAssignment);
        }
        clone.orderAssignmentList = clonedOrderAssignmentList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof TrailerRoutingSchedule)) {
            return false;
        } else {
            TrailerRoutingSchedule other = (TrailerRoutingSchedule) o;
            if (orderAssignmentList.size() != other.orderAssignmentList.size()) {
                return false;
            }
            for (Iterator<TrailerRoutingOrderAssignment> it = orderAssignmentList.iterator(), otherIt = other.orderAssignmentList.iterator(); it.hasNext();) {
                TrailerRoutingOrderAssignment orderAssignment = it.next();
                TrailerRoutingOrderAssignment otherOrderAssignment = otherIt.next();
                // Notice: we don't use equals()
                if (!orderAssignment.solutionEquals(otherOrderAssignment)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (TrailerRoutingOrderAssignment orderAssignment : orderAssignmentList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(orderAssignment.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
