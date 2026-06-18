/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class MrGlobalPenaltyInfo extends AbstractPersistable {

    private int processMoveCostWeight;
    private int serviceMoveCostWeight;
    private int machineMoveCostWeight;

    @SuppressWarnings("unused")
    MrGlobalPenaltyInfo() {
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
