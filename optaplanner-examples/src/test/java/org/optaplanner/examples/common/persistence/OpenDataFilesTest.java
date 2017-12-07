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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class OpenDataFilesTest<Solution_> extends LoggingTest {

    protected static <Solution_> Collection<Object[]> getSolutionFilesAsParameters(CommonApp<Solution_> commonApp) {
        List<File> fileList = new ArrayList<>(0);
        File dataDir = CommonApp.determineDataDir(commonApp.getDataDirName());
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        }
        String inputFileExtension = commonApp.createSolutionFileIO().getInputFileExtension();
        fileList.addAll(
                FileUtils.listFiles(unsolvedDataDir, new String[]{inputFileExtension}, true));
        File solvedDataDir = new File(dataDir, "solved");
        if (solvedDataDir.exists()) {
            String outputFileExtension = commonApp.createSolutionFileIO().getOutputFileExtension();
            fileList.addAll(
                    FileUtils.listFiles(solvedDataDir, new String[]{outputFileExtension}, true));
        }
        fileList.sort(new ProblemFileComparator());
        List<Object[]> filesAsParameters = new ArrayList<>();
        for (File file : fileList) {
            filesAsParameters.add(new Object[]{file});
        }
        return filesAsParameters;
    }

    protected final CommonApp<Solution_> commonApp;
    protected final File solutionFile;

    protected SolutionFileIO<Solution_> solutionFileIO;

    protected OpenDataFilesTest(CommonApp<Solution_> commonApp, File solutionFile) {
        this.commonApp = commonApp;
        this.solutionFile = solutionFile;
    }

    @Before
    public void setUp() {
        solutionFileIO = commonApp.createSolutionFileIO();
    }

    @Test
    public void readSolution() {
        solutionFileIO.read(solutionFile);
        logger.info("Opened: {}", solutionFile);
    }

}
