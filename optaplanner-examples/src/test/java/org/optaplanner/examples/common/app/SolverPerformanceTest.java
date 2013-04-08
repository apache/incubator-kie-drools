/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.app;

import java.io.File;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Runs an example solver.
 * All tests ending with the suffix <code>PerformanceTest</code> are reported on by hudson
 * in graphs which show the execution time over builds.
 * <p/>
 * Recommended courtesy notes: Always use a timeout value on @Test.
 * The timeout should be the triple of the timeout on a normal 3 year old desktop computer,
 * because some of the hudson machines are old.
 * For example, on a normal 3 year old desktop computer it always finishes in less than 1 minute,
 * then specify a timeout of 3 minutes.
 */
public abstract class SolverPerformanceTest extends LoggingTest {

    protected SolutionDao solutionDao;

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
        File dataDir = solutionDao.getDataDir();
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist." +
                    " The working directory should be set to the directory that contains the data directory." +
                    " This is different in a git clone (optaplanner/optaplanner-examples)" +
                    " and the release zip (examples).");
        }
    }

    protected abstract String createSolverConfigResource();

    protected abstract SolutionDao createSolutionDao();

    protected void runSpeedTest(File unsolvedDataFile, String scoreAttainedString) {
        runSpeedTest(unsolvedDataFile, scoreAttainedString, EnvironmentMode.REPRODUCIBLE);
    }

    protected void runSpeedTest(File unsolvedDataFile, String scoreAttainedString, EnvironmentMode environmentMode) {
        SolverFactory solverFactory = buildSolverFactory(scoreAttainedString, environmentMode);
        Solver solver = solve(solverFactory, unsolvedDataFile);
        assertBestSolution(solver, scoreAttainedString);
    }

    protected SolverFactory buildSolverFactory(String scoreAttainedString, EnvironmentMode environmentMode) {
        SolverFactory solverFactory = new XmlSolverFactory(createSolverConfigResource());
        solverFactory.getSolverConfig().setEnvironmentMode(environmentMode);
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setScoreAttained(scoreAttainedString);
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
        return solverFactory;
    }

    private Solver solve(SolverFactory solverFactory, File unsolvedDataFile) {
        Solution planningProblem = solutionDao.readSolution(unsolvedDataFile);
        Solver solver = solverFactory.buildSolver();
        solver.setPlanningProblem(planningProblem);
        solver.solve();
        return solver;
    }

    private void assertBestSolution(Solver solver, String scoreAttainedString) {
        Solution bestSolution = solver.getBestSolution();
        assertNotNull(bestSolution);
        Score bestScore = bestSolution.getScore();
        Score scoreAttained = solver.getScoreDirectorFactory().getScoreDefinition().parseScore(scoreAttainedString);
        assertTrue("The bestScore (" + bestScore + ") must be at least scoreAttained (" + scoreAttained + ").",
                bestScore.compareTo(scoreAttained) >= 0);
    }

}
