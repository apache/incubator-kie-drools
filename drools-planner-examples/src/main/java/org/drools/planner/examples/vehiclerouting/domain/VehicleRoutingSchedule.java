/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("VehicleRoutingSchedule")
public class VehicleRoutingSchedule extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private String name;
    private List<VrpLocation> locationList;
    private List<VrpVehicle> vehicleList;

    private List<VrpCustomer> customerList;

    private HardAndSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VrpLocation> getVrpLocationList() {
        return locationList;
    }

    public void setVrpLocationList(List<VrpLocation> locationList) {
        this.locationList = locationList;
    }

    public List<VrpVehicle> getVrpVehicleList() {
        return vehicleList;
    }

    public void setVrpVehicleList(List<VrpVehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @PlanningEntityCollectionProperty
    public List<VrpCustomer> getVrpCustomerList() {
        return customerList;
    }

    public void setVrpCustomerList(List<VrpCustomer> customerList) {
        this.customerList = customerList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(locationList);
        facts.addAll(vehicleList);
        // Do not add the planning entity's (customerList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #customerList}.
     */
    public VehicleRoutingSchedule cloneSolution() {
        VehicleRoutingSchedule clone = new VehicleRoutingSchedule();
        clone.id = id;
        clone.name = name;
        clone.locationList = locationList;
        clone.vehicleList = vehicleList;
        List<VrpCustomer> clonedVrpCustomerList = new ArrayList<VrpCustomer>(customerList.size());
        Map<Long, VrpCustomer> idToClonedVrpCustomerMap = new HashMap<Long, VrpCustomer>(
                customerList.size());
        for (VrpCustomer customer : customerList) {
            VrpCustomer clonedVrpCustomer = customer.clone();
            clonedVrpCustomerList.add(clonedVrpCustomer);
            idToClonedVrpCustomerMap.put(clonedVrpCustomer.getId(), clonedVrpCustomer);
        }
        // Fix: Previous should point to the new clones instead of the old instances
        for (VrpCustomer clonedVrpCustomer : clonedVrpCustomerList) {
            VrpAppearance previousVrpAppearance = clonedVrpCustomer.getPreviousAppearance();
            if (previousVrpAppearance instanceof VrpCustomer) {
                Long previousVrpCustomerId = ((VrpCustomer) previousVrpAppearance).getId();
                clonedVrpCustomer.setPreviousAppearance(idToClonedVrpCustomerMap.get(previousVrpCustomerId));
            }
        }
        clone.customerList = clonedVrpCustomerList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof VehicleRoutingSchedule)) {
            return false;
        } else {
            VehicleRoutingSchedule other = (VehicleRoutingSchedule) o;
            if (customerList.size() != other.customerList.size()) {
                return false;
            }
            for (Iterator<VrpCustomer> it = customerList.iterator(), otherIt = other.customerList.iterator(); it.hasNext();) {
                VrpCustomer customer = it.next();
                VrpCustomer otherVrpCustomer = otherIt.next();
                // Notice: we don't use equals()
                if (!customer.solutionEquals(otherVrpCustomer)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (VrpCustomer customer : customerList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(customer.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
