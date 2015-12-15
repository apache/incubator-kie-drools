/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringDao;

public class NurseRosteringPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return NurseRosteringApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new NurseRosteringDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveMedium_late01_initialized() {
        File unsolvedDataFile = new File("data/nurserostering/unsolved/medium_late01_initialized.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-350soft");
    }

    @Test(timeout = 600000)
    public void solveMedium_late01_initializedFastAssert() {
        File unsolvedDataFile = new File("data/nurserostering/unsolved/medium_late01_initialized.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-473soft", EnvironmentMode.FAST_ASSERT);
    }

}
