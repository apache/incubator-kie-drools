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

package org.drools.planner.examples.machinereassignment.solver.solution.initializer;

import org.drools.WorkingMemory;
import org.drools.planner.core.phase.custom.CustomSolverPhaseCommand;
import org.drools.planner.core.solution.director.SolutionDirector;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrProcessAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MrOriginalMachineSolutionInitializer implements CustomSolverPhaseCommand {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public void changeWorkingSolution(SolutionDirector solutionDirector) {
        MachineReassignment machineReassignment = (MachineReassignment) solutionDirector.getWorkingSolution();
        initializeProcessAssignmentList(solutionDirector, machineReassignment);
    }

    private void initializeProcessAssignmentList(SolutionDirector solutionDirector,
            MachineReassignment machineReassignment) {
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
        for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
            processAssignment.setMachine(processAssignment.getOriginalMachine());
            workingMemory.insert(processAssignment);
        }
    }

}
