package org.optaplanner.examples.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class OpenDataFilesTest<Solution_> extends LoggingTest {

    protected abstract CommonApp<Solution_> createCommonApp();

    private List<File> getSolutionFiles(CommonApp<Solution_> commonApp) {
        try (SolutionBusiness<Solution_, ?> solutionBusiness = commonApp.createSolutionBusiness()) {
            File unsolvedDataDir = solutionBusiness.getUnsolvedDataDir();
            if (!unsolvedDataDir.exists()) {
                throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                        + ") does not exist.");
            }
            SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
            String inputFileExtension = solutionFileIO.getInputFileExtension();
            List<File> fileList = new ArrayList<>(
                    getAllFilesRecursivelyAndSorted(unsolvedDataDir, file -> file.getName().endsWith(inputFileExtension)));
            File solvedDataDir = solutionBusiness.getSolvedDataDir();
            if (solvedDataDir.exists()) {
                String outputFileExtension = solutionFileIO.getOutputFileExtension();
                fileList.addAll(
                        getAllFilesRecursivelyAndSorted(solvedDataDir, file -> file.getName().endsWith(outputFileExtension)));
            }
            fileList.sort(new ProblemFileComparator());
            return fileList;
        }
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    Stream<DynamicTest> readAndWriteSolution() {
        CommonApp<Solution_> commonApp = createCommonApp();
        SolverFactory<Solution_> solverFactory =
                SolverFactory.createFromXmlResource(commonApp.getSolverConfigResource());
        SolutionManager<Solution_, ?> solutionManager = SolutionManager.create(solverFactory);
        return getSolutionFiles(commonApp).stream()
                .map(solutionFile -> dynamicTest(
                        solutionFile.getName(),
                        () -> readAndWriteSolution(solutionManager, commonApp.createSolutionFileIO(), solutionFile)));
    }

    private <Score_ extends Score<Score_>> void readAndWriteSolution(SolutionManager<Solution_, Score_> solutionManager,
            SolutionFileIO<Solution_> solutionFileIO, File solutionFile) {
        // Make sure we can process the solution from an existing file.
        Solution_ originalSolution = solutionFileIO.read(solutionFile);
        logger.info("Opened: {}", solutionFile);
        Score_ originalScore = solutionManager.update(originalSolution);
        assertThat(originalScore).isNotNull();
        // Write the solution to a temp file and read it back.
        Solution_ roundTripSolution = null;
        try {
            File tmpFile = File.createTempFile("optaplanner-solution", ".tmp");
            solutionFileIO.write(originalSolution, tmpFile);
            logger.info("Written: {}", tmpFile);
            roundTripSolution = solutionFileIO.read(tmpFile);
            logger.info("Re-opened: {}", tmpFile);
            tmpFile.delete();
        } catch (Exception ex) {
            Assertions.fail("Failed to write solution.", ex);
        }
        // Make sure the solutions equal by checking their scores against each other.
        Score_ roundTripScore = solutionManager.update(roundTripSolution);
        assertThat(roundTripScore).isEqualTo(originalScore);
    }

}
