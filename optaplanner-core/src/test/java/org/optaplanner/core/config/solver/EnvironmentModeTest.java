package org.optaplanner.core.config.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.heuristic.move.corrupted.undo.factory.TestdataCorruptedEntityUndoMoveFactory;
import org.optaplanner.core.impl.testdata.heuristic.move.corrupted.undo.factory.TestdataCorruptedUndoMoveFactory;
import org.optaplanner.core.impl.testdata.phase.custom.TestdataFirstValueInitializer;
import org.optaplanner.core.impl.testdata.phase.event.TestdataStepScoreListener;
import org.optaplanner.core.impl.testdata.score.director.TestdataCorruptedDifferentValuesCalculator;
import org.optaplanner.core.impl.testdata.score.director.TestdataDifferentValuesCalculator;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(Parameterized.class)
public class EnvironmentModeTest {

    private static final int NUMBER_OF_RANDOM_NUMBERS_GENERATED = 1000;
    private static final int NUMBER_OF_TIMES_RUN = 10;
    private static final int NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT = 20;

    private final EnvironmentMode environmentMode;
    private static TestdataSolution inputProblem;
    private SolverConfig solverConfig;

    public EnvironmentModeTest(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    @BeforeClass
    public static void setUpInputProblem() {
        inputProblem = new TestdataSolution("s1");
        inputProblem.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"),
                                                new TestdataValue("v3")));
        inputProblem.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"),
                                                 new TestdataEntity("e3"), new TestdataEntity("e4")));
    }

    @Before
    public void setUpSolverConfig() {
        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig()
            .withCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT));

        solverConfig = new SolverConfig()
                .withSolutionClass(TestdataSolution.class)
                .withEntityClasses(TestdataEntity.class)
                .withEnvironmentMode(environmentMode)
                .withPhases(initializerPhaseConfig, localSearchPhaseConfig);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<EnvironmentMode> params() {
        return Arrays.asList(EnvironmentMode.values());
    }

    @Test
    public void determinismTest() {
        setSolverConfigCalculatorClass(TestdataDifferentValuesCalculator.class);

        Solver solver1 = SolverFactory.create(solverConfig).buildSolver();
        Solver solver2 = SolverFactory.create(solverConfig).buildSolver();

        switch (environmentMode) {
            case PRODUCTION:
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

    @Test
    public void corruptedCustomMovesTest() {
        // Intrusive modes should throw exception about corrupted undoMove
        setSolverConfigCalculatorClass(TestdataDifferentValuesCalculator.class);

        switch (environmentMode) {
            case FULL_ASSERT:
            case FAST_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(TestdataCorruptedUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving("corrupted undoMove");
                break;
            case NON_INTRUSIVE_FULL_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(TestdataCorruptedEntityUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving("not the uncorruptedScore");
                break;
            case REPRODUCIBLE:
            case NON_REPRODUCIBLE:
            case PRODUCTION:
                // No exception expected
                break;
            default:
                Assertions.fail("Environment mode not covered: " + environmentMode);
        }
    }

    @Test
    public void corruptedScoreRulesTest() {
        // For full assert modes it should throw exception about corrupted score
        setSolverConfigCalculatorClass(TestdataCorruptedDifferentValuesCalculator.class);

        switch (environmentMode) {
            case FULL_ASSERT:
            case NON_INTRUSIVE_FULL_ASSERT:
                assertIllegalStateExceptionWhileSolving("not the uncorruptedScore");
                break;
            case FAST_ASSERT:
                assertIllegalStateExceptionWhileSolving("Score corruption analysis could not be generated ");
                break;
            case REPRODUCIBLE:
            case NON_REPRODUCIBLE:
            case PRODUCTION:
                // No exception expected
                break;
            default:
                Assertions.fail("Environment mode not covered: " + environmentMode);
        }
    }

    private void assertReproducibility(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        assertGeneratingSameNumbers(((DefaultSolver) solver1).getRandomFactory(),
                                    ((DefaultSolver) solver2).getRandomFactory());
        assertSameScoreSeries(solver1, solver2);
    }

    private void assertNonReproducibility(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        assertGeneratingDifferentNumbers(((DefaultSolver) solver1).getRandomFactory(),
                                         ((DefaultSolver) solver2).getRandomFactory());
        assertDifferentScoreSeries(solver1, solver2);
    }

    private void assertIllegalStateExceptionWhileSolving(String exceptionMessage) {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> PlannerTestUtils.solve(solverConfig, inputProblem))
                .withMessageContaining(exceptionMessage);
    }

    private void assertSameScoreSeries(Solver<TestdataSolution> solver1, Solver<TestdataSolution> solver2) {
        TestdataStepScoreListener listener = new TestdataStepScoreListener();
        TestdataStepScoreListener listener2 = new TestdataStepScoreListener();

        ((DefaultSolver<TestdataSolution>) solver1).addPhaseLifecycleListener(listener);
        ((DefaultSolver<TestdataSolution>) solver2).addPhaseLifecycleListener(listener2);

        SoftAssertions.assertSoftly(softly -> IntStream.range(0, NUMBER_OF_TIMES_RUN)
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

        SoftAssertions.assertSoftly(softly -> IntStream.range(0, NUMBER_OF_TIMES_RUN)
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

        SoftAssertions.assertSoftly(softly -> IntStream.range(0, NUMBER_OF_RANDOM_NUMBERS_GENERATED)
                .forEach(i -> softly.assertThat(random.nextInt())
                        .as("Random factories should generate the same results "
                                    + "in a reproducible environment mode.")
                        .isEqualTo(random2.nextInt())));
    }

    private void assertGeneratingDifferentNumbers(RandomFactory factory1, RandomFactory factory2) {
        Random random = factory1.createRandom();
        Random random2 = factory2.createRandom();

        SoftAssertions.assertSoftly(softly -> IntStream.range(0, NUMBER_OF_RANDOM_NUMBERS_GENERATED)
                .forEach(i -> softly.assertThat(random.nextInt())
                        .as("Random factories should not generate exactly the same results "
                                    + "in the non-reproducible environment mode. "
                                    + "It can happen but the probability is very low. Run test again")
                        .isNotEqualTo(random2.nextInt())));
    }

    private void setSolverConfigCalculatorClass(Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig()
                                                           .withEasyScoreCalculatorClass(easyScoreCalculatorClass));
    }

    private void setSolverConfigMoveListFactoryClassToCorrupted(Class<? extends MoveListFactory> move) {
        MoveListFactoryConfig moveListFactoryConfig = new MoveListFactoryConfig();
        moveListFactoryConfig.setMoveListFactoryClass(move);

        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig()
                .withCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(moveListFactoryConfig);
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT));

        solverConfig.withPhases(initializerPhaseConfig, localSearchPhaseConfig);
    }
}
