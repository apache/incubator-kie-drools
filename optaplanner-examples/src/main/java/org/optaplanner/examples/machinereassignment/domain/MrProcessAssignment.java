/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.machinereassignment.domain.solver.MrProcessAssignmentDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = MrProcessAssignmentDifficultyComparator.class)
@XStreamAlias("MrProcessAssignment")
public class MrProcessAssignment extends AbstractPersistable {

    private MrProcess process;
    private MrMachine originalMachine;

    private MrMachine machine;

    public MrProcess getProcess() {
        return process;
    }

    public void setProcess(MrProcess process) {
        this.process = process;
    }

    public MrMachine getOriginalMachine() {
        return originalMachine;
    }

    public void setOriginalMachine(MrMachine originalMachine) {
        this.originalMachine = originalMachine;
    }

    @PlanningVariable(valueRangeProviderRefs = {"machineRange"}) // TODO strengthComparatorClass = BedStrengthComparator.class)
    public MrMachine getMachine() {
        return machine;
    }

    public void setMachine(MrMachine machine) {
        this.machine = machine;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public MrService getService() {
        return process.getService();
    }

    public boolean isMoved() {
        return !ObjectUtils.equals(originalMachine, machine);
    }

    public int getProcessMoveCost() {
        return process.getMoveCost();
    }

    public int getMachineMoveCost() {
        return (machine == null || originalMachine == null) ? 0 : originalMachine.getMoveCostTo(machine);
    }

    public MrNeighborhood getNeighborhood() {
        return machine == null ? null : machine.getNeighborhood();
    }

    public MrLocation getLocation() {
        return machine == null ? null : machine.getLocation();
    }

    public long getUsage(MrResource resource) {
        return process.getUsage(resource);
    }

    public String getLabel() {
        return "Process " + getId();
    }

    @Override
    public String toString() {
        return process.toString();
    }

}
