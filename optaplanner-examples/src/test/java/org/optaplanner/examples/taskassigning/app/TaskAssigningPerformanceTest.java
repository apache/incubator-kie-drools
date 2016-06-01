/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.taskassigning.persistence.TaskAssigningDao;

public class TaskAssigningPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return TaskAssigningApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TaskAssigningDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_50tasks_5employees() {
        File unsolvedDataFile = new File("data/taskassigning/unsolved/50tasks-5employees.xml");
        runSpeedTest(unsolvedDataFile, "[0]hard/[-3925/-6293940/-7772/-20463]soft");
    }

    @Test(timeout = 600000)
    public void solveModel_50tasks_5employeesFastAssert() {
        File unsolvedDataFile = new File("data/taskassigning/unsolved/50tasks-5employees.xml");
        runSpeedTest(unsolvedDataFile, "[0]hard/[-3988/-10452712/-15713/-21195]soft", EnvironmentMode.FAST_ASSERT);
    }

}
