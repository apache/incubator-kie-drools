/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.solver.solution.initializer;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToOriginalMachineSolutionInitializer implements CustomPhaseCommand<MachineReassignment> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void changeWorkingSolution(ScoreDirector<MachineReassignment> scoreDirector) {
        MachineReassignment machineReassignment = scoreDirector.getWorkingSolution();
        initializeProcessAssignmentList(scoreDirector, machineReassignment);
    }

    private void initializeProcessAssignmentList(ScoreDirector<MachineReassignment> scoreDirector,
            MachineReassignment machineReassignment) {
        for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
            MrMachine originalMachine = processAssignment.getOriginalMachine();
            MrMachine machine = originalMachine == null ? machineReassignment.getMachineList().get(0) : originalMachine;
            scoreDirector.beforeVariableChanged(processAssignment, "machine");
            processAssignment.setMachine(machine);
            scoreDirector.afterVariableChanged(processAssignment, "machine");
            scoreDirector.triggerVariableListeners();
        }
    }

}
