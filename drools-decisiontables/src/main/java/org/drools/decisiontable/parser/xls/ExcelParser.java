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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DecisionTableParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Parse an excel spreadsheet, pushing cell info into the SheetListener interface.
 */
public class ExcelParser
        implements
        DecisionTableParser {

    private static final Logger log = LoggerFactory.getLogger( ExcelParser.class );

    public static final String DEFAULT_RULESHEET_NAME = "Decision Tables";
    private Map<String, List<DataListener>> _listeners = new HashMap<String, List<DataListener>>();
    private boolean _useFirstSheet;

    /**
     * Define a map of sheet name to listener handlers.
     * @param sheetListeners map of String to SheetListener
     */
    public ExcelParser( final Map<String, List<DataListener>> sheetListeners ) {
        this._listeners = sheetListeners;
    }

    public ExcelParser( final List<DataListener> sheetListeners ) {
        this._listeners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                             sheetListeners );
        this._useFirstSheet = true;
    }

    public ExcelParser( final DataListener listener ) {
        List<DataListener> listeners = new ArrayList<DataListener>();
        listeners.add( listener );
        this._listeners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                             listeners );
        this._useFirstSheet = true;
    }

    public void parseFile( InputStream inStream ) {
        try {
            Workbook workbook = WorkbookFactory.create( inStream );

            if ( _useFirstSheet ) {
                Sheet sheet = workbook.getSheetAt( 0 );
                processSheet( sheet, _listeners.get( DEFAULT_RULESHEET_NAME ) );
            } else {
                for ( String sheetName : _listeners.keySet() ) {
                    Sheet sheet = workbook.getSheet( sheetName );
                    if ( sheet == null ) {
                        throw new IllegalStateException( "Could not find the sheetName (" + sheetName
                                                                 + ") in the workbook sheetNames." );
                    }
                    processSheet( sheet,
                                  _listeners.get( sheetName ) );

                }
            }
        } catch ( InvalidFormatException e ) {
            throw new DecisionTableParseException( "An error occurred opening the workbook. It is possible that the encoding of the document did not match the encoding of the reader.",
                                                   e );

        } catch ( IOException e ) {
            throw new DecisionTableParseException( "Failed to open Excel stream, " + "please check that the content is xls97 format.",
                                                   e );
        }

    }

    private CellRangeAddress[] getMergedCells( Sheet sheet ) {
        CellRangeAddress[] ranges = new CellRangeAddress[ sheet.getNumMergedRegions() ];
        for ( int i = 0; i < ranges.length; i++ ) {
            ranges[ i ] = sheet.getMergedRegion( i );
        }
        return ranges;
    }

    private void processSheet( Sheet sheet,
                               List<? extends DataListener> listeners ) {
        int maxRows = sheet.getLastRowNum();

        CellRangeAddress[] mergedRanges = getMergedCells( sheet );
        DataFormatter formatter = new DataFormatter( Locale.ENGLISH );
        FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        for ( int i = 0; i <= maxRows; i++ ) {
            Row row = sheet.getRow( i );
            int lastCellNum = row != null ? row.getLastCellNum() : 0;
            newRow( listeners, i, lastCellNum );

            for ( int cellNum = 0; cellNum < lastCellNum; cellNum++ ) {
                Cell cell = row.getCell( cellNum );
                if ( cell == null ) {
                    continue;
                }
                double num = 0;

                CellRangeAddress merged = getRangeIfMerged( cell,
                                                            mergedRanges );

                if ( merged != null ) {
                    Cell topLeft = sheet.getRow( merged.getFirstRow() ).getCell( merged.getFirstColumn() );
                    newCell( listeners,
                             i,
                             cellNum,
                             formatter.formatCellValue( topLeft ),
                             topLeft.getColumnIndex() );

                } else {
                    switch ( cell.getCellType() ) {
                        case Cell.CELL_TYPE_FORMULA:
                            String cellValue = null;
                            try {
                                CellValue cv = formulaEvaluator.evaluate( cell );
                                cellValue = getCellValue( cv );
                                newCell( listeners,
                                         i,
                                         cellNum,
                                         cellValue,
                                         DataListener.NON_MERGED );
                            } catch ( RuntimeException e ) {
                                // This is thrown if an external link cannot be resolved, so try the cached value
                                log.warn( "Cannot resolve externally linked value: " + formatter.formatCellValue( cell ) );
                                String cachedValue = tryToReadCachedValue( cell );
                                newCell( listeners,
                                         i,
                                         cellNum,
                                         cachedValue,
                                         DataListener.NON_MERGED );
                            }
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            num = cell.getNumericCellValue();
                        default:
                            if ( num - Math.round( num ) != 0 ) {
                                newCell( listeners,
                                         i,
                                         cellNum,
                                         String.valueOf( num ),
                                         DataListener.NON_MERGED );
                            } else {
                                newCell( listeners,
                                         i,
                                         cellNum,
                                         formatter.formatCellValue( cell ),
                                         DataListener.NON_MERGED );
                            }
                    }
                }
            }
        }
        finishSheet( listeners );
    }

    private String tryToReadCachedValue( Cell cell ) {
        DataFormatter formatter = new DataFormatter( Locale.ENGLISH );
        String cachedValue;
        switch ( cell.getCachedFormulaResultType() ) {
            case Cell.CELL_TYPE_NUMERIC:
                double num = cell.getNumericCellValue();
                if ( num - Math.round( num ) != 0 ) {
                    cachedValue = String.valueOf( num );
                } else {
                    cachedValue = formatter.formatCellValue( cell );
                }
                break;

            case Cell.CELL_TYPE_STRING:
                cachedValue = cell.getStringCellValue();
                break;

            case Cell.CELL_TYPE_BOOLEAN:
                cachedValue = String.valueOf( cell.getBooleanCellValue() );
                break;

            case Cell.CELL_TYPE_ERROR:
                cachedValue = String.valueOf( cell.getErrorCellValue() );
                break;

            default:
                throw new DecisionTableParseException( format( "Can't read cached value for cell[row=%d, col=%d, value=%s]!",
                                                               cell.getRowIndex(), cell.getColumnIndex(), cell ) );
        }
        return cachedValue;
    }

    private String getCellValue( final CellValue cv ) {
        switch ( cv.getCellType() ) {
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString( cv.getBooleanValue() );
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf( cv.getNumberValue() );
        }
        return cv.getStringValue();
    }

    CellRangeAddress getRangeIfMerged( Cell cell,
                                       CellRangeAddress[] mergedRanges ) {
        for ( int i = 0; i < mergedRanges.length; i++ ) {
            CellRangeAddress r = mergedRanges[ i ];
            if ( r.isInRange( cell.getRowIndex(), cell.getColumnIndex() ) ) {
                return r;
            }
        }
        return null;
    }

    private void finishSheet( List<? extends DataListener> listeners ) {
        for ( DataListener listener : listeners ) {
            listener.finishSheet();
        }
    }

    private void newRow( List<? extends DataListener> listeners,
                         int row,
                         int cols ) {
        for ( DataListener listener : listeners ) {
            listener.newRow( row,
                             cols );
        }
    }

    public void newCell( List<? extends DataListener> listeners,
                         int row,
                         int column,
                         String value,
                         int mergedColStart ) {
        for ( DataListener listener : listeners ) {
            listener.newCell( row,
                              column,
                              value,
                              mergedColStart );
        }
    }

}