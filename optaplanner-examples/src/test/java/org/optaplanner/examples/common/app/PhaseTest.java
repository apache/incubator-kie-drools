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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.persistence.SolutionDao;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public abstract class PhaseTest extends LoggingTest {

    protected static <Enum_ extends Enum> Collection<Object[]> buildParameters(SolutionDao solutionDao,
            Enum_[] types, String... unsolvedFileNames) {
        List<Object[]> filesAsParameters = new ArrayList<Object[]>(unsolvedFileNames.length * types.length);
        File dataDir = solutionDao.getDataDir();
        File unsolvedDataDir = new File(dataDir, "unsolved");
        for (String unsolvedFileName : unsolvedFileNames) {
            File unsolvedFile = new File(unsolvedDataDir, unsolvedFileName);
            if (!unsolvedFile.exists()) {
                throw new IllegalStateException("The directory unsolvedFile (" + unsolvedFile.getAbsolutePath()
                        + ") does not exist.");
            }
            for (Enum_ type : types) {
                filesAsParameters.add(new Object[]{unsolvedFile, type});
            }
        }
        return filesAsParameters;
    }

    protected SolutionDao solutionDao;
    protected File dataFile;

    protected PhaseTest(File dataFile) {
        this.dataFile = dataFile;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
    }

    protected abstract SolutionDao createSolutionDao();

    @Test(timeout = 600000)
    public void runPhase() {
        SolverFactory<Solution> solverFactory = buildSolverFactory();
        Solution planningProblem = readPlanningProblem();
        Solver<Solution> solver = solverFactory.buildSolver();

        Solution bestSolution = solver.solve(planningProblem);
        assertSolution(bestSolution);
    }

    protected void assertSolution(Solution bestSolution) {
        assertNotNull(bestSolution);
        assertNotNull(bestSolution.getScore());
    }

    protected abstract SolverFactory<Solution> buildSolverFactory();

    protected abstract String createSolverConfigResource();

    protected Solution readPlanningProblem() {
        return solutionDao.readSolution(dataFile);
    }

}
