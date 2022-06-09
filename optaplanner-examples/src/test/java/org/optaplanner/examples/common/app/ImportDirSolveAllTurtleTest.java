package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class ImportDirSolveAllTurtleTest<Solution_> extends SolveAllTurtleTest<Solution_> {

    private static <Solution_> List<File> getImportDirFiles(CommonApp<Solution_> commonApp) {
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File importDataDir = new File(dataDir, "import");
        if (!importDataDir.exists()) {
            throw new IllegalStateException("The directory importDataDir (" + importDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            String inputFileSuffix = createSolutionImporter(commonApp).getInputFileSuffix();
            List<File> fileList = new ArrayList<>(
                    FileUtils.listFiles(importDataDir, new String[] { inputFileSuffix }, true));
            fileList.sort(new ProblemFileComparator());
            return fileList;
        }
    }

    private static <Solution_> AbstractSolutionImporter<Solution_> createSolutionImporter(CommonApp<Solution_> commonApp) {
        Set<AbstractSolutionImporter<Solution_>> importers = commonApp.createSolutionImporters();
        if (importers.size() != 1) {
            throw new IllegalStateException("The importers size (" + importers.size() + ") should be 1.");
        }
        return importers.stream()
                .findFirst()
                .orElseThrow();
    }

    @Override
    protected List<File> getSolutionFiles(CommonApp<Solution_> commonApp) {
        return getImportDirFiles(commonApp);
    }

    @Override
    protected ProblemFactory<Solution_> createProblemFactory(CommonApp<Solution_> commonApp) {
        AbstractSolutionImporter<Solution_> solutionImporter = createSolutionImporter(commonApp);
        return solutionImporter::readSolution;
    }
}
