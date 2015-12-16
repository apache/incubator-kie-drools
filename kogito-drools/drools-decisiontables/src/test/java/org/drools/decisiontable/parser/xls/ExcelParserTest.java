/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.decisiontable.parser.xls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.template.parser.DataListener;
import org.junit.Test;

/**
 *
 * Some unit tests for the corners of ExcelParser that are not explicitly
 * covered by integration tests.
 */
public class ExcelParserTest {

    private static final String LAST_CELL_VALUE = "last";
    private static final String FIRST_CELL_CONTENT = "first";

    /**
     * This should test to see if a cell is in a certain range or not. 
     * If it is in a merged range, then it should return the top left cell.
     * @throws Exception
     */
    @Test
    public void testCellMerge() throws Exception {
        ExcelParser parser = new ExcelParser((Map<String, List<DataListener>>) null);

        CellRangeAddress[] ranges = new CellRangeAddress[1];

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Cell cell = sheet.createRow(2).createCell(2);
        ranges[0] = new CellRangeAddress(2, 7, 2, 5);
        cell.setCellValue(FIRST_CELL_CONTENT);

        cell = sheet.createRow(7).createCell(5);
        cell.setCellValue(LAST_CELL_VALUE);

        cell = sheet.createRow(1).createCell(1);
        assertNull(parser.getRangeIfMerged(cell, ranges));

        cell = sheet.getRow(2).createCell(5);
        cell.setCellValue("wrong");

        CellRangeAddress rangeIfMerged = parser.getRangeIfMerged(cell, ranges);
        assertEquals(FIRST_CELL_CONTENT, sheet.getRow(rangeIfMerged.getFirstRow()).getCell(rangeIfMerged.getFirstColumn()).getStringCellValue());
    }

}
