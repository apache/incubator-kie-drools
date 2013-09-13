/*
 * Copyright 2013 JBoss Inc
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.business.SolutionFileFilter;

@RunWith(Parameterized.class)
public abstract class SolutionDaoTest extends LoggingTest {

    protected static Collection<Object[]> getSolutionFilesAsParameters(SolutionDao solutionDao) {
        List<File> fileList = new ArrayList<File>(0);
        File dataDir = solutionDao.getDataDir();
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            addFiles(solutionDao, fileList, unsolvedDataDir);
        }
        File solvedDataDir = new File(dataDir, "solved");
        if (solvedDataDir.exists()) {
            addFiles(solutionDao, fileList, solvedDataDir);
        }
        Collections.sort(fileList, new ProblemFileComparator());
        List<Object[]> filesAsParameters = new ArrayList<Object[]>();
        for (File file : fileList) {
            filesAsParameters.add(new Object[]{file});
        }
        return filesAsParameters;
    }

    private static void addFiles(SolutionDao solutionDao, List<File> fileList, File directory) {
        List<File> newFileList = Arrays.asList(directory.listFiles(new SolutionFileFilter(solutionDao)));
        fileList.addAll(newFileList);
        for (File subDirectory : directory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE)) {
            addFiles(solutionDao, fileList, subDirectory);
        }
    }

    protected SolutionDao solutionDao;

    protected File solutionFile;

    protected SolutionDaoTest(File solutionFile) {
        this.solutionFile = solutionFile;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
    }

    protected abstract SolutionDao createSolutionDao();

    @Test
    public void readSolution() {
        solutionDao.readSolution(solutionFile);
    }

}
