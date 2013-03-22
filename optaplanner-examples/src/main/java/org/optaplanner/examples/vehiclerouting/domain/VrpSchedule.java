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

package org.optaplanner.examples.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("VrpSchedule")
public class VrpSchedule extends AbstractPersistable implements Solution<HardSoftScore> {

    private String name;
    private List<VrpLocation> locationList;
    private List<VrpDepot> depotList;
    private List<VrpVehicle> vehicleList;

    private List<VrpCustomer> customerList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
    private HardSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VrpLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<VrpLocation> locationList) {
        this.locationList = locationList;
    }

    public List<VrpDepot> getDepotList() {
        return depotList;
    }

    public void setDepotList(List<VrpDepot> depotList) {
        this.depotList = depotList;
    }

    public List<VrpVehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<VrpVehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @PlanningEntityCollectionProperty
    public List<VrpCustomer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<VrpCustomer> customerList) {
        this.customerList = customerList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(locationList);
        facts.addAll(depotList);
        facts.addAll(vehicleList);
        // Do not add the planning entity's (customerList) because that will be done automatically
        return facts;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof VrpSchedule)) {
            return false;
        } else {
            VrpSchedule other = (VrpSchedule) o;
            if (customerList.size() != other.customerList.size()) {
                return false;
            }
            for (Iterator<VrpCustomer> it = customerList.iterator(), otherIt = other.customerList.iterator(); it.hasNext();) {
                VrpCustomer customer = it.next();
                VrpCustomer otherCustomer = otherIt.next();
                // Notice: we don't use equals()
                if (!customer.solutionEquals(otherCustomer)) {
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
