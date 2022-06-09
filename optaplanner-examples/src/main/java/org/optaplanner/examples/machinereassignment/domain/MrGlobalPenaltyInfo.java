package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrGlobalPenaltyInfo")
public class MrGlobalPenaltyInfo extends AbstractPersistable {

    private int processMoveCostWeight;
    private int serviceMoveCostWeight;
    private int machineMoveCostWeight;

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
