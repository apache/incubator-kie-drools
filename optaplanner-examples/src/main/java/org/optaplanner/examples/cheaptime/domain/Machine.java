package org.optaplanner.examples.cheaptime.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtMachine")
public class Machine extends AbstractPersistable {

    private int index;

    private long powerConsumptionMicros; // when it's up
    private long spinUpDownCostMicros; // In micros, sum of upCost and downCost

    // Order is equal to resourceList so Resource.getIndex() can be used for the index
    private List<MachineCapacity> machineCapacityList;

    public Machine() {

    }

    public Machine(int index, long powerConsumptionMicros, long spinUpDownCostMicros, MachineCapacity... machineCapacities) {
        super(index);
        this.index = index;
        this.powerConsumptionMicros = powerConsumptionMicros;
        this.spinUpDownCostMicros = spinUpDownCostMicros;
        this.machineCapacityList = Arrays.stream(machineCapacities)
                .collect(Collectors.toList());
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getPowerConsumptionMicros() {
        return powerConsumptionMicros;
    }

    public void setPowerConsumptionMicros(long powerConsumptionMicros) {
        this.powerConsumptionMicros = powerConsumptionMicros;
    }

    public long getSpinUpDownCostMicros() {
        return spinUpDownCostMicros;
    }

    public void setSpinUpDownCostMicros(long spinUpDownCostMicros) {
        this.spinUpDownCostMicros = spinUpDownCostMicros;
    }

    public List<MachineCapacity> getMachineCapacityList() {
        return machineCapacityList;
    }

    public void setMachineCapacityList(List<MachineCapacity> machineCapacityList) {
        this.machineCapacityList = machineCapacityList;
    }

    public int getCapacity(Resource resource) {
        return machineCapacityList.get(resource.getIndex()).getCapacity();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getLabel() {
        return "Machine " + id;
    }

}
