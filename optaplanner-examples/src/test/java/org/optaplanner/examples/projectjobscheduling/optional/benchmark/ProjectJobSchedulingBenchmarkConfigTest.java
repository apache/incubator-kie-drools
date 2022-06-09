package org.optaplanner.examples.projectjobscheduling.optional.benchmark;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class ProjectJobSchedulingBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new ProjectJobSchedulingBenchmarkApp();
    }
}
