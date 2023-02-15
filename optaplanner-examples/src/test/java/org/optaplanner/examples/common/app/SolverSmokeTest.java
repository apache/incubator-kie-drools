package org.optaplanner.examples.common.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.optaplanner.core.config.solver.EnvironmentMode.FULL_ASSERT;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testutil.DisabledInProductization;
import org.optaplanner.examples.common.TestSystemProperties;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * Runs an example {@link Solver}.
 * <p>
 * A test should run in ~5 seconds, choose the bestScoreLimit accordingly.
 * Always use a {@link Timeout} on {@link Test}, preferably 10 minutes as some CI nodes are slow.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolverSmokeTest<Solution_, Score_ extends Score<Score_>> extends LoggingTest {

    private static final String MOVE_THREAD_COUNTS_STRING = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNTS);

    protected SolutionFileIO<Solution_> solutionFileIO;
    protected String solverConfigResource;

    private static Stream<String> moveThreadCounts() {
        return Optional.ofNullable(MOVE_THREAD_COUNTS_STRING)
                .map(s -> Arrays.stream(s.split(",")))
                .orElse(Stream.of(SolverConfig.MOVE_THREAD_COUNT_NONE));
    }

    @BeforeEach
    public void setUp() {
        CommonApp<Solution_> commonApp = createCommonApp();
        solutionFileIO = commonApp.createSolutionFileIO();
        solverConfigResource = commonApp.getSolverConfigResource();
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(600)
    Stream<DynamicTest> runSpeedTest() {
        return runSpeedTest(ConstraintStreamImplType.DROOLS);
    }

    @DisabledInProductization
    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(600)
    Stream<DynamicTest> runSpeedTestBavet() {
        return runSpeedTest(ConstraintStreamImplType.BAVET);
    }

    private Stream<DynamicTest> runSpeedTest(ConstraintStreamImplType constraintStreamImplType) {
        return moveThreadCounts()
                .flatMap(moveThreadCount -> testData()
                        .filter(testData -> testData.constraintStreamImplType == constraintStreamImplType)
                        .flatMap(testData -> {
                            Stream.Builder<DynamicTest> streamBuilder = Stream.builder();
                            streamBuilder.add(createSpeedTest(testData.constraintStreamImplType, testData.unsolvedDataFile,
                                    EnvironmentMode.REPRODUCIBLE,
                                    testData.bestScoreLimitForReproducible, moveThreadCount));
                            if (testData.bestScoreLimitForFastAssert != null) {
                                streamBuilder.add(createSpeedTest(testData.constraintStreamImplType, testData.unsolvedDataFile,
                                        EnvironmentMode.FAST_ASSERT,
                                        testData.bestScoreLimitForFastAssert, moveThreadCount));
                            }
                            if (testData.bestScoreLimitForFullAssert != null) {
                                streamBuilder.add(createSpeedTest(testData.constraintStreamImplType, testData.unsolvedDataFile,
                                        FULL_ASSERT,
                                        testData.bestScoreLimitForFullAssert, moveThreadCount));
                            }
                            return streamBuilder.build();
                        }));
    }

    private DynamicTest createSpeedTest(ConstraintStreamImplType constraintStreamImplType, String unsolvedDataFile,
            EnvironmentMode environmentMode, Score_ bestScoreLimit, String moveThreadCount) {
        String testName = constraintStreamImplType + ", " +
                unsolvedDataFile.replaceFirst(".*/", "")
                + ", "
                + environmentMode
                + ", threads: " + moveThreadCount;
        return dynamicTest(testName,
                () -> runSpeedTest(constraintStreamImplType,
                        new File(unsolvedDataFile),
                        bestScoreLimit,
                        environmentMode,
                        moveThreadCount));
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(600)
    @DisabledInProductization
    Stream<DynamicTest> runConstraintStreamsMutualCorrectnessTest() {
        SolverFactory<Solution_> solverFactory = buildMutualCorrectnessSolverFactory();
        return testData()
                .map(testData -> new File(testData.unsolvedDataFile))
                .distinct()
                .map(inputSolutionFile -> createConstraintStreamsMutualCorrectnessTest(solverFactory, inputSolutionFile));
    }

    private DynamicTest createConstraintStreamsMutualCorrectnessTest(SolverFactory<Solution_> solverFactory,
            File unsolvedDataFile) {
        String testName = "DROOLS v. BAVET, " +
                unsolvedDataFile.toString().replaceFirst(".*/", "")
                + ", "
                + FULL_ASSERT;
        return dynamicTest(testName,
                () -> runConstraintStreamsMutualCorrectnessTest(solverFactory, unsolvedDataFile));
    }

    protected abstract CommonApp<Solution_> createCommonApp();

    protected abstract Stream<TestData<Score_>> testData();

    private void runSpeedTest(ConstraintStreamImplType constraintStreamImplType, File unsolvedDataFile, Score_ bestScoreLimit,
            EnvironmentMode environmentMode, String moveThreadCount) {
        SolverFactory<Solution_> solverFactory =
                buildSpeedSolverFactory(constraintStreamImplType, bestScoreLimit, environmentMode, moveThreadCount);
        Solution_ problem = solutionFileIO.read(unsolvedDataFile);
        logger.info("Opened: {}", unsolvedDataFile);
        Solver<Solution_> solver = solverFactory.buildSolver();
        Solution_ bestSolution = solver.solve(problem);
        assertScoreAndConstraintMatches(solverFactory, bestSolution, bestScoreLimit);
    }

    private SolverFactory<Solution_> buildSpeedSolverFactory(ConstraintStreamImplType constraintStreamImplType,
            Score_ bestScoreLimit, EnvironmentMode environmentMode, String moveThreadCount) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        solverConfig.withEnvironmentMode(environmentMode)
                .withTerminationConfig(new TerminationConfig()
                        .withBestScoreLimit(bestScoreLimit.toString()))
                .withMoveThreadCount(moveThreadCount);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                Objects.requireNonNullElseGet(solverConfig.getScoreDirectorFactoryConfig(),
                        ScoreDirectorFactoryConfig::new);
        if (scoreDirectorFactoryConfig.getConstraintProviderClass() == null) {
            Assertions.fail("Test does not support constraint streams.");
        }
        scoreDirectorFactoryConfig.setConstraintStreamImplType(constraintStreamImplType);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        return SolverFactory.create(solverConfig);
    }

    private void runConstraintStreamsMutualCorrectnessTest(SolverFactory<Solution_> solverFactory, File unsolvedDataFile) {
        Solution_ problem = solutionFileIO.read(unsolvedDataFile);
        logger.info("Opened: {}", unsolvedDataFile);
        Solver<Solution_> solver = solverFactory.buildSolver();
        Assertions.assertThatNoException()
                .isThrownBy(() -> solver.solve(problem));
    }

    private SolverFactory<Solution_> buildMutualCorrectnessSolverFactory() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        solverConfig.withEnvironmentMode(FULL_ASSERT)
                .withTerminationConfig(new TerminationConfig()
                        .withSecondsSpentLimit(10L)); // 10 seconds is not nearly enough; but better than nothing.
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                Objects.requireNonNullElseGet(solverConfig.getScoreDirectorFactoryConfig(),
                        ScoreDirectorFactoryConfig::new);
        if (scoreDirectorFactoryConfig.getConstraintProviderClass() == null) {
            Assertions.fail("Test does not support constraint streams.");
        }
        scoreDirectorFactoryConfig.setConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
        ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        assertionScoreDirectorFactoryConfig.setConstraintProviderClass(scoreDirectorFactoryConfig.getConstraintProviderClass());
        assertionScoreDirectorFactoryConfig.setConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        scoreDirectorFactoryConfig.setAssertionScoreDirectorFactory(assertionScoreDirectorFactoryConfig);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        return SolverFactory.create(solverConfig);
    }

    private void assertScoreAndConstraintMatches(SolverFactory<Solution_> solverFactory, Solution_ bestSolution,
            Score_ bestScoreLimit) {
        assertThat(bestSolution).isNotNull();
        SolutionManager<Solution_, Score_> solutionManager = SolutionManager.create(solverFactory);
        Score_ bestScore = solutionManager.update(bestSolution);
        assertThat(bestScore)
                .as("The bestScore (" + bestScore + ") must be at least the bestScoreLimit (" + bestScoreLimit + ").")
                .isGreaterThanOrEqualTo(bestScoreLimit);

        ScoreExplanation<Solution_, Score_> scoreExplanation = solutionManager.explain(bestSolution);
        Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotals =
                scoreExplanation.getConstraintMatchTotalMap();
        assertThat(constraintMatchTotals).isNotNull();
        assertThat(constraintMatchTotals.values().stream()
                .map(ConstraintMatchTotal::getScore)
                .reduce(Score::add)
                .orElse(bestScore.zero()))
                .isEqualTo(scoreExplanation.getScore());
        assertThat(scoreExplanation.getIndictmentMap()).isNotNull();
    }

    protected static class TestData<Score_ extends Score<Score_>> {

        public static <Score_ extends Score<Score_>> TestData<Score_> of(ConstraintStreamImplType constraintStreamImplType,
                String unsolvedDataFile, Score_ bestScoreLimitForReproducible) {
            return TestData.of(constraintStreamImplType, unsolvedDataFile, bestScoreLimitForReproducible, null);
        }

        public static <Score_ extends Score<Score_>> TestData<Score_> of(ConstraintStreamImplType constraintStreamImplType,
                String unsolvedDataFile, Score_ bestScoreLimitForReproducible, Score_ bestScoreLimitForFastAssert) {
            return TestData.of(constraintStreamImplType, unsolvedDataFile, bestScoreLimitForReproducible,
                    bestScoreLimitForFastAssert, null);
        }

        public static <Score_ extends Score<Score_>> TestData<Score_> of(ConstraintStreamImplType constraintStreamImplType,
                String unsolvedDataFile, Score_ bestScoreLimitForReproducible, Score_ bestScoreLimitForFastAssert,
                Score_ bestScoreLimitForFullAssert) {
            return new TestData<>(constraintStreamImplType, unsolvedDataFile, bestScoreLimitForReproducible,
                    bestScoreLimitForFastAssert, bestScoreLimitForFullAssert);
        }

        private final ConstraintStreamImplType constraintStreamImplType;
        private final String unsolvedDataFile;
        private final Score_ bestScoreLimitForReproducible;
        private final Score_ bestScoreLimitForFastAssert;
        private final Score_ bestScoreLimitForFullAssert;

        private TestData(ConstraintStreamImplType constraintStreamImplType, String unsolvedDataFile,
                Score_ bestScoreLimitForReproducible, Score_ bestScoreLimitForFastAssert,
                Score_ bestScoreLimitForFullAssert) {
            this.constraintStreamImplType = constraintStreamImplType;
            this.unsolvedDataFile = unsolvedDataFile;
            this.bestScoreLimitForReproducible = Objects.requireNonNull(bestScoreLimitForReproducible);
            this.bestScoreLimitForFastAssert = bestScoreLimitForFastAssert;
            this.bestScoreLimitForFullAssert = bestScoreLimitForFullAssert;
        }
    }
}
