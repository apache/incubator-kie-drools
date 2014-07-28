/*
 * Copyright 2012 JBoss Inc
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
package org.drools.scorecards.parser.xls;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.scorecards.ScorecardError;
import org.drools.scorecards.parser.AbstractScorecardParser;
import org.drools.scorecards.parser.ScorecardParseException;
import org.drools.scorecards.pmml.ScorecardPMMLGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XLSScorecardParser extends AbstractScorecardParser {

    protected XLSEventDataCollector excelDataCollector;
    private Scorecard scorecard;
    private PMML pmmlDocument = null;
    List<ScorecardError> parseErrors = new ArrayList<ScorecardError>();
    private HSSFSheet currentWorksheet;

    @Override
    public List<ScorecardError>  parseFile(InputStream inStream, String worksheetName) throws ScorecardParseException {
        try {
            excelDataCollector = new XLSEventDataCollector();
            excelDataCollector.setParser(this);
            HSSFWorkbook workbook = new HSSFWorkbook(inStream);
            HSSFSheet worksheet = workbook.getSheet(worksheetName);
            if (worksheet != null) {
                currentWorksheet = worksheet;
                excelDataCollector.sheetStart(worksheetName);
                excelDataCollector.setMergedRegionsInSheet(getMergedCellRangeList(worksheet));
                processSheet(worksheet);
                excelDataCollector.sheetComplete();
                parseErrors = excelDataCollector.getParseErrors();
                scorecard = excelDataCollector.getScorecard();
            } else {
                throw new ScorecardParseException("No worksheet found with name '" + worksheetName + "'.");
            }
        } catch (IOException e) {
            throw new ScorecardParseException(e);
        }
        return parseErrors;
    }

    @Override
    public PMML getPMMLDocument() {
        if (pmmlDocument == null) {
            pmmlDocument = new ScorecardPMMLGenerator().generateDocument(scorecard);
        }
        return pmmlDocument;
    }

    private void processSheet(HSSFSheet worksheet) throws ScorecardParseException {
        for (Row row : worksheet) {
            int currentRowCtr = row.getRowNum();
            excelDataCollector.newRow(currentRowCtr);
            for (Cell cell : row) {
                int currentColCtr = cell.getColumnIndex();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        excelDataCollector.newCell(currentRowCtr, currentColCtr, cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            excelDataCollector.newCell(currentRowCtr, currentColCtr, cell.getDateCellValue());
                        } else {
                            excelDataCollector.newCell(currentRowCtr, currentColCtr, Double.valueOf(cell.getNumericCellValue()));
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        excelDataCollector.newCell(currentRowCtr, currentColCtr, Boolean.valueOf(cell.getBooleanCellValue()).toString());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        excelDataCollector.newCell(currentRowCtr, currentColCtr, "");
                        break;
                }
            }
        }
    }

    public String peekValueAt(int row, int col) {
        if (currentWorksheet != null){
            if ( row >= 0 && row < currentWorksheet.getLastRowNum() ) {
                HSSFRow hssfRow = currentWorksheet.getRow(row);
                if (hssfRow != null && col >= 0 && col < hssfRow.getLastCellNum()){
                    return hssfRow.getCell(col).getStringCellValue();
                }
            }
        }
        return null;
    }

    private List<MergedCellRange> getMergedCellRangeList(HSSFSheet worksheet) {
        List<MergedCellRange> mergedCellRanges = new ArrayList<MergedCellRange>();
        int mergedRegionsCount = worksheet.getNumMergedRegions();
        for (int ctr = 0; ctr < mergedRegionsCount; ctr++) {
            CellRangeAddress rangeAddress = worksheet.getMergedRegion(ctr);
            mergedCellRanges.add(new MergedCellRange(rangeAddress.getFirstRow(), rangeAddress.getFirstColumn(), rangeAddress.getLastRow(), rangeAddress.getLastColumn()));
        }
        return mergedCellRanges;
    }
}
