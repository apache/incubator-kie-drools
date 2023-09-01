package org.drools.decisiontable.parser.xls;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.template.parser.DataListener;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Some unit tests for the corners of ExcelParser that are not explicitly
 * covered by integration tests.
 */
public class ExcelParserTest {

    private static final String LAST_CELL_VALUE = "last";
    private static final String FIRST_CELL_CONTENT = "first";
    private Workbook workbook;
    private Sheet sheet;

    
    @After
    public void tearDown() throws IOException {
        if(workbook != null) {
            workbook.close();
        }
    }
    
    /**
     * This should test to see if a cell is in a certain range or not. 
     * If it is in a merged range, then it should return the top left cell.
     * @throws Exception
     */
    @Test
    public void testCellMerge() throws Exception {
        ExcelParser parser = new ExcelParser((Map<String, List<DataListener>>) null);
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
        
        createCell(2, 2, FIRST_CELL_CONTENT);
        createCell(7, 5, LAST_CELL_VALUE);

        Cell cell = sheet.createRow(1).createCell(1);
        CellRangeAddress[] ranges = {new CellRangeAddress(2, 7, 2, 5)};
        assertThat(parser.getRangeIfMerged(cell, ranges)).isNull();
        
        updateCell(2,  5, "wrong");
        Cell wrongCell = getCell(2, 5);
        CellRangeAddress rangeIfMerged = parser.getRangeIfMerged(wrongCell, ranges);
        
        assertThat(sheet.getRow(rangeIfMerged.getFirstRow()).getCell(rangeIfMerged.getFirstColumn()).getStringCellValue()).isEqualTo(FIRST_CELL_CONTENT);
    }
    
    private void createCell(int row, int column, String value) {
        Cell cell = sheet.createRow(row).createCell(column);
        cell.setCellValue(value);
    }
    
    private void updateCell(int row, int column, String value) {
        Cell cell = sheet.getRow(row).createCell(column);
        cell.setCellValue(value);
    }

    private Cell getCell(int row, int column) {
        return sheet.getRow(row).createCell(column);
    }

}
