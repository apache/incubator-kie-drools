/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.swing.impl.TangoColorFactory;

public abstract class AbstractXlsxSolutionFileIO<Solution_> implements SolutionFileIO<Solution_> {

    protected static final Pattern VALID_TAG_PATTERN = Pattern.compile("(?U)^[\\w&\\-\\.\\/\\(\\)\\'][\\w&\\-\\.\\/\\(\\)\\' ]*[\\w&\\-\\.\\/\\(\\)\\']?$");
    protected static final Pattern VALID_NAME_PATTERN = AbstractXlsxSolutionFileIO.VALID_TAG_PATTERN;
    protected static final Pattern VALID_CODE_PATTERN = Pattern.compile("(?U)^[\\w\\-\\.\\/\\(\\)]+$");

    public static final DateTimeFormatter DAY_FORMATTER
            = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);
    public static final DateTimeFormatter MONTH_FORMATTER
            = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
    public static final DateTimeFormatter TIME_FORMATTER
            = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    protected static final XSSFColor VIEW_TAB_COLOR = new XSSFColor(TangoColorFactory.BUTTER_1);
    protected static final XSSFColor UNAVAILABLE_COLOR = new XSSFColor(TangoColorFactory.ALUMINIUM_5);
    protected static final XSSFColor PINNED_COLOR = new XSSFColor(TangoColorFactory.PLUM_1);
    protected static final XSSFColor HARD_PENALTY_COLOR = new XSSFColor(TangoColorFactory.SCARLET_1);
    protected static final XSSFColor MEDIUM_PENALTY_COLOR = new XSSFColor(TangoColorFactory.SCARLET_3);
    protected static final XSSFColor SOFT_PENALTY_COLOR = new XSSFColor(TangoColorFactory.ORANGE_1);

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    public static abstract class AbstractXlsxReader<Solution_> {

        protected final XSSFWorkbook workbook;

        protected Solution_ solution;

        protected XSSFSheet currentSheet;
        protected Iterator<Row> currentRowIterator;
        protected XSSFRow currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;

        public AbstractXlsxReader(XSSFWorkbook workbook) {
            this.workbook = workbook;
        }

        public abstract Solution_ read();

        protected void readIntConstraintLine(String name, Consumer<Integer> consumer, String constraintdescription) {
            nextRow();
            readHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (consumer != null) {
                if (weightCell.getCellTypeEnum() != CellType.NUMERIC) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be a number and the cell type must be numeric.");
                }
                double value = weightCell.getNumericCellValue();
                if (((double) ((int) value)) != value) {
                    throw new IllegalArgumentException(currentPosition() + ": The value (" + value
                            + ") for constraint (" + name + ") must be an integer.");
                }
                consumer.accept((int) value);
            } else {
                if (weightCell.getCellTypeEnum() == CellType.NUMERIC
                        || !weightCell.getStringCellValue().equals("n/a")) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be an n/a.");
                }
            }
            readHeaderCell(constraintdescription);
        }

        protected void readLongConstraintLine(String name, Consumer<Long> consumer, String constraintdescription) {
            nextRow();
            readHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (consumer != null) {
                if (weightCell.getCellTypeEnum() != CellType.NUMERIC) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be a number and the cell type must be numeric.");
                }
                double value = weightCell.getNumericCellValue();
                if (((double) ((long) value)) != value) {
                    throw new IllegalArgumentException(currentPosition() + ": The value (" + value
                            + ") for constraint (" + name + ") must be a (long) integer.");
                }
                consumer.accept((long) value);
            } else {
                if (weightCell.getCellTypeEnum() == CellType.NUMERIC
                        || !weightCell.getStringCellValue().equals("n/a")) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be an n/a.");
                }
            }
            readHeaderCell(constraintdescription);
        }

        protected String currentPosition() {
            return "Sheet (" + currentSheet.getSheetName() + ") cell ("
                    + (currentRowNumber + 1) + CellReference.convertNumToColString(currentColumnNumber) + ")";
        }

        protected boolean hasSheet(String sheetName) {
            return workbook.getSheet(sheetName) != null;
        }

        protected void nextSheet(String sheetName) {
            currentSheet = workbook.getSheet(sheetName);
            if (currentSheet == null) {
                throw new IllegalStateException("The workbook does not contain a sheet with name ("
                        + sheetName + ").");
            }
            currentRowIterator = currentSheet.rowIterator();
            if (currentRowIterator == null) {
                throw new IllegalStateException(currentPosition() + ": The sheet has no rows.");
            }
            currentRowNumber = -1;
        }

        protected boolean nextRow() {
            return nextRow(true);
        }

        protected boolean nextRow(boolean skipEmptyRows) {
            currentRowNumber++;
            currentColumnNumber = -1;
            if (!currentRowIterator.hasNext()) {
                currentRow = null;
                return false;
            }
            currentRow = (XSSFRow) currentRowIterator.next();
            while (skipEmptyRows && currentRowIsEmpty()) {
                if (!currentRowIterator.hasNext()) {
                    currentRow = null;
                    return false;
                }
                currentRow = (XSSFRow) currentRowIterator.next();
            }
            if (currentRow.getRowNum() != currentRowNumber) {
                if (currentRow.getRowNum() == currentRowNumber + 1) {
                    currentRowNumber++;
                } else {
                    throw new IllegalStateException(currentPosition() + ": The next row (" + currentRow.getRowNum()
                            + ") has a gap of more than 1 empty line with the previous.");
                }
            }
            return true;
        }

        protected boolean currentRowIsEmpty() {
            if (currentRow.getPhysicalNumberOfCells() == 0) {
                return true;
            }
            for (Cell cell : currentRow) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    if (!cell.getStringCellValue().isEmpty()) {
                        return false;
                    }
                } else if (cell.getCellTypeEnum() != CellType.BLANK) {
                    return false;
                }
            }
            return true;
        }

        protected void readHeaderCell(String value) {
            XSSFCell cell = currentRow == null ? null : nextStringCell();
            if (cell == null || !cell.getStringCellValue().equals(value)) {
                throw new IllegalStateException(currentPosition() + ": The cell does not contain the expected value ("
                        + value + ").");
            }
        }

        protected void readHeaderCell(double value) {
            XSSFCell cell = currentRow == null ? null : nextNumericCell();
            if (cell == null || cell.getNumericCellValue() != value) {
                throw new IllegalStateException(currentPosition() + ": The cell does not contain the expected value ("
                        + value + ").");
            }
        }

        protected XSSFCell nextStringCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getNumericCellValue() + ") has a numeric type but should be a string.");
            }
            return cell;
        }

        protected XSSFCell nextNumericCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.STRING) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getStringCellValue() + ") has a string type but should be numeric.");
            }
            return cell;
        }

        protected XSSFCell nextBooleanCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.STRING) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getStringCellValue() + ") has a string type but should be boolean.");
            }
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getNumericCellValue() + ") has a numeric type but should be a boolean.");
            }
            return cell;
        }

        protected XSSFCell nextCell() {
            currentColumnNumber++;
            XSSFCell cell = currentRow.getCell(currentColumnNumber);
            // TODO HACK to workaround the fact that LibreOffice and Excel automatically remove empty trailing cells
            if (cell == null) {
                // Return dummy cell
                return currentRow.createCell(currentColumnNumber);
            }
            return cell;
        }

        protected XSSFColor extractColor(XSSFCell cell, XSSFColor... acceptableColors) {
            XSSFCellStyle cellStyle = cell.getCellStyle();
            FillPatternType fillPattern = cellStyle.getFillPatternEnum();
            if (fillPattern == null || fillPattern == FillPatternType.NO_FILL) {
                return null;
            }
            if (fillPattern != FillPatternType.SOLID_FOREGROUND) {
                throw new IllegalStateException(currentPosition() + ": The fill pattern (" + fillPattern
                        + ") should be either " + FillPatternType.NO_FILL
                        + " or " + FillPatternType.SOLID_FOREGROUND + ".");
            }
            XSSFColor color = cellStyle.getFillForegroundColorColor();
            for (XSSFColor acceptableColor : acceptableColors) {
                if (acceptableColor.equals(color)) {
                    return acceptableColor;
                }
            }
            throw new IllegalStateException(currentPosition() + ": The fill color (" + color
                    + ") is not one of the acceptableColors (" + Arrays.toString(acceptableColors) + ").");
        }
    }

    public static abstract class AbstractXlsxWriter<Solution_> {

        protected final Solution_ solution;
        protected final List<ConstraintMatchTotal> constraintMatchTotalList;
        protected final Map<Object, Indictment> indictmentMap;

        protected XSSFWorkbook workbook;
        protected CreationHelper creationHelper;

        protected XSSFCellStyle headerStyle;
        protected XSSFCellStyle defaultStyle;
        protected XSSFCellStyle unavailableStyle;
        protected XSSFCellStyle pinnedStyle;
        protected XSSFCellStyle hardPenaltyStyle;
        protected XSSFCellStyle mediumPenaltyStyle;
        protected XSSFCellStyle softPenaltyStyle;
        protected XSSFCellStyle wrappedStyle;

        protected XSSFSheet currentSheet;
        protected Drawing currentDrawing;
        protected XSSFRow currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;
        protected int headerCellCount;

        public AbstractXlsxWriter(Solution_ solution, String solverConfigResource) {
            this.solution = solution;
            ScoreDirectorFactory<Solution_> scoreDirectorFactory
                    = SolverFactory.<Solution_>createFromXmlResource(solverConfigResource)
                    .buildSolver().getScoreDirectorFactory();
            try (ScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
                scoreDirector.setWorkingSolution(solution);
                scoreDirector.calculateScore();
                constraintMatchTotalList = new ArrayList<>(scoreDirector.getConstraintMatchTotals());
                constraintMatchTotalList.sort(Comparator.comparing(ConstraintMatchTotal::getScore));
                indictmentMap = scoreDirector.getIndictmentMap();
            }
        }

        public abstract Workbook write();

        public void writeSetup() {
            workbook = new XSSFWorkbook();
            creationHelper = workbook.getCreationHelper();
            createStyles();
        }

        protected void createStyles() {
            headerStyle = createStyle(null);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            defaultStyle = createStyle(null);
            unavailableStyle = createStyle(UNAVAILABLE_COLOR);
            pinnedStyle = createStyle(PINNED_COLOR);
            hardPenaltyStyle = createStyle(HARD_PENALTY_COLOR);
            mediumPenaltyStyle = createStyle(MEDIUM_PENALTY_COLOR);
            softPenaltyStyle = createStyle(SOFT_PENALTY_COLOR);
            wrappedStyle = createStyle(null);
        }

        protected XSSFCellStyle createStyle(XSSFColor color) {
            XSSFCellStyle style = workbook.createCellStyle();
            if (color != null) {
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillForegroundColor(color);
            }
            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        protected void writeIntConstraintLine(String name, Supplier<Integer> supplier, String constraintDescription) {
            nextRow();
            nextHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (supplier != null) {
                weightCell.setCellValue(supplier.get());
            } else {
                weightCell.setCellValue("n/a");
            }
            nextHeaderCell(constraintDescription);
        }

        protected void writeLongConstraintLine(String name, Supplier<Long> supplier, String constraintDescription) {
            nextRow();
            nextHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (supplier != null) {
                weightCell.setCellValue(supplier.get());
            } else {
                weightCell.setCellValue("n/a");
            }
            nextHeaderCell(constraintDescription);
        }

        protected void nextSheet(String sheetName, int colSplit, int rowSplit, boolean view) {
            currentSheet = workbook.createSheet(sheetName);
            currentDrawing = currentSheet.createDrawingPatriarch();
            currentSheet.createFreezePane(colSplit, rowSplit);
            currentRowNumber = -1;
            headerCellCount = 0;
            if (view) {
                currentSheet.setTabColor(VIEW_TAB_COLOR);
            }
        }

        protected void nextRow() {
            currentRowNumber++;
            currentRow = currentSheet.createRow(currentRowNumber);
            currentColumnNumber = -1;
        }

        protected void nextHeaderCell(String value) {
            nextCell(headerStyle).setCellValue(value);
            headerCellCount++;
        }

        protected void nextHeaderCell(double value) {
            nextCell(headerStyle).setCellValue(value);
            headerCellCount++;
        }

        protected XSSFCell nextCell() {
            return nextCell(defaultStyle);
        }

        protected XSSFCell nextCell(XSSFCellStyle cellStyle) {
            currentColumnNumber++;
            XSSFCell cell = currentRow.createCell(currentColumnNumber);
            cell.setCellStyle(cellStyle);
            return cell;
        }

        protected void nextHeaderCellVertically(String value) {
            nextCellVertically(headerStyle).setCellValue(value);
            headerCellCount++;
        }

        protected XSSFCell nextCellVertically() {
            return nextCellVertically(defaultStyle);
        }

        protected XSSFCell nextCellVertically(XSSFCellStyle cellStyle) {
            currentRowNumber++;
            currentRow = currentSheet.getRow(currentRowNumber);
            XSSFCell cell = currentRow.createCell(currentColumnNumber);
            cell.setCellStyle(cellStyle);
            return cell;
        }

        protected void autoSizeColumnsWithHeader() {
            for (int i = 0; i < headerCellCount; i++) {
                currentSheet.autoSizeColumn(i);
            }
        }

        protected void setSizeColumnsWithHeader(int width) {
            for (int i = 0; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, width);
            }
        }
    }

}
