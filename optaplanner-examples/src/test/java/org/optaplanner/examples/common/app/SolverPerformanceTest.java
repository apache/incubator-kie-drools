package org.optaplanner.examples.common.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.common.TestSystemProperties;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * Runs an example {@link Solver}.
 * <p>
 * A test should run in less than 10 seconds on a 3 year old desktop computer, choose the bestScoreLimit accordingly.
 * Always use a {@link Timeout} on {@link Test}, preferably 10 minutes because some of the Jenkins machines are old.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolverPerformanceTest<Solution_, Score_ extends Score<Score_>> extends LoggingTest {

    private static final String MOVE_THREAD_COUNTS_STRING = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNTS);

    protected SolutionFileIO<Solution_> solutionFileIO;
    protected String solverConfigResource;

    private static Stream<String> moveThreadCounts() {
        return Optional.ofNullable(MOVE_THREAD_COUNTS_STRING)
                .map(s -> Arrays.stream(s.split(",")))
                .orElse(Stream.of(SolverConfig.MOVE_THREAD_COUNT_NONE));
    }

    protected TestData<Score_> testData(String unsolvedDataFile, Score_ bestScoreLimit, EnvironmentMode environmentMode) {
        return new TestData<>(unsolvedDataFile, bestScoreLimit, environmentMode);
    }

    @TestFactory
    @Timeout(600)
    Stream<DynamicTest> runSpeedTest() {
        return moveThreadCounts().flatMap(moveThreadCount -> testData().map(testData -> dynamicTest(
                testData.unsolvedDataFile.replaceFirst(".*/", "")
                        + ", "
                        + testData.environmentMode
                        + ", threads: " + moveThreadCount,
                () -> runSpeedTest(
                        new File(testData.unsolvedDataFile),
                        testData.bestScoreLimit,
                        testData.environmentMode,
                        moveThreadCount))));
    }

    @BeforeEach
    public void setUp() {
        CommonApp<Solution_> commonApp = createCommonApp();
        solutionFileIO = commonApp.createSolutionFileIO();
        solverConfigResource = commonApp.getSolverConfigResource();
    }

    protected abstract CommonApp<Solution_> createCommonApp();

    protected abstract Stream<TestData<Score_>> testData();

    private void runSpeedTest(File unsolvedDataFile, Score_ bestScoreLimit, EnvironmentMode environmentMode,
            String moveThreadCount) {
        SolverFactory<Solution_> solverFactory = buildSolverFactory(bestScoreLimit, environmentMode, moveThreadCount);
        Solution_ problem = solutionFileIO.read(unsolvedDataFile);
        logger.info("Opened: {}", unsolvedDataFile);
        Solver<Solution_> solver = solverFactory.buildSolver();
        Solution_ bestSolution = solver.solve(problem);
        assertScoreAndConstraintMatches(solverFactory, bestSolution, bestScoreLimit);
    }

    private SolverFactory<Solution_> buildSolverFactory(Score_ bestScoreLimit, EnvironmentMode environmentMode,
            String moveThreadCount) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        solverConfig.withEnvironmentMode(environmentMode)
                .withTerminationConfig(new TerminationConfig()
                        .withBestScoreLimit(bestScoreLimit.toString()))
                .withMoveThreadCount(moveThreadCount);
        return SolverFactory.create(solverConfig);
    }

    private void assertScoreAndConstraintMatches(SolverFactory<Solution_> solverFactory, Solution_ bestSolution,
            Score_ bestScoreLimit) {
        assertThat(bestSolution).isNotNull();
        ScoreManager<Solution_, Score_> scoreManager = ScoreManager.create(solverFactory);
        Score_ bestScore = scoreManager.updateScore(bestSolution);
        assertThat(bestScore)
                .as("The bestScore (" + bestScore + ") must be at least the bestScoreLimit (" + bestScoreLimit + ").")
                .isGreaterThanOrEqualTo(bestScoreLimit);

        ScoreExplanation<Solution_, Score_> scoreExplanation = scoreManager.explainScore(bestSolution);
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

        private final String unsolvedDataFile;
        private final Score_ bestScoreLimit;
        private final EnvironmentMode environmentMode;

        private TestData(String unsolvedDataFile, Score_ bestScoreLimit, EnvironmentMode environmentMode) {
            this.unsolvedDataFile = unsolvedDataFile;
            this.bestScoreLimit = bestScoreLimit;
            this.environmentMode = environmentMode;
        }
    }
}
