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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("MrGlobalPenaltyInfo")
public class MrGlobalPenaltyInfo extends AbstractPersistable {

    private int processMoveCost;
    private int serviceMoveCost;
    private int machineMoveCost;

    public int getProcessMoveCost() {
        return processMoveCost;
    }

    public void setProcessMoveCost(int processMoveCost) {
        this.processMoveCost = processMoveCost;
    }

    public int getServiceMoveCost() {
        return serviceMoveCost;
    }

    public void setServiceMoveCost(int serviceMoveCost) {
        this.serviceMoveCost = serviceMoveCost;
    }

    public int getMachineMoveCost() {
        return machineMoveCost;
    }

    public void setMachineMoveCost(int machineMoveCost) {
        this.machineMoveCost = machineMoveCost;
    }
}
