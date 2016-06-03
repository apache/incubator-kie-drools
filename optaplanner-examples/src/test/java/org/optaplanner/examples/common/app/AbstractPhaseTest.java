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
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.persistence.SolutionDao;

import static org.junit.Assert.*;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class AbstractPhaseTest<Solution_> extends LoggingTest {

    protected static <Solution_, Enum_ extends Enum> Collection<Object[]> buildParameters(
            SolutionDao<Solution_> solutionDao, Enum_[] types, String... unsolvedFileNames) {
        List<Object[]> filesAsParameters = new ArrayList<>(unsolvedFileNames.length * types.length);
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

    protected SolutionDao<Solution_> solutionDao;
    protected File dataFile;

    protected AbstractPhaseTest(File dataFile) {
        this.dataFile = dataFile;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
    }

    protected abstract SolutionDao<Solution_> createSolutionDao();

    @Test(timeout = 600000)
    public void runPhase() {
        SolverFactory<Solution_> solverFactory = buildSolverFactory();
        Solution_ planningProblem = readPlanningProblem();
        Solver<Solution_> solver = solverFactory.buildSolver();

        Solution_ bestSolution = solver.solve(planningProblem);
        assertSolution(bestSolution);
        assertNotNull(solver.getBestScore());
    }

    protected void assertSolution(Solution_ bestSolution) {
        assertNotNull(bestSolution);
    }

    protected abstract SolverFactory<Solution_> buildSolverFactory();

    protected abstract String createSolverConfigResource();

    protected Solution_ readPlanningProblem() {
        return solutionDao.readSolution(dataFile);
    }

}
