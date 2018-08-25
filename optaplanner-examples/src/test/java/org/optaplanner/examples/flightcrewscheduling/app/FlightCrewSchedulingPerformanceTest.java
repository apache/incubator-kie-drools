/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.flightcrewscheduling.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;

public class FlightCrewSchedulingPerformanceTest extends SolverPerformanceTest<FlightCrewSolution> {

    public FlightCrewSchedulingPerformanceTest(String moveThreadCount) {
        super(moveThreadCount);
    }

    @Override
    protected FlightCrewSchedulingApp createCommonApp() {
        return new FlightCrewSchedulingApp();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel() {
        File unsolvedDataFile = new File("data/flightcrewscheduling/unsolved/175flights-7days-Europe.xlsx");
        runSpeedTest(unsolvedDataFile, "0hard/-129000000soft");
    }

    @Test(timeout = 600000)
    public void solveModelFastAssert() {
        File unsolvedDataFile = new File("data/flightcrewscheduling/unsolved/175flights-7days-Europe.xlsx");
        runSpeedTest(unsolvedDataFile, "0hard/-129000000soft", EnvironmentMode.FAST_ASSERT);
    }

}
