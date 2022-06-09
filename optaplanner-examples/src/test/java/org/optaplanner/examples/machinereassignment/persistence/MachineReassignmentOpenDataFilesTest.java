package org.optaplanner.examples.machinereassignment.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.machinereassignment.app.MachineReassignmentApp;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;

class MachineReassignmentOpenDataFilesTest extends OpenDataFilesTest<MachineReassignment> {

    @Override
    protected CommonApp<MachineReassignment> createCommonApp() {
        return new MachineReassignmentApp();
    }
}
