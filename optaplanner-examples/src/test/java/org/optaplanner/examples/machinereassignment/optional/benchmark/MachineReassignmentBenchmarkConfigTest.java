package org.optaplanner.examples.machinereassignment.optional.benchmark;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class MachineReassignmentBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new MachineReassignmentBenchmarkApp();
    }
}
