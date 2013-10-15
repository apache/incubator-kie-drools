/*
 * Copyright 2013 JBoss Inc
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.business.ExtensionFileFilter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.vehiclerouting.solver.score.VehicleRoutingSimpleScoreCalculator;

import static org.junit.Assume.*;

/**
 * Turtle tests are not run by default. They are only run if <code>-DrunTurtleTests=true</code> because it takes days.
 */
@RunWith(Parameterized.class)
public abstract class SolveAllTurtleTest extends LoggingTest {

    protected static void checkRunTurtleTests() {
        assumeTrue(ObjectUtils.equals("true", System.getProperty("runTurtleTests")));
    }

    protected static Collection<Object[]> getUnsolvedDataFilesAsParameters(SolutionDao solutionDao) {
        List<Object[]> filesAsParameters = new ArrayList<Object[]>();
        File dataDir = solutionDao.getDataDir();
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            List<File> unsolvedFileList = Arrays.asList(unsolvedDataDir.listFiles(
                    new ExtensionFileFilter(solutionDao.getFileExtension())));
            Collections.sort(unsolvedFileList);
            for (File unsolvedFile : unsolvedFileList) {
                filesAsParameters.add(new Object[]{unsolvedFile});
            }
        }
        return filesAsParameters;
    }

    protected SolutionDao solutionDao;

    protected File unsolvedDataFile;

    protected SolveAllTurtleTest(File unsolvedDataFile) {
        this.unsolvedDataFile = unsolvedDataFile;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
    }

    protected abstract String createSolverConfigResource();

    protected abstract SolutionDao createSolutionDao();

    @Test
    public void runFastAndFullAssert() {
        checkRunTurtleTests();
        SolverFactory solverFactory = buildSolverFactory();
        Solution planningProblem = solutionDao.readSolution(unsolvedDataFile);
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, planningProblem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.FAST_ASSERT, planningProblem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, planningProblem, 3L);
    }

    protected Solution buildAndSolve(SolverFactory solverFactory, EnvironmentMode environmentMode,
            Solution planningProblem, long maximumMinutesSpend) {
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.getTerminationConfig().setMaximumMinutesSpend(maximumMinutesSpend);
        solverConfig.setEnvironmentMode(environmentMode);
        Class<? extends SimpleScoreCalculator> simpleScoreCalculatorClass = overwritingSimpleScoreCalculatorClass();
        if (simpleScoreCalculatorClass != null && environmentMode.isAsserted()) {
            ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            assertionScoreDirectorFactoryConfig.setSimpleScoreCalculatorClass(simpleScoreCalculatorClass);
            solverConfig.getScoreDirectorFactoryConfig().setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactoryConfig);
        }
        Solver solver = solverFactory.buildSolver();
        solver.setPlanningProblem(planningProblem);
        solver.solve();
        Solution bestSolution = solver.getBestSolution();
        if (bestSolution == null) {
            // Solver didn't make it past initialization // TODO remove me once getBestSolution() never returns null
            bestSolution = planningProblem;
        }
        return bestSolution;
    }

    protected Class<? extends SimpleScoreCalculator>  overwritingSimpleScoreCalculatorClass()  {
        return null;
    }

    protected SolverFactory buildSolverFactory() {
        SolverFactory solverFactory = new XmlSolverFactory(createSolverConfigResource());
        TerminationConfig terminationConfig = new TerminationConfig();
        // buildAndSolve() fills in maximumMinutesSpend
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
        return solverFactory;
    }

}
