package org.optaplanner.examples.tsp.optional.benchmark;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class TspBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new TspBenchmarkApp();
    }
}
