/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VehicleRoutingPerformanceTest extends SolverPerformanceTest<VehicleRoutingSolution> {

    private static final String CVRP_32_CUSTOMERS_XML = "data/vehiclerouting/unsolved/cvrp-32customers.xml";
    private static final String CVRPTW_100_CUSTOMERS_A_XML = "data/vehiclerouting/unsolved/cvrptw-100customers-A.xml";

    @Override
    protected VehicleRoutingApp createCommonApp() {
        return new VehicleRoutingApp();
    }

    @Override
    protected Stream<TestData> testData() {
        return Stream.of(
                testData(CVRP_32_CUSTOMERS_XML, "0hard/-750000soft", EnvironmentMode.REPRODUCIBLE),
                testData(CVRP_32_CUSTOMERS_XML, "0hard/-770000soft", EnvironmentMode.FAST_ASSERT),
                testData(CVRPTW_100_CUSTOMERS_A_XML, "0hard/-1869903soft", EnvironmentMode.REPRODUCIBLE),
                testData(CVRPTW_100_CUSTOMERS_A_XML, "0hard/-1877466soft", EnvironmentMode.FAST_ASSERT));
    }
}
