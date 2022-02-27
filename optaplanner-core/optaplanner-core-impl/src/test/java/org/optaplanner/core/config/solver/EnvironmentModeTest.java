/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.solver.testutil.calculator.TestdataCorruptedDifferentValuesCalculator;
import org.optaplanner.core.config.solver.testutil.calculator.TestdataDifferentValuesCalculator;
import org.optaplanner.core.config.solver.testutil.corruptedmove.factory.TestdataCorruptedEntityUndoMoveFactory;
import org.optaplanner.core.config.solver.testutil.corruptedmove.factory.TestdataCorruptedUndoMoveFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class EnvironmentModeTest {

    private static final int NUMBER_OF_RANDOM_NUMBERS_GENERATED = 1000;
    private static final int NUMBER_OF_TIMES_RUN = 10;
    private static final int NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT = 20;

    private static TestdataSolution inputProblem;

    @BeforeAll
    static void setUpInputProblem() {
        inputProblem = new TestdataSolution("s1");
        inputProblem.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"),
                new TestdataValue("v3")));
        inputProblem.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"),
                new TestdataEntity("e3"), new TestdataEntity("e4")));
    }

    private static SolverConfig buildSolverConfig(EnvironmentMode environmentMode) {
        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig()
                .withCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig
                .setTerminationConfig(
                        new TerminationConfig().withStepCountLimit(NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT));

        return new SolverConfig()
                .withSolutionClass(TestdataSolution.class)
                .withEntityClasses(TestdataEntity.class)
                .withEnvironmentMode(environmentMode)
                .withPhases(initializerPhaseConfig, localSearchPhaseConfig);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(EnvironmentMode.class)
    void determinism(EnvironmentMode environmentMode) {
        SolverConfig solverConfig = buildSolverConfig(environmentMode);
        setSolverConfigCalculatorClass(solverConfig, TestdataDifferentValuesCalculator.class);

        Solver<TestdataSolution> solver1 = SolverFactory.<TestdataSolution> create(solverConfig).buildSolver();
        Solver<TestdataSolution> solver2 = SolverFactory.<TestdataSolution> create(solverConfig).buildSolver();

        switch (environmentMode) {
            case NON_REPRODUCIBLE:
                assertNonReproducibility(solver1, solver2);
                break;
            case FULL_ASSERT:
            case FAST_ASSERT:
            case NON_INTRUSIVE_FULL_ASSERT:
            case REPRODUCIBLE:
                assertReproducibility(solver1, solver2);
                break;
            default:
                Assertions.fail("Environment mode not covered: " + environmentMode);
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(EnvironmentMode.class)
    void corruptedCustomMoves(EnvironmentMode environmentMode) {
        SolverConfig solverConfig = buildSolverConfig(environmentMode);
        // Intrusive modes should throw exception about corrupted undoMove
        setSolverConfigCalculatorClass(solverConfig, TestdataDifferentValuesCalculator.class);

        switch (environmentMode) {
            case FULL_ASSERT:
            case FAST_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(
                        solverConfig,
                        TestdataCorruptedUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving(solverConfig, "corrupted undoMove");
                break;
            case NON_INTRUSIVE_FULL_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(
                        solverConfig,
                        TestdataCorruptedEntityUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving(solverConfig, "not the uncorruptedScore");
                break;
            case REPRODUCIBLE:
            case NON_REPRODUCIBLE:
                // No exception expected
                break;
            default:
                Assertions.fail("Environment mode not covered: " + environmentMode);
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(EnvironmentMode.class)
    void corruptedConstraints(EnvironmentMode environmentMode) {
        SolverConfig solverConfig = buildSolverConfig(environmentMode);
        // For full assert modes it should throw exception about corrupted score
        setSolverConfigCalculatorClass(solverConfig, TestdataCorruptedDifferentValuesCalculator.class);

        switch (environmentMode) {
            case FULL_ASSERT:
            case NON_INTRUSIVE_FULL_ASSERT:
                assertIllegalStateExceptionWhileSolving(
                        solverConfig,
                        "not the uncorruptedScore");
                break;
            case FAST_ASSERT:
                assertIllegalStateExceptionWhileSolving(
                        solverConfig,
                        "Score corruption analysis could not be generated ");
                break;
            case REPRODUCIBLE:
            case NON_REPRODUCIBLE:
                // No exception expected
                break;
            default:
                Assertions.fail("Environment mode not covered: " + environmentMode);
        }
    }

    private void assertReproducibility(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        assertGeneratingSameNumbers(((DefaultSolver<TestdataSolution>) solver1).getRandomFactory(),
                ((DefaultSolver<TestdataSolution>) solver2).getRandomFactory());
        assertSameScoreSeries(solver1, solver2);
    }

    private void assertNonReproducibility(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        assertGeneratingDifferentNumbers(((DefaultSolver<TestdataSolution>) solver1).getRandomFactory(),
                ((DefaultSolver<TestdataSolution>) solver2).getRandomFactory());
        assertDifferentScoreSeries(solver1, solver2);
    }

    private void assertIllegalStateExceptionWhileSolving(SolverConfig solverConfig, String exceptionMessage) {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> PlannerTestUtils.solve(solverConfig, inputProblem))
                .withMessageContaining(exceptionMessage);
    }

    private void assertSameScoreSeries(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        TestdataStepScoreListener listener = new TestdataStepScoreListener();
        TestdataStepScoreListener listener2 = new TestdataStepScoreListener();

        ((DefaultSolver<TestdataSolution>) solver1).addPhaseLifecycleListener(listener);
        ((DefaultSolver<TestdataSolution>) solver2).addPhaseLifecycleListener(listener2);

        assertSoftly(softly -> IntStream.range(0, NUMBER_OF_TIMES_RUN)
                .forEach(i -> {
                    solver1.solve(inputProblem);
                    solver2.solve(inputProblem);
                    softly.assertThat(listener.getScores())
                            .as("Score steps should be the same "
                                    + "in a reproducible environment mode.")
                            .isEqualTo(listener2.getScores());
                }));
    }

    private void assertDifferentScoreSeries(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        TestdataStepScoreListener listener = new TestdataStepScoreListener();
        TestdataStepScoreListener listener2 = new TestdataStepScoreListener();

        ((DefaultSolver<TestdataSolution>) solver1).addPhaseLifecycleListener(listener);
        ((DefaultSolver<TestdataSolution>) solver2).addPhaseLifecycleListener(listener2);

        assertSoftly(softly -> IntStream.range(0, NUMBER_OF_TIMES_RUN)
                .forEach(i -> {
                    solver1.solve(inputProblem);
                    solver2.solve(inputProblem);
                    softly.assertThat(listener.getScores())
                            .as("Score steps should not be the same in a non-reproducible environment mode. "
                                    + "This might be possible because searchSpace is not infinite and "
                                    + "two different random scenarios can have the same results. "
                                    + "Run test again.")
                            .isNotEqualTo(listener2.getScores());
                }));
    }

    private void assertGeneratingSameNumbers(RandomFactory factory1, RandomFactory factory2) {
        Random random = factory1.createRandom();
        Random random2 = factory2.createRandom();

        assertSoftly(softly -> IntStream.range(0, NUMBER_OF_RANDOM_NUMBERS_GENERATED)
                .forEach(i -> softly.assertThat(random.nextInt())
                        .as("Random factories should generate the same results "
                                + "in a reproducible environment mode.")
                        .isEqualTo(random2.nextInt())));
    }

    private void assertGeneratingDifferentNumbers(RandomFactory factory1, RandomFactory factory2) {
        Random random = factory1.createRandom();
        Random random2 = factory2.createRandom();

        assertSoftly(softly -> IntStream.range(0, NUMBER_OF_RANDOM_NUMBERS_GENERATED)
                .forEach(i -> softly.assertThat(random.nextInt())
                        .as("Random factories should not generate exactly the same results "
                                + "in the non-reproducible environment mode. "
                                + "It can happen but the probability is very low. Run test again")
                        .isNotEqualTo(random2.nextInt())));
    }

    private void setSolverConfigCalculatorClass(SolverConfig solverConfig,
            Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig()
                .withEasyScoreCalculatorClass(easyScoreCalculatorClass));
    }

    private void setSolverConfigMoveListFactoryClassToCorrupted(SolverConfig solverConfig,
            Class<? extends MoveListFactory<TestdataSolution>> move) {
        MoveListFactoryConfig moveListFactoryConfig = new MoveListFactoryConfig();
        moveListFactoryConfig.setMoveListFactoryClass(move);

        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig()
                .withCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(moveListFactoryConfig);
        localSearchPhaseConfig
                .setTerminationConfig(
                        new TerminationConfig().withStepCountLimit(NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT));

        solverConfig.withPhases(initializerPhaseConfig, localSearchPhaseConfig);
    }

    public static class TestdataFirstValueInitializer implements CustomPhaseCommand<TestdataSolution> {

        @Override
        public void changeWorkingSolution(ScoreDirector<TestdataSolution> scoreDirector) {
            TestdataSolution solution = scoreDirector.getWorkingSolution();
            TestdataValue firstValue = solution.getValueList().get(0);

            for (TestdataEntity entity : solution.getEntityList()) {
                scoreDirector.beforeVariableChanged(entity, "value");
                entity.setValue(firstValue);
                scoreDirector.afterVariableChanged(entity, "value");
            }

            scoreDirector.triggerVariableListeners();
            InnerScoreDirector<TestdataSolution, ?> innerScoreDirector =
                    (InnerScoreDirector<TestdataSolution, ?>) scoreDirector;
            Score<?> score = innerScoreDirector.calculateScore();

            if (!score.isSolutionInitialized()) {
                throw new IllegalStateException("The solution (" + TestdataEntity.class.getSimpleName()
                        + ") was not fully initialized by CustomSolverPhase: ("
                        + this.getClass().getCanonicalName() + ")");
            }
        }
    }

    public static class TestdataStepScoreListener extends PhaseLifecycleListenerAdapter<TestdataSolution> {

        private List<SimpleScore> scores = new ArrayList<>();

        @Override
        public void stepEnded(AbstractStepScope<TestdataSolution> stepScope) {
            TestdataSolution solution = stepScope.getWorkingSolution();

            if (solution.getScore() != null) {
                scores.add(solution.getScore());
            }
        }

        public List<SimpleScore> getScores() {
            return scores;
        }
    }
}
