/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nqueens.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.nqueens.persistence.NQueensDao;

public class NQueensPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return NQueensApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new NQueensDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_16queens() {
        runSpeedTest(new File("data/nqueens/unsolved/16queens.xml"),
                "0");
    }

    @Test(timeout = 600000)
    public void solveModel_8queensFastAssert() {
        runSpeedTest(new File("data/nqueens/unsolved/8queens.xml"),
                "0", EnvironmentMode.FAST_ASSERT);
    }

    @Test(timeout = 600000)
    public void solveModel_4queensFullAssert() {
        runSpeedTest(new File("data/nqueens/unsolved/4queens.xml"),
                "0", EnvironmentMode.FULL_ASSERT);
    }

}
