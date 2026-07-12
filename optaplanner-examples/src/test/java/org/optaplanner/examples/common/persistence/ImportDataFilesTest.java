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

package org.optaplanner.examples.common.persistence;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingTest;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class ImportDataFilesTest<Solution_> extends LoggingTest {

    protected abstract AbstractSolutionImporter<Solution_> createSolutionImporter();

    protected abstract String getDataDirName();

    protected Predicate<File> dataFileInclusionFilter() {
        return file -> true;
    }

    private static List<File> getInputFiles(String dataDirName, AbstractSolutionImporter<?> solutionImporter) {
        File importDir = new File(CommonApp.determineDataDir(dataDirName), "import");
        return getAllFilesRecursivelyAndSorted(importDir, solutionImporter::acceptInputFile);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    Stream<DynamicTest> readSolution() {
        AbstractSolutionImporter<Solution_> solutionImporter = createSolutionImporter();
        return getInputFiles(getDataDirName(), solutionImporter).stream()
                .filter(dataFileInclusionFilter())
                .map(importFile -> dynamicTest(
                        importFile.getName(),
                        () -> solutionImporter.readSolution(importFile)));
    }
}
