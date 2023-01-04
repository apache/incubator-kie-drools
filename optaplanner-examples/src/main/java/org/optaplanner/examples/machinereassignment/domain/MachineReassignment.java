package org.optaplanner.examples.machinereassignment.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningSolution
public class MachineReassignment extends AbstractPersistable {

    private MrGlobalPenaltyInfo globalPenaltyInfo;
    private List<MrResource> resourceList;
    private List<MrNeighborhood> neighborhoodList;
    private List<MrLocation> locationList;
    private List<MrMachine> machineList;
    private List<MrMachineCapacity> machineCapacityList;
    private List<MrService> serviceList;
    private List<MrProcess> processList;
    private List<MrBalancePenalty> balancePenaltyList;

    private List<MrProcessAssignment> processAssignmentList;

    MachineReassignment() {
    }

    public MachineReassignment(long id) {
        super(id);
    }

    private HardSoftLongScore score;

    @ProblemFactProperty
    public MrGlobalPenaltyInfo getGlobalPenaltyInfo() {
        return globalPenaltyInfo;
    }

    public void setGlobalPenaltyInfo(MrGlobalPenaltyInfo globalPenaltyInfo) {
        this.globalPenaltyInfo = globalPenaltyInfo;
    }

    @ProblemFactCollectionProperty
    public List<MrResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<MrResource> resourceList) {
        this.resourceList = resourceList;
    }

    @ProblemFactCollectionProperty
    public List<MrNeighborhood> getNeighborhoodList() {
        return neighborhoodList;
    }

    public void setNeighborhoodList(List<MrNeighborhood> neighborhoodList) {
        this.neighborhoodList = neighborhoodList;
    }

    @ProblemFactCollectionProperty
    public List<MrLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<MrLocation> locationList) {
        this.locationList = locationList;
    }

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    public List<MrMachine> getMachineList() {
        return machineList;
    }

    public void setMachineList(List<MrMachine> machineList) {
        this.machineList = machineList;
    }

    @ProblemFactCollectionProperty
    public List<MrMachineCapacity> getMachineCapacityList() {
        return machineCapacityList;
    }

    public void setMachineCapacityList(List<MrMachineCapacity> machineCapacityList) {
        this.machineCapacityList = machineCapacityList;
    }

    @ProblemFactCollectionProperty
    public List<MrService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<MrService> serviceList) {
        this.serviceList = serviceList;
    }

    @ProblemFactCollectionProperty
    public List<MrProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<MrProcess> processList) {
        this.processList = processList;
    }

    @ProblemFactCollectionProperty
    public List<MrBalancePenalty> getBalancePenaltyList() {
        return balancePenaltyList;
    }

    public void setBalancePenaltyList(List<MrBalancePenalty> balancePenaltyList) {
        this.balancePenaltyList = balancePenaltyList;
    }

    @PlanningEntityCollectionProperty
    public List<MrProcessAssignment> getProcessAssignmentList() {
        return processAssignmentList;
    }

    public void setProcessAssignmentList(List<MrProcessAssignment> processAssignmentList) {
        this.processAssignmentList = processAssignmentList;
    }

    @PlanningScore
    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ProblemFactCollectionProperty
    @JsonIgnore
    @SuppressWarnings("unused")
    private List<MrServiceDependency> getServiceDependencyList() {
        List<MrServiceDependency> serviceDependencyList = new ArrayList<>(serviceList.size() * 5);
        for (MrService service : serviceList) {
            for (MrService toService : service.getToDependencyServiceList()) {
                MrServiceDependency serviceDependency = new MrServiceDependency();
                serviceDependency.setFromService(service);
                serviceDependency.setToService(toService);
                serviceDependencyList.add(serviceDependency);
            }
        }
        return serviceDependencyList;
    }

}
