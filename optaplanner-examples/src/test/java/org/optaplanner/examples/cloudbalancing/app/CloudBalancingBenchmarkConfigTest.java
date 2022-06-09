package org.optaplanner.examples.cloudbalancing.app;

import org.optaplanner.examples.cloudbalancing.optional.benchmark.CloudBalancingBenchmarkApp;
import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class CloudBalancingBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new CloudBalancingBenchmarkApp();
    }
}
