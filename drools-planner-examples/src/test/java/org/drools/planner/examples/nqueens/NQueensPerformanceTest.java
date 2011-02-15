/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.nqueens;

import java.io.File;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.examples.common.app.SolverPerformanceTest;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.nqueens.persistence.NQueensDaoImpl;
import org.junit.Test;

public class NQueensPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return "/org/drools/planner/examples/nqueens/solver/nqueensSolverConfig.xml";
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new NQueensDaoImpl();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 10000)
    public void solveDebug4QueensScore0() {
        runSpeedTest(new File("data/nqueens/unsolved/unsolvedNQueens04.xml"), "0", EnvironmentMode.DEBUG);
    }

    @Test(timeout = 10000)
    public void solveDebug8QueensScore0() {
        runSpeedTest(new File("data/nqueens/unsolved/unsolvedNQueens08.xml"), "0", EnvironmentMode.DEBUG);
    }

}
