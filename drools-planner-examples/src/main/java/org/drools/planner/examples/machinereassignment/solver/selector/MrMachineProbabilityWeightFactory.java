/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.machinereassignment.solver.selector;

import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrProcess;
import org.drools.planner.examples.machinereassignment.domain.MrProcessAssignment;
import org.drools.planner.examples.machinereassignment.domain.MrResource;

public class MrMachineProbabilityWeightFactory implements SelectionProbabilityWeightFactory<MrProcessAssignment> {

    public double createProbabilityWeight(ScoreDirector scoreDirector, MrProcessAssignment processAssignment) {
        MachineReassignment machineReassignment = (MachineReassignment) scoreDirector.getWorkingSolution();
        MrMachine machine = processAssignment.getMachine();
        // TODO reuse usage calculated by of the ScoreCalculator which is a delta
        long[] usage = new long[machineReassignment.getResourceList().size()];
        for (MrProcessAssignment someProcessAssignment : machineReassignment.getProcessAssignmentList()) {
            if (someProcessAssignment.getMachine() == machine) {
                MrProcess process = someProcessAssignment.getProcess();
                for (MrResource resource : machineReassignment.getResourceList()) {
                    usage[resource.getIndex()] += process.getUsage(resource);
                }
            }
        }
        double sum = 0.0;
        for (MrResource resource : machineReassignment.getResourceList()) {
            double available = (double)
                    (machine.getMachineCapacity(resource).getSafetyCapacity() - usage[resource.getIndex()]);
            sum += (available * available);
        }
        return sum + 1.0;
    }

}
