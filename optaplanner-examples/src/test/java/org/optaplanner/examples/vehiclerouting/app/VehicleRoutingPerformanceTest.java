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

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingDao;

public class VehicleRoutingPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return VehicleRoutingApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new VehicleRoutingDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_cvrp_32customers() {
        File unsolvedDataFile = new File("data/vehiclerouting/unsolved/cvrp-32customers.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-750000soft");
    }

    @Test(timeout = 600000)
    public void solveModel_cvrp_32customersFastAssert() {
        File unsolvedDataFile = new File("data/vehiclerouting/unsolved/cvrp-32customers.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-770000soft", EnvironmentMode.FAST_ASSERT);
    }

    @Test(timeout = 600000)
    public void solveModel_cvrptw_100customers_A() {
        File unsolvedDataFile = new File("data/vehiclerouting/unsolved/cvrptw-100customers-A.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-1869903soft");
    }

    @Test(timeout = 600000)
    public void solveModel_cvrptw_100customers_AFastAssert() {
        File unsolvedDataFile = new File("data/vehiclerouting/unsolved/cvrptw-100customers-A.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-1877466soft", EnvironmentMode.FAST_ASSERT);
    }

}
