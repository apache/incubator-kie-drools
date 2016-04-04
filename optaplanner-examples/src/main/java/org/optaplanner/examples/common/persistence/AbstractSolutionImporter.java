/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import com.google.common.math.BigIntegerMath;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.business.ProblemFileComparator;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractSolutionImporter<Solution_> extends LoggingMain {

    protected static final String DEFAULT_OUTPUT_FILE_SUFFIX = "xml";

    protected final SolutionDao<Solution_> solutionDao;
    protected final File inputDir;
    protected final File outputDir;

    public AbstractSolutionImporter(SolutionDao<Solution_> solutionDao) {
        this.solutionDao = solutionDao;
        inputDir = new File(solutionDao.getDataDir(), "import");
        if (!inputDir.exists()) {
            throw new IllegalStateException("The directory inputDir (" + inputDir.getAbsolutePath()
                    + ") does not exist.");
        }
        outputDir = new File(solutionDao.getDataDir(), "unsolved");
    }

    public AbstractSolutionImporter(boolean withoutDao) {
        if (!withoutDao) {
            throw new IllegalArgumentException("The parameter withoutDao (" + withoutDao + ") must be true.");
        }
        solutionDao = null;
        inputDir = null;
        outputDir = null;
    }

    public File getInputDir() {
        return inputDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public boolean isInputFileDirectory() {
        return false;
    }

    public abstract String getInputFileSuffix();

    protected String getOutputFileSuffix() {
        return DEFAULT_OUTPUT_FILE_SUFFIX;
    }

    public void convertAll() {
        File[] inputFiles = inputDir.listFiles();
        Arrays.sort(inputFiles, new ProblemFileComparator());
        for (File inputFile : inputFiles) {
            if (acceptInputFile(inputFile) && acceptInputFileDuringBulkConvert(inputFile)) {
                String inputFileName = inputFile.getName();
                String outputFileName = inputFileName.substring(0,
                        inputFileName.length() - getInputFileSuffix().length())
                        + getOutputFileSuffix();
                File outputFile = new File(outputDir, outputFileName);
                convert(inputFile, outputFile);
            }
        }
    }

    public void convert(String inputFileName, String outputFileName) {
        File inputFile = new File(inputDir, inputFileName);
        if (!inputFile.exists()) {
            throw new IllegalStateException("The file inputFile (" + inputFile.getAbsolutePath()
                    + ") does not exist.");
        }
        File outputFile = new File(outputDir, outputFileName);
        outputFile.getParentFile().mkdirs();
        convert(inputFile, outputFile);
    }

    protected void convert(File inputFile, File outputFile) {
        Solution_ solution = readSolution(inputFile);
        solutionDao.writeSolution(solution, outputFile);
    }

    public boolean acceptInputFile(File inputFile) {
        if (isInputFileDirectory()) {
            return inputFile.isDirectory();
        }
        return inputFile.getName().endsWith("." + getInputFileSuffix());
    }

    /**
     * Some files are too big to be serialized to XML or take too long.
     * @param inputFile never null
     * @return true if accepted
     */
    public boolean acceptInputFileDuringBulkConvert(File inputFile) {
        return true;
    }

    public abstract Solution_ readSolution(File inputFile);

    public static abstract class InputBuilder extends LoggingMain {

        public static BigInteger factorial(int base) {
            BigInteger value = BigInteger.ONE;
            for (int i = 1; i <= base; i++) {
                value = value.multiply(BigInteger.valueOf(base));
            }
            return value;
        }

    }

    public static String getFlooredPossibleSolutionSize(BigInteger possibleSolutionSize) {
        if (possibleSolutionSize.compareTo(BigInteger.valueOf(1000L)) < 0) {
            return possibleSolutionSize.toString();
        }
        return "10^" + (BigIntegerMath.log10(possibleSolutionSize, RoundingMode.FLOOR));
    }

}
