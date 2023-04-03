package org.optaplanner.examples.vehiclerouting.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

class VehicleRoutingSmokeTest extends SolverSmokeTest<VehicleRoutingSolution, HardSoftLongScore> {

    private static final String CVRP_32_CUSTOMERS = "data/vehiclerouting/unsolved/cvrp-32customers.json";
    private static final String CVRPTW_100_CUSTOMERS_A = "data/vehiclerouting/unsolved/cvrptw-100customers-A.json";

    @Override
    protected VehicleRoutingApp createCommonApp() {
        return new VehicleRoutingApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, CVRP_32_CUSTOMERS,
                        HardSoftLongScore.ofSoft(-788369),
                        HardSoftLongScore.ofSoft(-788369)),
                TestData.of(ConstraintStreamImplType.DROOLS, CVRPTW_100_CUSTOMERS_A,
                        HardSoftLongScore.ofSoft(-2113238),
                        HardSoftLongScore.ofSoft(-2156060)),
                TestData.of(ConstraintStreamImplType.BAVET, CVRP_32_CUSTOMERS,
                        HardSoftLongScore.ofSoft(-788369),
                        HardSoftLongScore.ofSoft(-788369)),
                TestData.of(ConstraintStreamImplType.BAVET, CVRPTW_100_CUSTOMERS_A,
                        HardSoftLongScore.ofSoft(-1965310),
                        HardSoftLongScore.ofSoft(-2112382)));
    }
}
