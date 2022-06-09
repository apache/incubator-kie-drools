package org.optaplanner.examples.vehiclerouting.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

class VehicleRoutingPerformanceTest extends SolverPerformanceTest<VehicleRoutingSolution, HardSoftLongScore> {

    private static final String CVRP_32_CUSTOMERS_XML = "data/vehiclerouting/unsolved/cvrp-32customers.xml";
    private static final String CVRPTW_100_CUSTOMERS_A_XML = "data/vehiclerouting/unsolved/cvrptw-100customers-A.xml";

    @Override
    protected VehicleRoutingApp createCommonApp() {
        return new VehicleRoutingApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                testData(CVRP_32_CUSTOMERS_XML, HardSoftLongScore.of(0, -744242), EnvironmentMode.REPRODUCIBLE),
                testData(CVRP_32_CUSTOMERS_XML, HardSoftLongScore.of(0, -745420), EnvironmentMode.FAST_ASSERT),
                testData(CVRPTW_100_CUSTOMERS_A_XML, HardSoftLongScore.of(0, -1798722), EnvironmentMode.REPRODUCIBLE),
                testData(CVRPTW_100_CUSTOMERS_A_XML, HardSoftLongScore.of(0, -1812202), EnvironmentMode.FAST_ASSERT));
    }
}
