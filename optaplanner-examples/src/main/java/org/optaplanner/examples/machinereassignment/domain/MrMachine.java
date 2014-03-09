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

package org.optaplanner.examples.machinereassignment.domain;

import java.util.List;

import com.google.common.collect.ArrayTable;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("MrMachine")
public class MrMachine extends AbstractPersistable {

    private MrNeighborhood neighborhood;
    private MrLocation location;

    // Order is equal to resourceList so resource.getIndex() can be used
    private List<MrMachineCapacity> machineCapacityList;
    private ArrayTable<MrNeighborhood, MrLocation, Integer> machineMoveCostTable; // key is toMachine

    public MrNeighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(MrNeighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public MrLocation getLocation() {
        return location;
    }

    public void setLocation(MrLocation location) {
        this.location = location;
    }

    public List<MrMachineCapacity> getMachineCapacityList() {
        return machineCapacityList;
    }

    public void setMachineCapacityList(List<MrMachineCapacity> machineCapacityList) {
        this.machineCapacityList = machineCapacityList;
    }

    public MrMachineCapacity getMachineCapacity(MrResource resource) {
        return machineCapacityList.get(resource.getIndex());
    }

    public ArrayTable<MrNeighborhood, MrLocation, Integer> getMachineMoveCostTable() {
        return machineMoveCostTable;
    }

    public void setMachineMoveCostTable(ArrayTable<MrNeighborhood, MrLocation, Integer> machineMoveCostTable) {
        this.machineMoveCostTable = machineMoveCostTable;
    }

    public String getLabel() {
        return "Machine " + getId();
    }

    public int getMoveCostTo(MrMachine machine) {
        return machineMoveCostTable.get(machine.getNeighborhood(), machine.getLocation());
    }

}
