/*
 * Copyright 2005 JBoss Inc
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

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.format.CellFormat;

import org.drools.template.parser.DataListener;
import org.junit.Test;

/**
 *
 * Some unit tests for the corners of ExcelParser that are not explicitly
 * covered by integration tests.
 */
public class ExcelParserTest {
    /**
     * This should test to see if a cell is in a certain range or not. 
     * If it is in a merged range, then it should return the top left cell.
     * @throws Exception
     */
    @Test
    public void testCellMerge() throws Exception {
        ExcelParser parser = new ExcelParser((Map<String, List<DataListener>>) null);

        Range[] ranges = new Range[1];

        MockRange r1 = new MockRange();
        ranges[0] = r1;
        r1.topLeft = new MockCell();
        r1.topLeft.row = 2;
        r1.topLeft.column = 2;
        r1.topLeft.contents = "first";



        r1.bottomRight = new MockCell();
        r1.bottomRight.column = 5;
        r1.bottomRight.row = 7;
        r1.bottomRight.contents = "last";


        MockCell cell = new MockCell();
        cell.contents = "test";
        cell.row = 1;
        cell.column = 1;

        assertNull(parser.getRangeIfMerged( cell, ranges));

        cell = new MockCell();
        cell.contents = "wrong";
        cell.row = 2;
        cell.column = 5;

        assertEquals("first", parser.getRangeIfMerged( cell, ranges).getTopLeft().getContents());

    }
    
    static class MockCell<CellFeatures> implements Cell {

        int column;
        int row;
        String contents;


        public CellFormat getCellFormat() {
            return null;
        }

        public int getColumn() {
            return column;
        }

        public String getContents() {
            return contents;
        }

        public int getRow() {
            return row;
        }

        public CellType getType() {
            return null;
        }

        public boolean isHidden() {
            return false;
        }

        public jxl.CellFeatures getCellFeatures() {
            // TODO Auto-generated method stub
            return null;
        }

    }
    
    static class MockRange implements Range {

        MockCell bottomRight;
        MockCell topLeft;

        public Cell getBottomRight() {
            return bottomRight;
        }

        public int getFirstSheetIndex() {
            return 0;
        }

        public int getLastSheetIndex() {
            return 0;
        }

        public Cell getTopLeft() {
            return topLeft;
        }

    }

}
