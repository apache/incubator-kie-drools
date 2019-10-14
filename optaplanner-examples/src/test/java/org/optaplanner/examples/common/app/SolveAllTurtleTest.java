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
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.common.TestSystemProperties;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class SolveAllTurtleTest<Solution_> extends AbstractTurtleTest {

    private static final String MOVE_THREAD_COUNT_OVERRIDE = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNT);

    private final String solverConfigResource;

    public SolveAllTurtleTest(String solverConfigResource) {
        this.solverConfigResource = solverConfigResource;
    }

    protected abstract Solution_ readProblem();

    @Test
    public void runFastAndFullAssert() {
        checkRunTurtleTests();
        SolverConfig solverConfig = buildSolverConfig();
        Solution_ problem = readProblem();
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.FAST_ASSERT, problem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        problem = buildAndSolve(solverConfig, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 3L);
    }

    protected SolverConfig buildSolverConfig() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        // buildAndSolve() fills in minutesSpentLimit
        solverConfig.setTerminationConfig(new TerminationConfig());
        if (MOVE_THREAD_COUNT_OVERRIDE != null) {
            solverConfig.setMoveThreadCount(MOVE_THREAD_COUNT_OVERRIDE);
        }
        return solverConfig;
    }

    protected Solution_ buildAndSolve(SolverConfig solverConfig, EnvironmentMode environmentMode,
            Solution_ problem, long maximumMinutesSpent) {
        solverConfig.getTerminationConfig().setMinutesSpentLimit(maximumMinutesSpent);
        solverConfig.setEnvironmentMode(environmentMode);
        Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = overwritingEasyScoreCalculatorClass();
        if (easyScoreCalculatorClass != null && environmentMode.isAsserted()) {
            ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            assertionScoreDirectorFactoryConfig.setEasyScoreCalculatorClass(easyScoreCalculatorClass);
            solverConfig.getScoreDirectorFactoryConfig().setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactoryConfig);
        }
        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        Solver<Solution_> solver = solverFactory.buildSolver();
        return solver.solve(problem);
    }

    protected Class<? extends EasyScoreCalculator> overwritingEasyScoreCalculatorClass()  {
        return null;
    }

}
