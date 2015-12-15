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

package org.optaplanner.examples.projectjobscheduling.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingDao;

public class ProjectJobSchedulingPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return ProjectJobSchedulingApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new ProjectJobSchedulingDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_A_4() {
        File unsolvedDataFile = new File("data/projectjobscheduling/unsolved/A-4.xml");
        runSpeedTest(unsolvedDataFile, "0/-152/-69");
    }

    @Test(timeout = 600000)
    public void solveModel_A_4FastAssert() {
        File unsolvedDataFile = new File("data/projectjobscheduling/unsolved/A-4.xml");
        runSpeedTest(unsolvedDataFile, "0/-193/-92", EnvironmentMode.FAST_ASSERT);
    }

}
