/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.conferencescheduling.solver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

public class ConferenceSchedulingScoreRulesXlsxTest {

    private ConferenceSolution currentSolution;
    private String currentPackage;
    private String currentConstraint;
    private int expectedHardScore, expectedSoftScore;
    private final String testFileName = "org/optaplanner/examples/conferencescheduling/testConferenceSchedulingScoreRules.xlsx";
    private HardSoftScoreVerifier<ConferenceSolution> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG));
    private testConferenceSchedulingScoreRulesReader testFileReader;

    @Before
    public void setup() {
        try (InputStream in = new BufferedInputStream(new FileInputStream(testFileName))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            this.testFileReader = new testConferenceSchedulingScoreRulesReader(workbook);
            this.currentSolution = testFileReader.read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                                                    + testFileName + ").", e);
        }
        this.expectedHardScore = currentSolution.getScore().getHardScore();
        this.expectedSoftScore = currentSolution.getScore().getSoftScore();
    }

    @Test
    public void testRules() {
        while ((currentSolution = testFileReader.nextSolution()) != null) {
            scoreVerifier.assertHardWeight(currentPackage, currentConstraint, expectedHardScore, currentSolution);
            scoreVerifier.assertHardWeight(currentPackage, currentConstraint, expectedSoftScore, currentSolution);
        }
    }

    private class testConferenceSchedulingScoreRulesReader extends AbstractXlsxSolutionFileIO.AbstractXslxReader<ConferenceSolution> {

        private int numberOfSheets, currentSheetIndex;
        Map<Integer, String> cellColumnIndexToDateStringMap;
        Map<Integer, String> cellColumnIndexToTimeStringMap;
        private final FieldAccessingSolutionCloner<ConferenceSolution> solutionCloner = new FieldAccessingSolutionCloner<>(new SolutionDescriptor<>(ConferenceSolution.class));

        public testConferenceSchedulingScoreRulesReader(XSSFWorkbook workbook) {
            super(workbook);
            this.numberOfSheets = workbook.getNumberOfSheets();
            this.currentSheetIndex = 0;
        }

        @Override
        public ConferenceSolution read() {
            return solution;
        }

        public ConferenceSolution nextSolution() {
            if (currentSheetIndex >= numberOfSheets) {
                return null;
            }
            nextSheet(workbook.getSheetName(currentSheetIndex++));

            ConferenceSolution nextSheetSolution = null;
            try {
                nextSheetSolution = (ConferenceSolution) clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            nextRow(false);
            readHeaderCell("Constraint package");
            currentPackage = nextStringCell().getStringCellValue();
            nextRow(false);
            readHeaderCell("Constraint name");
            currentConstraint = nextStringCell().getStringCellValue();
            nextRow(false);
            readHeaderCell("Score");
            String[] scoresAsString = nextStringCell().getStringCellValue().split("/");
            expectedHardScore = Integer.parseInt(scoresAsString[0]);
            expectedSoftScore = Integer.parseInt(scoresAsString[1]);

            nextRow();
            cellColumnIndexToDateStringMap = readTimeslotDays();
            nextRow(false);
            readHeaderCell("Room");
            cellColumnIndexToTimeStringMap = readTiemslotHours();
            while (nextRow()) {

            }

            return nextSheetSolution;
        }

        private Map<Integer, String> readTimeslotDays() {
            Map<Integer, String> cellIndexToDateStringMap = new HashMap<>();
            XSSFCell cell;
            while ((cell = currentRow == null ? null : nextStringCell()) != null) {
                if (cell != null) {

                    cellIndexToDateStringMap.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }
            return cellIndexToDateStringMap;
        }

        private Map<Integer, String> readTiemslotHours() {
            Map<Integer, String> cellIndexToTimeStringMap = new HashMap<>();
            XSSFCell cell;
            while ((cell = currentRow == null ? null : nextStringCell()) != null) {
                if (cell != null) {
                    cellIndexToTimeStringMap.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }
            return cellIndexToTimeStringMap;
        }
    }
}
