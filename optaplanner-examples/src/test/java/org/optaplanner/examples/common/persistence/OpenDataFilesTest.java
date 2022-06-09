package org.optaplanner.examples.common.persistence;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
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

    private static List<File> getSolutionFiles(CommonApp<?> commonApp) {
        List<File> fileList = new ArrayList<>(0);
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        }
        String inputFileExtension = commonApp.createSolutionFileIO().getInputFileExtension();
        fileList.addAll(
                FileUtils.listFiles(unsolvedDataDir, new String[] { inputFileExtension }, true));
        File solvedDataDir = new File(dataDir, "solved");
        if (solvedDataDir.exists()) {
            String outputFileExtension = commonApp.createSolutionFileIO().getOutputFileExtension();
            fileList.addAll(
                    FileUtils.listFiles(solvedDataDir, new String[] { outputFileExtension }, true));
        }
        fileList.sort(new ProblemFileComparator());
        return fileList;
    }

    @TestFactory
    Stream<DynamicTest> readAndWriteSolution() {
        CommonApp<Solution_> commonApp = createCommonApp();
        SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
        SolutionBusiness<Solution_, ?> solutionBusiness = commonApp.createSolutionBusiness();
        return getSolutionFiles(commonApp).stream()
                .map(solutionFile -> dynamicTest(
                        solutionFile.getName(),
                        () -> readAndWriteSolution(solutionBusiness, solutionFileIO, solutionFile)));
    }

    private <Score_ extends Score<Score_>> void readAndWriteSolution(SolutionBusiness<Solution_, Score_> solutionBusiness,
            SolutionFileIO<Solution_> solutionFileIO, File solutionFile) {
        // Make sure we can process the solution from an existing file.
        Solution_ originalSolution = solutionFileIO.read(solutionFile);
        logger.info("Opened: {}", solutionFile);
        Score_ originalScore = calculateScore(solutionBusiness, originalSolution);
        Assertions.assertThat(originalScore).isNotNull();
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
        Score_ roundTripScore = calculateScore(solutionBusiness, roundTripSolution);
        Assertions.assertThat(roundTripScore).isEqualTo(originalScore);
    }

    private <Score_ extends Score<Score_>> Score_ calculateScore(SolutionBusiness<Solution_, Score_> solutionBusiness,
            Solution_ solution) {
        solutionBusiness.setSolution(Objects.requireNonNull(solution));
        return solutionBusiness.getScore();
    }

}
