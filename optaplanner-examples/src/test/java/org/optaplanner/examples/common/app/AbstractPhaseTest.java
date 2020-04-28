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
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractPhaseTest<Solution_> extends LoggingTest {

    protected abstract CommonApp<Solution_> createCommonApp();

    protected abstract Stream<String> unsolvedFileNames();

    protected abstract Stream<SolverFactory<Solution_>> buildSolverFactories(CommonApp<Solution_> commonApp);

    protected static File buildFile(File unsolvedDataDir, String unsolvedFileName) {
        File unsolvedFile = new File(unsolvedDataDir, unsolvedFileName);
        if (!unsolvedFile.exists()) {
            throw new IllegalStateException("The directory unsolvedFile (" + unsolvedFile.getAbsolutePath()
                    + ") does not exist.");
        }
        return unsolvedFile;
    }

    @TestFactory
    @Timeout(600)
    Stream<DynamicTest> runPhase() {
        CommonApp<Solution_> commonApp = createCommonApp();
        SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File unsolvedDataDir = new File(dataDir, "unsolved");
        return buildSolverFactories(commonApp).flatMap(solverFactory ->
                unsolvedFileNames().map(unsolvedFileName ->
                        dynamicTest(
                                unsolvedFileName + ", TODO enum",
                                () -> runPhase(solverFactory, readProblem(solutionFileIO, buildFile(unsolvedDataDir, unsolvedFileName)))
                        )));
    }

    private void runPhase(SolverFactory<Solution_> solverFactory, Solution_ problem) {
        Solver<Solution_> solver = solverFactory.buildSolver();

        Solution_ bestSolution = solver.solve(problem);
        assertSolution(bestSolution);
        assertNotNull(solver.getBestScore());
    }

    protected void assertSolution(Solution_ bestSolution) {
        assertNotNull(bestSolution);
    }

    private Solution_ readProblem(SolutionFileIO<Solution_> solutionFileIO, File dataFile) {
        Solution_ problem = solutionFileIO.read(dataFile);
        logger.info("Opened: {}", dataFile);
        return problem;
    }
}
