/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractXlsxSolutionImporter<Solution_> extends AbstractSolutionImporter<Solution_> {

    private static final String DEFAULT_INPUT_FILE_SUFFIX = "xlsx";

    @Override
    public String getInputFileSuffix() {
        return DEFAULT_INPUT_FILE_SUFFIX;
    }

    public abstract XlsxInputBuilder<Solution_> createXlsxInputBuilder();

    @Override
    public Solution_ readSolution(File inputFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XlsxInputBuilder<Solution_> xlsxInputBuilder = createXlsxInputBuilder();
            xlsxInputBuilder.setInputFile(inputFile);
            xlsxInputBuilder.setWorkbook(workbook);
            try {
                Solution_ solution = xlsxInputBuilder.readSolution();
                logger.info("Imported: {}", inputFile);
                return solution;
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new IllegalArgumentException("Exception in inputFile (" + inputFile + ")", e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the file (" + inputFile.getName() + ").", e);
        }
    }

    public static abstract class XlsxInputBuilder<Solution_> extends InputBuilder {

        protected File inputFile;
        protected XSSFWorkbook workbook;

        public void setInputFile(File inputFile) {
            this.inputFile = inputFile;
        }

        public void setWorkbook(XSSFWorkbook document) {
            this.workbook = document;
        }

        public abstract Solution_ readSolution() throws IOException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

        public String getInputId() {
            return FilenameUtils.getBaseName(inputFile.getPath());
        }

        protected XSSFSheet readSheet(int index, String name) {
            XSSFSheet sheet = workbook.getSheetAt(index);
            if (!sheet.getSheetName().equals(name)) {
                throw new IllegalArgumentException("The sheet (" + sheet.getSheetName() + ") at index (" + index
                        + ") is expected to have another name (" + name + ")");
            }
            return sheet;
        }

        protected void assertCellConstant(Cell cell, String constant) {
            if (!constant.equals(cell.getStringCellValue())) {
                throw new IllegalArgumentException("The cell (" + cell.getRow().getRowNum() + ","
                        + cell.getColumnIndex() + ") with value (" + cell.getStringCellValue()
                        + ") is expected to have the constant (" + constant + ")");
            }
        }

        protected long readLongCell(Cell cell) {
            double d = cell.getNumericCellValue();
            long l = (long) d;
            if (d - (double) l != 0.0) {
                throw new IllegalArgumentException("The keyCell (" + cell.getRow().getRowNum() + ","
                        + cell.getColumnIndex() + ") with value (" + d + ") is expected to be a long.");
            }
            return l;
        }

        protected double readDoubleCell(Cell cell) {
            return cell.getNumericCellValue();
        }

        protected String readStringCell(Cell cell) {
            return cell.getStringCellValue();
        }

        protected String readStringParameter(Row row, String key) {
            Cell keyCell = row.getCell(0);
            if (!key.equals(keyCell.getStringCellValue())) {
                throw new IllegalArgumentException("The keyCell (" + keyCell.getRow().getRowNum() + ","
                        + keyCell.getColumnIndex() + ") with value (" + keyCell.getStringCellValue()
                        + ") is expected to have the key (" + key + ")");
            }
            Cell valueCell = row.getCell(1);
            return valueCell.getStringCellValue();
        }

        protected double readDoubleParameter(Row row, String key) {
            Cell keyCell = row.getCell(0);
            if (!key.equals(keyCell.getStringCellValue())) {
                throw new IllegalArgumentException("The keyCell (" + keyCell.getRow().getRowNum() + ","
                        + keyCell.getColumnIndex() + ") with value (" + keyCell.getStringCellValue()
                        + ") is expected to have the key (" + key + ")");
            }
            Cell valueCell = row.getCell(1);
            return valueCell.getNumericCellValue();
        }
    }
}
