/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import static org.junit.Assume.*;

/**
 * Turtle tests are not run by default. They are only run if <code>-DrunTurtleTests=true</code> because it takes days.
 */
@RunWith(Parameterized.class)
public abstract class SolveAllTurtleTest extends LoggingTest {

    protected static void checkRunTurtleTests() {
        assumeTrue("true".equals(System.getProperty("runTurtleTests")));
    }

    protected abstract String createSolverConfigResource();

    protected abstract Solution readPlanningProblem();

    @Test
    public void runFastAndFullAssert() {
        checkRunTurtleTests();
        SolverFactory<Solution> solverFactory = buildSolverFactory();
        Solution planningProblem = readPlanningProblem();
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, planningProblem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.FAST_ASSERT, planningProblem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        planningProblem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, planningProblem, 3L);
    }

    protected Solution buildAndSolve(SolverFactory<Solution> solverFactory, EnvironmentMode environmentMode,
            Solution planningProblem, long maximumMinutesSpent) {
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.getTerminationConfig().setMinutesSpentLimit(maximumMinutesSpent);
        solverConfig.setEnvironmentMode(environmentMode);
        Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = overwritingEasyScoreCalculatorClass();
        if (easyScoreCalculatorClass != null && environmentMode.isAsserted()) {
            ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            assertionScoreDirectorFactoryConfig.setEasyScoreCalculatorClass(easyScoreCalculatorClass);
            solverConfig.getScoreDirectorFactoryConfig().setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactoryConfig);
        }
        Solver<Solution> solver = solverFactory.buildSolver();
        Solution bestSolution = solver.solve(planningProblem);
        if (bestSolution == null) {
            // Solver didn't make it past initialization // TODO remove me once getBestSolution() never returns null
            bestSolution = planningProblem;
        }
        return bestSolution;
    }

    protected Class<? extends EasyScoreCalculator> overwritingEasyScoreCalculatorClass()  {
        return null;
    }

    protected SolverFactory<Solution> buildSolverFactory() {
        SolverFactory<Solution> solverFactory = SolverFactory.createFromXmlResource(createSolverConfigResource());
        TerminationConfig terminationConfig = new TerminationConfig();
        // buildAndSolve() fills in minutesSpentLimit
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);
        return solverFactory;
    }

}
