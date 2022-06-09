package org.optaplanner.examples.machinereassignment.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;

class MachineReassignmentSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<MachineReassignment> {

    @Override
    protected CommonApp<MachineReassignment> createCommonApp() {
        return new MachineReassignmentApp();
    }
}
