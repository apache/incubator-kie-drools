/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.common.persistence;

import java.io.File;
import java.util.Arrays;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.app.LoggingMain;

public abstract class AbstractSolutionImporter extends LoggingMain {

    protected static final String DEFAULT_OUTPUT_FILE_SUFFIX = ".xml";

    protected SolutionDao solutionDao;

    public AbstractSolutionImporter(SolutionDao solutionDao) {
        this.solutionDao = solutionDao;
    }

    protected File getInputDir() {
        return new File(solutionDao.getDataDir(), "input");
    }

    protected abstract String getInputFileSuffix();

    protected File getOutputDir() {
        return new File(solutionDao.getDataDir(), "unsolved");
    }

    protected String getOutputFileSuffix() {
        return DEFAULT_OUTPUT_FILE_SUFFIX;
    }

    public void convertAll() {
        File inputDir = getInputDir();
        File outputDir = getOutputDir();
        File[] inputFiles = inputDir.listFiles();
        if (inputFiles == null) {
            throw new IllegalArgumentException(
                    "Your working dir should be drools-planner-examples and contain: " + inputDir);
        }
        Arrays.sort(inputFiles);
        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            if (inputFileName.endsWith(getInputFileSuffix())) {
                Solution solution = readSolution(inputFile);
                String outputFileName = inputFileName.substring(0,
                        inputFileName.length() - getInputFileSuffix().length())
                        + getOutputFileSuffix();
                File outputFile = new File(outputDir, outputFileName);
                solutionDao.writeSolution(solution, outputFile);
            }
        }
    }

    public abstract Solution readSolution(File inputFile);

}
