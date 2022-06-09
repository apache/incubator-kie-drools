package org.optaplanner.examples.nurserostering.optional.benchmark;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class NurseRosteringBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new NurseRosteringBenchmarkApp();
    }
}
