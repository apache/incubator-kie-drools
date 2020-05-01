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

package org.optaplanner.examples.common.persistence;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.common.business.ProblemFileComparator;
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
    Stream<DynamicTest> readSolution() {
        CommonApp<Solution_> commonApp = createCommonApp();
        SolutionFileIO<Solution_> solutionFileIO = commonApp.createSolutionFileIO();
        return getSolutionFiles(commonApp).stream()
                .map(solutionFile -> dynamicTest(
                        solutionFile.getName(),
                        () -> readSolution(solutionFileIO, solutionFile)));
    }

    private void readSolution(SolutionFileIO<Solution_> solutionFileIO, File solutionFile) {
        solutionFileIO.read(solutionFile);
        logger.info("Opened: {}", solutionFile);
    }
}
