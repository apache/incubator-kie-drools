/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtMachine")
public class Machine extends AbstractPersistable {

    private int index;

    private long powerConsumptionMicros; // when it's up
    private long spinUpDownCostMicros; // In micros, sum of upCost and downCost

    // Order is equal to resourceList so Resource.getIndex() can be used for the index
    private List<MachineCapacity> machineCapacityList;

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

    public MachineCapacity getMachineCapacity(Resource resource) {
        return machineCapacityList.get(resource.getIndex());
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getLabel() {
        return "Machine " + id;
    }

}
