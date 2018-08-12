/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.SolverPerformanceTest;

public class CheapTimePerformanceTest extends SolverPerformanceTest<CheapTimeSolution> {

    public CheapTimePerformanceTest(String moveThreadCount) {
        super(moveThreadCount);
    }

    @Override
    protected CheapTimeApp createCommonApp() {
        return new CheapTimeApp();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveInstance00() {
        File unsolvedDataFile = new File("data/cheaptime/unsolved/instance00.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-902335925205947medium/-20672soft");
    }

    @Test(timeout = 600000)
    public void solveInstance00FastAssert() {
        File unsolvedDataFile = new File("data/cheaptime/unsolved/instance00.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-918680355373904medium/-22228soft", EnvironmentMode.FAST_ASSERT);
    }

}
