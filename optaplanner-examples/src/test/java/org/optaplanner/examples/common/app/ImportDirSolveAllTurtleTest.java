/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class ImportDirSolveAllTurtleTest<Solution_> extends SolveAllTurtleTest<Solution_> {

    private static <Solution_> List<File> getImportDirFiles(CommonApp<Solution_> commonApp) {
        try (SolutionBusiness<Solution_, ?> solutionBusiness = commonApp.createSolutionBusiness()) {
            File importDataDir = solutionBusiness.getImportDataDir();
            if (!importDataDir.exists()) {
                throw new IllegalStateException("The directory importDataDir (" + importDataDir.getAbsolutePath()
                        + ") does not exist.");
            } else {
                return getAllFilesRecursivelyAndSorted(importDataDir, createSolutionImporter(commonApp)::acceptInputFile);
            }
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
