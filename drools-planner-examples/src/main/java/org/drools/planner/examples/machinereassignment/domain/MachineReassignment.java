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

package org.drools.planner.examples.machinereassignment.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.pas.domain.Department;
import org.drools.planner.examples.pas.domain.DepartmentSpecialism;
import org.drools.planner.examples.pas.domain.Equipment;
import org.drools.planner.examples.pas.domain.Night;
import org.drools.planner.examples.pas.domain.Patient;
import org.drools.planner.examples.pas.domain.PreferredPatientEquipment;
import org.drools.planner.examples.pas.domain.RequiredPatientEquipment;
import org.drools.planner.examples.pas.domain.Room;
import org.drools.planner.examples.pas.domain.RoomEquipment;
import org.drools.planner.examples.pas.domain.RoomSpecialism;
import org.drools.planner.examples.pas.domain.Specialism;
import org.drools.planner.examples.pas.domain.solver.AdmissionPartConflict;

@XStreamAlias("MachineReassignment")
public class MachineReassignment extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private MrGlobalPenaltyInfo globalPenaltyInfo;
    private List<MrResource> resourceList;
    private List<MrNeighborhood> neighborhoodList;
    private List<MrLocation> locationList;
    private List<MrMachine> machineList;
    private List<MrCapacity> capacityList;
    private List<MrService> serviceList;
    private List<MrProcess> processList;
    private List<MrRequirement> requirementList;
    private List<MrBalancePenalty> balancePenaltyList;

    private List<BedDesignation> bedDesignationList;

    private HardAndSoftScore score;

    public MrGlobalPenaltyInfo getGlobalPenaltyInfo() {
        return globalPenaltyInfo;
    }

    public void setGlobalPenaltyInfo(MrGlobalPenaltyInfo globalPenaltyInfo) {
        this.globalPenaltyInfo = globalPenaltyInfo;
    }

    public List<MrResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<MrResource> resourceList) {
        this.resourceList = resourceList;
    }

    public List<MrNeighborhood> getNeighborhoodList() {
        return neighborhoodList;
    }

    public void setNeighborhoodList(List<MrNeighborhood> neighborhoodList) {
        this.neighborhoodList = neighborhoodList;
    }

    public List<MrLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<MrLocation> locationList) {
        this.locationList = locationList;
    }

    public List<MrMachine> getMachineList() {
        return machineList;
    }

    public void setMachineList(List<MrMachine> machineList) {
        this.machineList = machineList;
    }

    public List<MrCapacity> getCapacityList() {
        return capacityList;
    }

    public void setCapacityList(List<MrCapacity> capacityList) {
        this.capacityList = capacityList;
    }

    public List<MrService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<MrService> serviceList) {
        this.serviceList = serviceList;
    }

    public List<MrProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<MrProcess> processList) {
        this.processList = processList;
    }

    public List<MrRequirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<MrRequirement> requirementList) {
        this.requirementList = requirementList;
    }

    public List<MrBalancePenalty> getBalancePenaltyList() {
        return balancePenaltyList;
    }

    public void setBalancePenaltyList(List<MrBalancePenalty> balancePenaltyList) {
        this.balancePenaltyList = balancePenaltyList;
    }

    @PlanningEntityCollectionProperty
    public List<BedDesignation> getBedDesignationList() {
        return bedDesignationList;
    }

    public void setBedDesignationList(List<BedDesignation> bedDesignationList) {
        this.bedDesignationList = bedDesignationList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(globalPenaltyInfo);
        facts.addAll(resourceList);
        facts.addAll(neighborhoodList);
        facts.addAll(locationList);
        facts.addAll(machineList);
        facts.addAll(capacityList);
        facts.addAll(serviceList);
        facts.addAll(processList);
        facts.addAll(requirementList);
        facts.addAll(balancePenaltyList);
        // Do not add the planning entity's (bedDesignationList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #bedDesignationList}.
     */
    public MachineReassignment cloneSolution() {
        MachineReassignment clone = new MachineReassignment();
        clone.id = id;
        clone.globalPenaltyInfo = globalPenaltyInfo;
        clone.resourceList = resourceList;
        clone.neighborhoodList = neighborhoodList;
        clone.locationList = locationList;
        clone.machineList = machineList;
        clone.capacityList = capacityList;
        clone.serviceList = serviceList;
        clone.processList = processList;
        clone.requirementList = requirementList;
        clone.balancePenaltyList = balancePenaltyList;
        List<BedDesignation> clonedBedDesignationList = new ArrayList<BedDesignation>(bedDesignationList.size());
        for (BedDesignation bedDesignation : bedDesignationList) {
            BedDesignation clonedBedDesignation = bedDesignation.clone();
            clonedBedDesignationList.add(clonedBedDesignation);
        }
        clone.bedDesignationList = clonedBedDesignationList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof MachineReassignment)) {
            return false;
        } else {
            MachineReassignment other = (MachineReassignment) o;
            if (bedDesignationList.size() != other.bedDesignationList.size()) {
                return false;
            }
            for (Iterator<BedDesignation> it = bedDesignationList.iterator(), otherIt = other.bedDesignationList.iterator(); it.hasNext();) {
                BedDesignation bedDesignation = it.next();
                BedDesignation otherBedDesignation = otherIt.next();
                // Notice: we don't use equals()
                if (!bedDesignation.solutionEquals(otherBedDesignation)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (BedDesignation bedDesignation : bedDesignationList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(bedDesignation.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
