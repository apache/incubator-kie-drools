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

package org.drools.decisiontable.parser.csv;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.template.parser.DataListener;

public class CsvParserTest {

    @Test
    public void testCsv() {
        final MockSheetListener listener = new MockSheetListener();
        final CsvLineParser lineParser = new CsvLineParser();
        final CsvParser parser = new CsvParser( listener,
                                                lineParser );

        parser.parseFile( getClass().getResourceAsStream( "/data/TestCsv.csv" ) );
        assertEquals( "A",
                      listener.getCell( 0,
                                        0 ) );
        assertEquals( "B",
                      listener.getCell( 0,
                                        1 ) );
        assertEquals( "",
                      listener.getCell( 2,
                                        0 ) );
        assertEquals( "C",
                      listener.getCell( 1,
                                        0 ) );
        assertEquals( "D",
                      listener.getCell( 1,
                                        1 ) );
        assertEquals( "E",
                      listener.getCell( 1,
                                        3 ) );

    }

    /**
     * Test the handling of merged cells.
     */
    @Test
    public void testCellMergeHandling() {
        CsvParser parser = new CsvParser( (DataListener) null,
                                          null );
        assertEquals( DataListener.NON_MERGED,
                      parser.calcStartMerge( DataListener.NON_MERGED,
                                             1,
                                             "foo" ) );
        assertEquals( 42,
                      parser.calcStartMerge( DataListener.NON_MERGED,
                                             42,
                                             "..." ) );

        assertEquals( 42,
                      parser.calcStartMerge( 42,
                                             43,
                                             "..." ) );

        assertEquals( DataListener.NON_MERGED,
                      parser.calcStartMerge( 42,
                                             44,
                                             "VanHalen" ) );

        assertEquals( "VanHalen",
                      parser.calcCellText( DataListener.NON_MERGED,
                                           "VanHalen" ) );
        assertEquals( "VanHalen",
                      parser.calcCellText( 42,
                                           "VanHalen..." ) );
        assertEquals( "",
                      parser.calcCellText( 42,
                                           "..." ) );

    }

    static class MockSheetListener
        implements
        DataListener {

        Map<String, String> data = new HashMap<String, String>();

        public String getCell(final int row,
                              final int col) {
            return this.data.get( cellKey( row,
                                           col ) );
        }

        public void startSheet(final String name) {
        }

        public void finishSheet() {
        }

        public void newRow(final int rowNumber,
                           final int columns) {

        }

        public void newCell(final int row,
                            final int column,
                            final String value,
                            final int mergeCellStart) {

            this.data.put( cellKey( row,
                                    column ),
                           value );
        }

        String cellKey(final int row,
                       final int column) {
            return "R" + row + "C" + column;
        }
    }
}
