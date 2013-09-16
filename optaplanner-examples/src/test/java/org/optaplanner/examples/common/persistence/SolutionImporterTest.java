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
import org.optaplanner.examples.common.business.ExtensionFileFilter;
import org.optaplanner.examples.common.business.ProblemFileComparator;

@RunWith(Parameterized.class)
public abstract class SolutionImporterTest extends LoggingTest {

    protected static Collection<Object[]> getInputFilesAsParameters(AbstractSolutionImporter solutionImporter) {
        List<File> fileList = new ArrayList<File>(0);
        File inputDir = solutionImporter.getInputDir();
        if (!inputDir.exists()) {
            throw new IllegalStateException("The directory inputDir (" + inputDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            addFiles(solutionImporter, fileList, inputDir);
        }
        Collections.sort(fileList, new ProblemFileComparator());
        List<Object[]> filesAsParameters = new ArrayList<Object[]>();
        for (File file : fileList) {
            filesAsParameters.add(new Object[]{file});
        }
        return filesAsParameters;
    }

    private static void addFiles(final AbstractSolutionImporter solutionImporter, List<File> fileList, File directory) {
        List<File> newFileList = Arrays.asList(directory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return solutionImporter.acceptInputFile(file);
            }
        }));
        fileList.addAll(newFileList);
        for (File subDirectory : directory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE)) {
            addFiles(solutionImporter, fileList, subDirectory);
        }
    }

    protected AbstractSolutionImporter solutionImporter;

    protected File importFile;

    protected SolutionImporterTest(File importFile) {
        this.importFile = importFile;
    }

    @Before
    public void setUp() {
        solutionImporter = createSolutionImporter();
    }

    protected abstract AbstractSolutionImporter createSolutionImporter();

    @Test
    public void readSolution() {
        solutionImporter.readSolution(importFile);
    }

}
