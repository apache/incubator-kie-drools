package org.optaplanner.examples.cheaptime.optional.app;

import org.optaplanner.examples.cheaptime.optional.benchmark.CheapTimeBenchmarkApp;
import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class CheapTimeBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new CheapTimeBenchmarkApp();
    }
}
