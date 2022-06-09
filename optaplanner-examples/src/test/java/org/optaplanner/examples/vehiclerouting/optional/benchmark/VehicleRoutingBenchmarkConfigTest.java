package org.optaplanner.examples.vehiclerouting.optional.benchmark;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class VehicleRoutingBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new VehicleRoutingBenchmarkApp();
    }
}
