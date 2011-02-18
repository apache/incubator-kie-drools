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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.TemplateDataListener;

public class ExternalSpreadsheetCompiler {

    public String compile(final String xls,
                          final String template,
                          int startRow,
                          int startCol) {
        return compile( xls,
                        template,
                        InputType.XLS,
                        startRow,
                        startCol );

    }

    public String compile(final String xls,
                          final String template,
                          InputType type,
                          int startRow,
                          int startCol) {
        final InputStream xlsStream = this.getClass().getResourceAsStream( xls );
        final InputStream templateStream = this.getClass().getResourceAsStream( template );
        return compile( xlsStream,
                        templateStream,
                        type,
                        startRow,
                        startCol );

    }

    public String compile(final String xls,
                          final String worksheetName,
                          final String template,
                          int startRow,
                          int startCol) {
        final InputStream xlsStream = this.getClass().getResourceAsStream( xls );
        final InputStream templateStream = this.getClass().getResourceAsStream( template );
        return compile( xlsStream,
                        worksheetName,
                        templateStream,
                        startRow,
                        startCol );

    }

    public String compile(final InputStream xlsStream,
                          final InputStream templateStream,
                          int startRow,
                          int startCol) {
        return compile( xlsStream,
                        templateStream,
                        InputType.XLS,
                        startRow,
                        startCol );
    }

    public String compile(final InputStream xlsStream,
                          final InputStream templateStream,
                          InputType type,
                          int startRow,
                          int startCol) {
        TemplateContainer tc = new DefaultTemplateContainer( templateStream );
        closeStream( templateStream );
        return compile( xlsStream,
                        type,
                        new TemplateDataListener( startRow,
                                                  startCol,
                                                  tc ) );
    }

    public String compile(final InputStream xlsStream,
                          final String worksheetName,
                          final InputStream templateStream,
                          int startRow,
                          int startCol) {
        TemplateContainer tc = new DefaultTemplateContainer( templateStream );
        closeStream( templateStream );
        return compile( xlsStream,
                        worksheetName,
                        new TemplateDataListener( startRow,
                                                  startCol,
                                                  tc ) );
    }

    public void compile(final String xls,
                        InputType type,
                        final List<DataListener> listeners) {
        final InputStream xlsStream = this.getClass().getResourceAsStream( xls );
        compile( xlsStream,
                 type,
                 listeners );
    }

    public void compile(final String xls,
                        final Map<String, List<DataListener>> listeners) {
        final InputStream xlsStream = this.getClass().getResourceAsStream( xls );
        compile( xlsStream,
                 listeners );
    }

    public void compile(final InputStream xlsStream,
                        InputType type,
                        final List<DataListener> listeners) {
        final DecisionTableParser parser = type.createParser( listeners );
        parser.parseFile( xlsStream );
        closeStream( xlsStream );
    }

    public void compile(final InputStream xlsStream,
                        final Map<String, List<DataListener>> listeners) {
        final DecisionTableParser parser = new ExcelParser( listeners );
        parser.parseFile( xlsStream );
        closeStream( xlsStream );
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
     * @return DRL xml, ready for use in drools.
     * @throws IOException
     */
    public String compile(final InputStream xlsStream,
                          final InputType type,
                          final TemplateDataListener listener) {
        ArrayList<DataListener> listeners = new ArrayList<DataListener>();
        listeners.add( listener );
        compile( xlsStream,
                 type,
                 listeners );
        return listener.renderDRL();
    }

    public String compile(final InputStream xlsStream,
                          final String worksheetName,
                          final TemplateDataListener listener) {
        Map<String, List<DataListener>> listeners = new HashMap<String, List<DataListener>>();
        List<DataListener> l = new ArrayList<DataListener>();
        l.add( listener );
        listeners.put( worksheetName,
                       l );
        compile( xlsStream,
                 listeners );
        return listener.renderDRL();
    }

    private void closeStream(final InputStream stream) {
        try {
            stream.close();
        } catch ( final Exception e ) {
            System.err.print( "WARNING: Wasn't able to " + "correctly close stream for decision table. " + e.getMessage() );
        }
    }

}
