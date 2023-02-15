package org.optaplanner.examples.common.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> type of the {@link SolverFactory} parameter
 */
public abstract class AbstractPhaseTest<Solution_, T> extends LoggingTest {

    protected abstract CommonApp<Solution_> createCommonApp();

    protected abstract Stream<String> unsolvedFileNames();

    protected abstract Stream<T> solverFactoryParams();

    protected abstract SolverFactory<Solution_> buildSolverFactory(
            CommonApp<Solution_> commonApp,
            T solverFactoryParam);

    protected void assertSolution(Solution_ bestSolution) {
        assertThat(bestSolution).isNotNull();
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(600)
    Stream<DynamicContainer> runPhase() {
        CommonApp<Solution_> commonApp = createCommonApp();
        SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File unsolvedDataDir = new File(dataDir, "unsolved");
        return solverFactoryParams().map(solverFactoryParam -> {
            SolverFactory<Solution_> solverFactory = buildSolverFactory(commonApp, solverFactoryParam);
            return dynamicContainer(
                    solverFactoryParam.toString(),
                    unsolvedFileNames().map(unsolvedFileName -> {
                        File dataFile = buildFile(unsolvedDataDir, unsolvedFileName);
                        return dynamicTest(
                                unsolvedFileName,
                                () -> runPhase(solverFactory, readProblem(solutionFileIO, dataFile)));
                    }));
        });
    }

    private void runPhase(SolverFactory<Solution_> solverFactory, Solution_ problem) {
        Solver<Solution_> solver = solverFactory.buildSolver();

        Solution_ bestSolution = solver.solve(problem);
        assertSolution(bestSolution);
        SolutionManager<Solution_, ?> solutionManager = SolutionManager.create(solverFactory);
        assertThat(solutionManager.update(bestSolution)).isNotNull();
    }

    private static File buildFile(File unsolvedDataDir, String unsolvedFileName) {
        File unsolvedFile = new File(unsolvedDataDir, unsolvedFileName);
        if (!unsolvedFile.exists()) {
            throw new IllegalStateException("The directory unsolvedFile (" + unsolvedFile.getAbsolutePath()
                    + ") does not exist.");
        }
        return unsolvedFile;
    }

    private Solution_ readProblem(SolutionFileIO<Solution_> solutionFileIO, File dataFile) {
        Solution_ problem = solutionFileIO.read(dataFile);
        logger.info("Opened: {}", dataFile);
        return problem;
    }
}
