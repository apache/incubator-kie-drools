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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class ImportDirSolveAllTurtleTest<Solution_> extends SolveAllTurtleTest<Solution_> {

    protected static Collection<Object[]> getImportDirFilesAsParameters(AbstractSolutionImporter solutionImporter) {
        List<Object[]> filesAsParameters = new ArrayList<>();
        File importDataDir = solutionImporter.getInputDir();
        if (!importDataDir.exists()) {
            throw new IllegalStateException("The directory importDataDir (" + importDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            List<File> fileList = new ArrayList<>(
                    FileUtils.listFiles(importDataDir, new String[]{solutionImporter.getInputFileSuffix()}, true));
            Collections.sort(fileList, new ProblemFileComparator());
            for (File file : fileList) {
                filesAsParameters.add(new Object[]{file});
            }
        }
        return filesAsParameters;
    }

    protected File dataFile;
    protected AbstractSolutionImporter<Solution_> solutionImporter;

    protected ImportDirSolveAllTurtleTest(File dataFile) {
        this.dataFile = dataFile;
    }

    @Before
    public void setUp() {
        solutionImporter = createSolutionImporter();
    }

    protected abstract AbstractSolutionImporter<Solution_> createSolutionImporter();

    @Override
    protected Solution_ readPlanningProblem() {
        return solutionImporter.readSolution(dataFile);
    }

}
