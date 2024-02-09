/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.decisiontable.parser.xls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.drools.util.Config.getConfig;

/**
 * Parse an excel spreadsheet, pushing cell info into the SheetListener interface.
 */
public class ExcelParser
        implements
        DecisionTableParser {

    private static final Logger log = LoggerFactory.getLogger( ExcelParser.class );

    private static void initMinInflateRatio() {
        String minInflateRatio = getConfig( "drools.excelParser.minInflateRatio" );
        if (minInflateRatio != null) {
            try {
                ZipSecureFile.setMinInflateRatio( Double.parseDouble( minInflateRatio ) );
            } catch (NumberFormatException nfe) {
                log.error( "Invalid value '" + minInflateRatio + "' for property drools.excelParser.minInflateRatio. It has to be a double" );
            }
        } else {
            ZipSecureFile.setMinInflateRatio( 0.01 ); // default value
        }
    }

    public static final String DEFAULT_RULESHEET_NAME = "Decision Tables";
    private Map<String, List<DataListener>> _listeners = new HashMap<>();
    private boolean _useFirstSheet;

    /**
     * Define a map of sheet name to listener handlers.
     * @param sheetListeners map of String to SheetListener
     */
    public ExcelParser( final Map<String, List<DataListener>> sheetListeners ) {
        this._listeners = sheetListeners;
        initMinInflateRatio();
    }

    public ExcelParser( final List<DataListener> sheetListeners ) {
        this._listeners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                             sheetListeners );
        this._useFirstSheet = true;
        initMinInflateRatio();
    }

    public ExcelParser( final DataListener listener ) {
        List<DataListener> listeners = new ArrayList<>();
        listeners.add( listener );
        this._listeners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                             listeners );
        this._useFirstSheet = true;
        initMinInflateRatio();
    }

    public void parseFile( InputStream inStream ) {
        try {
            parseWorkbook( WorkbookFactory.create( inStream ) );
        } catch ( IOException e ) {
            throw new DecisionTableParseException( "Failed to open Excel stream, " + "please check that the content is xls97 format.",
                                                   e );
        }
    }

    public void parseFile( File file ) {
        try {
            parseWorkbook( WorkbookFactory.create(file, null, true));
        } catch ( IOException e ) {
            throw new DecisionTableParseException( "Failed to open Excel stream, " + "please check that the content is xls97 format.",
                                                   e );
        }
    }

    public void parseWorkbook( Workbook workbook ) {
        try {
            try {
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
                        processSheet                              ( sheet,
                                                                    _listeners.get( sheetName ) );

                    }
                }
            } finally {
                workbook.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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

                int mergedColStart = DataListener.NON_MERGED;
                if ( merged != null ) {
                    cell = sheet.getRow(merged.getFirstRow()).getCell(merged.getFirstColumn());
                    mergedColStart = cell.getColumnIndex();
                }

                switch ( cell.getCellType() ) {
                    case BOOLEAN:
                        newCell(listeners,
                                i,
                                cellNum,
                                cell.getBooleanCellValue() ? "true" : "false",
                                mergedColStart);
                        break;
                    case FORMULA:
                        try {
                            boolean ignoreNumericFormat = doesIgnoreNumericFormat(listeners) && !isGeneralFormat(cell);
                            newCell(listeners,
                                    i,
                                    cellNum,
                                    getFormulaValue( formatter, formulaEvaluator, cell, ignoreNumericFormat ),
                                    mergedColStart);
                        } catch (RuntimeException e) {
                            // This is thrown if an external link cannot be resolved, so try the cached value
                            log.warn("Cannot resolve externally linked value: " + formatter.formatCellValue(cell));
                            String cachedValue = tryToReadCachedValue(cell);
                            newCell(listeners,
                                    i,
                                    cellNum,
                                    cachedValue,
                                    mergedColStart);
                        }
                        break;
                    case NUMERIC:
                        if ( isNumericDisabled(listeners) ) {
                            // don't get a double value. rely on DataFormatter
                        } else if ( DateUtil.isCellDateFormatted(cell) ) {
                            newCell(listeners,
                                    i,
                                    cellNum,
                                    "\"" + DateUtils.format(cell.getDateCellValue()) + "\"",
                                    mergedColStart);
                            break;
                        } else {
                            num = cell.getNumericCellValue();
                            if (doesIgnoreNumericFormat(listeners) && !isGeneralFormat(cell)) {
                                // If it's not GENERAL format (e.g. Percent, Currency), we don't rely on formatter
                                newCell(listeners,
                                        i,
                                        cellNum,
                                        String.valueOf(num),
                                        mergedColStart);
                                break;
                            }
                        }
                    default:
                        if (num - Math.round(num) != 0) {
                            newCell(listeners,
                                    i,
                                    cellNum,
                                    String.valueOf(num),
                                    mergedColStart);
                        } else {
                            // e.g. format '42.0' to '42' for int
                            newCell(listeners,
                                    i,
                                    cellNum,
                                    formatter.formatCellValue(cell),
                                    mergedColStart);
                        }
                }
            }
        }
        finishSheet( listeners );
    }

    private boolean isGeneralFormat(Cell cell) {
        CellStyle style = cell.getCellStyle();
        ExcelNumberFormat nf = ExcelNumberFormat.from(style);
        return nf.getFormat().equalsIgnoreCase("General");
    }

    private String getFormulaValue(DataFormatter formatter, FormulaEvaluator formulaEvaluator, Cell cell, boolean ignoreNumericFormat) {
        CellType cellType = formulaEvaluator.evaluate(cell).getCellType();
        if (cellType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue() ? "true" : "false";
        }
        if (cellType == CellType.NUMERIC && ignoreNumericFormat) {
            return String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
        }
        return formatter.formatCellValue(cell, formulaEvaluator);
    }

    private String tryToReadCachedValue( Cell cell ) {
        DataFormatter formatter = new DataFormatter( Locale.ENGLISH );
        String cachedValue;
        switch ( cell.getCachedFormulaResultType() ) {
            case NUMERIC:
                double num = cell.getNumericCellValue();
                if ( num - Math.round( num ) != 0 ) {
                    cachedValue = String.valueOf( num );
                } else {
                    cachedValue = formatter.formatCellValue( cell );
                }
                break;

            case STRING:
                cachedValue = cell.getStringCellValue();
                break;

            case BOOLEAN:
                cachedValue = String.valueOf( cell.getBooleanCellValue() );
                break;

            case ERROR:
                cachedValue = String.valueOf( cell.getErrorCellValue() );
                break;

            default:
                throw new DecisionTableParseException( format( "Can't read cached value for cell[row=%d, col=%d, value=%s]!",
                                                               cell.getRowIndex(), cell.getColumnIndex(), cell ) );
        }
        return cachedValue;
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

    private boolean isNumericDisabled( List<? extends DataListener> listeners ) {
        for ( DataListener listener : listeners ) {
            if (listener instanceof DefaultRuleSheetListener) {
                return ((DefaultRuleSheetListener)listener).isNumericDisabled();
            }
        }
        return false;
    }

    private boolean doesIgnoreNumericFormat( List<? extends DataListener> listeners ) {
        for ( DataListener listener : listeners ) {
            if (listener instanceof DefaultRuleSheetListener) {
                return ((DefaultRuleSheetListener)listener).doesIgnoreNumericFormat();
            }
        }
        return false;
    }
}
