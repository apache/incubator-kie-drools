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

package org.drools.decisiontable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.decisiontable.parser.RuleSheetListener;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.model.DRLOutput;
import org.drools.template.model.Package;
import org.drools.template.parser.DataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class handles the input XLS and CSV and extracts the rule DRL, ready for
 * pumping into drools.
 */
public class SpreadsheetCompiler {

    protected static transient Logger logger = LoggerFactory.getLogger(SpreadsheetCompiler.class);

    /**
     * Generates DRL from the input stream containing the spreadsheet.
     *
     * @param showPackage
     *            tells it to print or not print any package statements in the spreadsheet.
     * @param xlsStream
     *            The stream to the spreadsheet. Uses the first worksheet found
     *            for the decision tables, ignores others.
     * @return DRL xml, ready for use in drools.
     */
    public String compile(boolean showPackage,
                          final InputStream xlsStream,
                          final InputType type) {
        return compile( xlsStream,
                        type,
                        new DefaultRuleSheetListener( showPackage ) );
    }

    /**
     * Generates DRL from the input stream containing the spreadsheet.
     *
     * @param xlsStream
     *            The stream to the spreadsheet. Uses the first worksheet found
     *            for the decision tables, ignores others.
     * @return DRL xml, ready for use in drools.
     */
    public String compile(final InputStream xlsStream,
                          final InputType type) {
        return compile( xlsStream,
                        type,
                        new DefaultRuleSheetListener() );
    }

    /**
     * Generates DRL from the input stream containing the spreadsheet.
     *
     * @param xlsStream
     *            The stream to the spreadsheet. Uses the first worksheet found
     *            for the decision tables, ignores others.
     * @param type
     *            The type of the file - InputType.CSV or InputType.XLS
     * @param listener
     *
     * @return DRL xml, ready for use in drools.
     */
    public String compile(final InputStream xlsStream,
                          final InputType type,
                          final RuleSheetListener listener) {
        final DecisionTableParser parser = type.createParser( listener );
        parser.parseFile( xlsStream );
        final Package rulePackage = listener.getRuleSet();
        final DRLOutput out = new DRLOutput();
        rulePackage.renderDRL( out );
        return out.getDRL();
    }

    /**
     * Convenience implementation, taking rules from the classpath. It is
     * recommended to use the stream version, as you can then change rules
     * dynamically. (that is a lot of the benefit of rule engines !).
     *
     * @param classPathResource
     *            full class path to the spreadsheet you wish to convert to DRL.
     *            Uses the first worksheet for the decision tables.
     * @return DRL.
     */
    public String compile(final String classPathResource,
                          final InputType inputType) {
        final InputStream stream = this.getClass().getResourceAsStream( classPathResource );
        try {
            final String drl = compile( stream,
                                        inputType );
            return drl;
        } finally {
            closeStream( stream );
        }
    }

    /**
     * Looks for a named worksheet to find the decision tables on. Only works
     * with XLS format spreadsheets (as they have multiple worksheets).
     *
     * @param stream
     *            The stream of the decision tables (spreadsheet) IN XLS format !!
     * @param worksheetName
     *            The name of the worksheet that the decision tables live on.
     * @return DRL, ready to go.
     */
    public String compile(final InputStream stream,
                          final String worksheetName) {
        final RuleSheetListener listener = getRuleSheetListener( stream,
                                                                 worksheetName );
        final Package rulePackage = listener.getRuleSet();
        final DRLOutput out = new DRLOutput();
        rulePackage.renderDRL( out );
        return out.getDRL();
    }

    private RuleSheetListener getRuleSheetListener(final InputStream stream,
                                                   final String worksheetName) {
        final RuleSheetListener listener = new DefaultRuleSheetListener();
        final Map<String, List<DataListener>> sheetListeners = new HashMap<String, List<DataListener>>();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        listeners.add(listener);
        sheetListeners.put( worksheetName,
                       listeners );
        final ExcelParser parser = new ExcelParser( sheetListeners );
        parser.parseFile( stream );
        return listener;
    }

    private void closeStream(final InputStream stream) {
        try {
            stream.close();
        } catch ( final Exception e ) {
            logger.warn("WARNING: Wasn't able to " + "correctly close stream for decision table. " + e.getMessage());
        }
    }

}
