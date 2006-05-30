package org.drools.decisiontable.parser.xls;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.drools.decisiontable.parser.DecisionTableParseException;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.SheetListener;

/**
 * @author <a href="mailto:shaun.addison@gmail.com"> Shaun Addison </a><a
 *         href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 *         Forumula handling by Pascal Chanteux
 * 
 * Parse an excel spreadsheet using the event model of POI. TODO: can not
 * resolve date format issues as yet.
 * 
 */
public class ExcelParser
    implements
    DecisionTableParser,
    HSSFListener {

    public static final String  DEFAULT_RULESHEET_NAME = "Decision Tables";

    private static final String WORKBOOK               = "Workbook";

    public static final String  DATE_TARGET_FORMAT     = "dd-MMM-yyyy";

    private Map                 _listners              = new HashMap();

    private SheetListener       _currentSheetListener  = new NullSheetListener();

    private final List                _sheetNames            = new ArrayList();

    private int                 _currentSheetNumber    = 0;

    private SSTRecord           _sstRec                = null;

    private boolean             _useFirstSheet         = false;

    private HSSFWorkbook        _workbook              = null;

    /**
     * Define a map of sheet name to listner handlers.
     * 
     * @param sheetListners
     *            map of String to SheetListener
     */
    public ExcelParser(final Map sheetListners) {
        this._listners = sheetListners;
    }

    public ExcelParser(final SheetListener listener) {
        this._listners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                       listener );
        this._useFirstSheet = true;
    }

    /**
     * Initialise a new sheet and complete the previous.
     * 
     * @param record
     *            the new record
     */
    private void newSheet(final String sheetName) {
        this._currentSheetListener.finishSheet();
        this._currentSheetListener = new NullSheetListener();

        if ( this._useFirstSheet && this._currentSheetNumber == 1 ) {
            this._currentSheetListener = getFirstSheetListener();
        } else {
            if ( this._listners.containsKey( sheetName ) ) {
                this._currentSheetListener = (SheetListener) this._listners.get( sheetName );
            }
        }

        this._currentSheetListener.startSheet( sheetName );
    }

    private SheetListener getFirstSheetListener() {
        final SheetListener listener = (SheetListener) this._listners.values().iterator().next();
        if ( listener == null ) {
            throw new IllegalStateException( "No default first sheet listener found !" );
        }
        return listener;
    }

    /**
     * Implement the HSSFListener method
     * 
     * This receives the events for the Excel file format scan. A bunch of
     * document data OLE style and the sheet content.
     */
    public void processRecord(final Record record) {

        switch ( record.getSid() ) {
            case BoundSheetRecord.sid :
                final BoundSheetRecord bsr = (BoundSheetRecord) record;
                this._sheetNames.add( bsr.getSheetname() );
                break;
            case BOFRecord.sid :
                final BOFRecord bof = (BOFRecord) record;
                if ( bof.getType() == BOFRecord.TYPE_WORKSHEET ) {
                    newSheet( (String) this._sheetNames.get( this._currentSheetNumber++ ) );
                }
                break;
            case RowRecord.sid :
                final RowRecord rowRec = (RowRecord) record;
                this._currentSheetListener.newRow( rowRec.getRowNumber(),
                                              rowRec.getLastCol() - rowRec.getFirstCol() + 1 );
                break;
            case SSTRecord.sid :
                this._sstRec = (SSTRecord) record;
                break;
            case LabelSSTRecord.sid :
                handleText( record );
                break;

            case NumberRecord.sid :
                handleNumber( record );
                break;
            case BoolErrRecord.sid :
                handleBool( record );
                break;

            case FormulaRecord.sid :
                handleFormula( record );
                break;

            default :
                break;
        }

    }

    private void handleFormula(final Record record) {

        final FormulaRecord formulaRec = (FormulaRecord) record;

        final Ptg ptg = formulaRec.peekExpressionToken();

        if ( ptg instanceof Ref3DPtg ) {

            final Ref3DPtg ptg3D = (Ref3DPtg) ptg;

            final HSSFSheet sheet = this._workbook.getSheetAt( ptg3D.getExternSheetIndex() + 1 );
            final HSSFRow row = sheet.getRow( ptg3D.getRow() );
            final HSSFCell cell = row.getCell( ptg3D.getColumn() );

            this._currentSheetListener.newCell( formulaRec.getRow(),
                                           formulaRec.getColumn(),
                                           "" + cell.getStringCellValue() );

        }
    }

    private void handleBool(final Record record) {
        final BoolErrRecord boolRec = (BoolErrRecord) record;
        this._currentSheetListener.newCell( boolRec.getRow(),
                                       boolRec.getColumn(),
                                       "" + boolRec.getBooleanValue() );
    }

    private void handleText(final Record record) {
        final LabelSSTRecord labelRec = (LabelSSTRecord) record;
        final String value = this._sstRec.getString( labelRec.getSSTIndex() );
        this._currentSheetListener.newCell( labelRec.getRow(),
                                       labelRec.getColumn(),
                                       value );
    }

    private void handleNumber(final Record record) {
        // MN have to do with stupid numbers with ".0" as well as
        // freaking excel freaking dates freakin.
        final NumberRecord numRec = (NumberRecord) record;

        String stringVal = null;

        /*
         * if (numRec.getXFIndex() == DATE_FORMATTED_CELL) { Date date =
         * HSSFDateUtil.getJavaDate(val); SimpleDateFormat format = new
         * SimpleDateFormat(DATE_TARGET_FORMAT); stringVal =
         * format.format(date); } else {
         */
        stringVal = "" + numRec.getValue();
        stringVal = removeTrailingZero( stringVal );
        /* } */
        this._currentSheetListener.newCell( numRec.getRow(),
                                       numRec.getColumn(),
                                       "" + stringVal );
    }

    static String removeTrailingZero(String stringVal) {
        if ( stringVal.endsWith( ".0" ) ) {
            stringVal = stringVal.substring( 0,
                                             stringVal.length() - 2 );
        }
        return stringVal;
    }

    /**
     * Scan an Excel file stream.
     * 
     * @param inStream
     *            the ms-excel data stream
     */
    public void parseFile(final InputStream inStream) {
        InputStream din = null;
        POIFSFileSystem poifs = null;

        try {
            poifs = new POIFSFileSystem( inStream );

            // Get the workbook for further references
            this._workbook = new HSSFWorkbook( poifs );

            // get the Workbook (excel part) stream in a InputStream
            din = poifs.createDocumentInputStream( ExcelParser.WORKBOOK );
            final HSSFRequest req = new HSSFRequest();
            // lazy listen for ALL records with the listener shown above
            req.addListenerForAllRecords( this );
            final HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents( req,
                                   din );
        } catch ( final IOException ex ) {
            throw new DecisionTableParseException( "Failed to open Excel stream, " + "please check that the content is xls97 format.",
                                                   ex );
        }
    }

}