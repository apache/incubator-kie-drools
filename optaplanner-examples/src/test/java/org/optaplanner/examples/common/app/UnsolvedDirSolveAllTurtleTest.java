package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class UnsolvedDirSolveAllTurtleTest<Solution_> extends SolveAllTurtleTest<Solution_> {

    private static <Solution_> List<File> getUnsolvedDirFiles(CommonApp<Solution_> commonApp) {
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            String inputFileExtension = commonApp.createSolutionFileIO().getInputFileExtension();
            List<File> fileList = new ArrayList<>(
                    FileUtils.listFiles(unsolvedDataDir, new String[] { inputFileExtension }, true));
            fileList.sort(new ProblemFileComparator());
            return fileList;
        }
    }

    @Override
    protected List<File> getSolutionFiles(CommonApp<Solution_> commonApp) {
        return getUnsolvedDirFiles(commonApp);
    }

    @Override
    protected ProblemFactory<Solution_> createProblemFactory(CommonApp<Solution_> commonApp) {
        SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
        return (dataFile) -> {
            Solution_ problem = solutionFileIO.read(dataFile);
            logger.info("Opened: {}", dataFile);
            return problem;
        };
    }
}
