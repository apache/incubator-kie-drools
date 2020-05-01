/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        AbstractSolutionImporter[] importers = commonApp.createSolutionImporters();
        if (importers.length != 1) {
            throw new IllegalStateException("The importers size (" + importers.length + ") should be 1.");
        }
        return importers[0];
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
