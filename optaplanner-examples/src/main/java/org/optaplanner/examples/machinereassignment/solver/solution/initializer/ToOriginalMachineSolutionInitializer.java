package org.optaplanner.examples.machinereassignment.solver.solution.initializer;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;

public class ToOriginalMachineSolutionInitializer implements CustomPhaseCommand<MachineReassignment> {

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
