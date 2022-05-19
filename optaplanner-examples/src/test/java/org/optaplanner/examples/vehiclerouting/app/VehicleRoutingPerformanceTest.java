/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
