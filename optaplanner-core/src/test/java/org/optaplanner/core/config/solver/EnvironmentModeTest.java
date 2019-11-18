package org.optaplanner.core.config.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import org.optaplanner.core.impl.testdata.heuristic.move.factory.TestdataChangeMoveWithCorruptedUndoMoveFactory;
import org.optaplanner.core.impl.testdata.heuristic.move.factory.TestdataCorruptedEntityUndoMoveFactory;
import org.optaplanner.core.impl.testdata.phase.custom.TestdataFirstValueInitializer;
import org.optaplanner.core.impl.testdata.phase.event.TestdataStepScoreListener;
import org.optaplanner.core.impl.testdata.score.director.TestdataDifferentValuesCalculator;
import org.optaplanner.core.impl.testdata.score.director.TestdataCorruptedDifferentValuesCalculator;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(Parameterized.class)
public class EnvironmentModeTest {

    private static final int NUMBER_OF_RANDOM_NUMBERS_GENERATED = 1000;
    private static final int NUMBER_OF_TIMES_RUN = 10;
    private static final int NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT = 3;

    private final EnvironmentMode environmentMode;
    private static TestdataSolution inputProblem;
    private SolverConfig solverConfig;

    public EnvironmentModeTest(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    @BeforeClass
    public static void setUpInputProblem() {
        inputProblem = new TestdataSolution("s1");
        inputProblem.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        inputProblem.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
    }

    @Before
    public void setUpSolverConfig() {
        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig();
        initializerPhaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

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
                throw new AssertionError("Missing case for " + environmentMode);
        }
    }

    @Test
    public void corruptedCustomMovesTest() {
        // intrusive modes should throw exception about corrupted undoMove
        setSolverConfigCalculatorClass(TestdataDifferentValuesCalculator.class);

        switch (environmentMode) {
            case FULL_ASSERT:
            case FAST_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(TestdataChangeMoveWithCorruptedUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving("corrupted undoMove");
                break;
            case NON_INTRUSIVE_FULL_ASSERT:
                setSolverConfigMoveListFactoryClassToCorrupted(TestdataCorruptedEntityUndoMoveFactory.class);
                assertIllegalStateExceptionWhileSolving("not the uncorruptedScore");
                break;
            case REPRODUCIBLE:
            case NON_REPRODUCIBLE:
            case PRODUCTION:
                // no exception expected
                break;
            default:
                throw new AssertionError("Missing case for " + environmentMode);
        }
    }

    @Test
    public void corruptedScoreRulesTest() {
        // for full assert modes it should throw exception about corrupted score
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
                // no exception expected
                break;
            default:
                throw new AssertionError("Missing case for " + environmentMode);
        }
    }

    private void assertReproducibility(Solver solver1, Solver solver2) {
        assertThat(areGeneratingSameNumbers(
                ((DefaultSolver) solver1).getRandomFactory(),
                ((DefaultSolver) solver2).getRandomFactory())
        )
                .as("Random factories generate different random values. This is 100% wrong.")
                .isTrue();
        assertThat(areScoreSeriesTheSame(solver1, solver2))
                .as("Score steps are different and should be the same!")
                .isTrue();
    }

    private void assertNonReproducibility(Solver solver1, Solver solver2) {
        assertThat(areGeneratingSameNumbers(
                ((DefaultSolver) solver1).getRandomFactory(),
                ((DefaultSolver) solver2).getRandomFactory())
        )
                .as("Random factories generate exactly same results. "
                            + "It can happen but the probability is very low. Run test again")
                .isFalse();
        assertThat(areScoreSeriesTheSame(solver1, solver2))
                .as("Score steps are same and should be different! "
                            + "This might be possible because searchSpace is not infinite and "
                            + "two different random scenarios can have same results. Run test again.")
                .isFalse();
    }

    private void assertIllegalStateExceptionWhileSolving(String exceptionMessage) {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> PlannerTestUtils.solve(solverConfig, inputProblem))
                .withMessageContaining(exceptionMessage);
    }

    private boolean areScoreSeriesTheSame(Solver<TestdataSolution> solver, Solver<TestdataSolution> solver2) {
        boolean areSame = true;

        TestdataStepScoreListener listener = new TestdataStepScoreListener();
        TestdataStepScoreListener listener2 = new TestdataStepScoreListener();

        ((DefaultSolver<TestdataSolution>) solver).addPhaseLifecycleListener(listener);
        ((DefaultSolver<TestdataSolution>) solver2).addPhaseLifecycleListener(listener2);

        for (int i = 0; i < NUMBER_OF_TIMES_RUN && areSame; i++) {
            solver.solve(inputProblem);
            solver2.solve(inputProblem);

            List<Integer> scoreTimeSeries = listener.getScores();
            List<Integer> scoreTimeSeries2 = listener2.getScores();

            areSame = scoreTimeSeries.equals(scoreTimeSeries2);
        }
        return areSame;
    }

    private boolean areGeneratingSameNumbers(RandomFactory f1, RandomFactory f2) {
        boolean areSame = true;
        Random random = f1.createRandom();
        Random random2 = f2.createRandom();
        for (int i = 0; i < EnvironmentModeTest.NUMBER_OF_RANDOM_NUMBERS_GENERATED; i++) {
            if (random.nextInt() != random2.nextInt()) {
                areSame = false;
                break;
            }
        }
        return areSame;
    }

    private void setSolverConfigCalculatorClass(Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig()
                                                           .withEasyScoreCalculatorClass(easyScoreCalculatorClass));
    }

    private void setSolverConfigMoveListFactoryClassToCorrupted(Class<? extends MoveListFactory> move) {
        MoveListFactoryConfig moveListFactoryConfig = new MoveListFactoryConfig();
        moveListFactoryConfig.setMoveListFactoryClass(move);

        CustomPhaseConfig initializerPhaseConfig = new CustomPhaseConfig();
        initializerPhaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(TestdataFirstValueInitializer.class));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(moveListFactoryConfig);
        localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(NUMBER_OF_TERMINATION_STEP_COUNT_LIMIT));

        solverConfig.withPhases(initializerPhaseConfig, localSearchPhaseConfig);
    }
}
