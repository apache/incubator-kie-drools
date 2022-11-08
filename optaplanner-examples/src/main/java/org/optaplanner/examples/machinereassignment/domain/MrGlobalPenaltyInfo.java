package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

public class MrGlobalPenaltyInfo extends AbstractPersistableJackson {

    private int processMoveCostWeight;
    private int serviceMoveCostWeight;
    private int machineMoveCostWeight;

    @SuppressWarnings("unused")
    MrGlobalPenaltyInfo() { // For Jackson.
    }

    public MrGlobalPenaltyInfo(long id) {
        super(id);
    }

    public MrGlobalPenaltyInfo(long id, int processMoveCostWeight, int serviceMoveCostWeight, int machineMoveCostWeight) {
        super(id);
        this.processMoveCostWeight = processMoveCostWeight;
        this.serviceMoveCostWeight = serviceMoveCostWeight;
        this.machineMoveCostWeight = machineMoveCostWeight;
    }

    public int getProcessMoveCostWeight() {
        return processMoveCostWeight;
    }

    public void setProcessMoveCostWeight(int processMoveCostWeight) {
        this.processMoveCostWeight = processMoveCostWeight;
    }

    public int getServiceMoveCostWeight() {
        return serviceMoveCostWeight;
    }

    public void setServiceMoveCostWeight(int serviceMoveCostWeight) {
        this.serviceMoveCostWeight = serviceMoveCostWeight;
    }

    public int getMachineMoveCostWeight() {
        return machineMoveCostWeight;
    }

    public void setMachineMoveCostWeight(int machineMoveCostWeight) {
        this.machineMoveCostWeight = machineMoveCostWeight;
    }

}
