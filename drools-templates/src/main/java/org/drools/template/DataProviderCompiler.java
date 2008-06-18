package org.drools.template;

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
import java.util.List;

import org.drools.template.parser.DataListener;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.TemplateDataListener;

public class DataProviderCompiler {

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
    public String compile(final DataProvider dataProvider,
                          final String template) {
        final InputStream templateStream = this.getClass().getResourceAsStream( template );
        return compile( dataProvider,
                        templateStream );
    }

    public String compile(final DataProvider dataProvider,
                          final InputStream templateStream) {
        TemplateContainer tc = new DefaultTemplateContainer( templateStream );
        closeStream( templateStream );
        return compile( dataProvider,
                        new TemplateDataListener( tc ) );
    }

    public String compile(final DataProvider dataProvider,
                          final TemplateDataListener listener) {
        List<DataListener> listeners = new ArrayList<DataListener>();
        listeners.add( listener );
        processData( dataProvider,
                     listeners );
        return listener.renderDRL();
    }

    private void processData(final DataProvider dataProvider,
                             List<DataListener> listeners) {
        for ( int i = 0; dataProvider.hasNext(); i++ ) {
            String[] row = dataProvider.next();
            newRow( listeners,
                    i,
                    row.length );
            for ( int cellNum = 0; cellNum < row.length; cellNum++ ) {
                String cell = row[cellNum];

                newCell( listeners,
                         i,
                         cellNum,
                         cell,
                         DataListener.NON_MERGED );
            }
        }
        finishData( listeners );
    }

    private void finishData(List<DataListener> listeners) {
        for ( DataListener listener : listeners ) {
            listener.finishSheet();
        }
    }

    private void newRow(List<DataListener> listeners,
                        int row,
                        int cols) {
        for ( DataListener listener : listeners ) {
            listener.newRow( row,
                             cols );
        }
    }

    public void newCell(List<DataListener> listeners,
                        int row,
                        int column,
                        String value,
                        int mergedColStart) {
        for ( DataListener listener : listeners ) {
            listener.newCell( row,
                              column,
                              value,
                              mergedColStart );
        }
    }

    private void closeStream(final InputStream stream) {
        try {
            stream.close();
        } catch ( final Exception e ) {
            System.err.print( "WARNING: Wasn't able to correctly close stream for rule template. " + e.getMessage() );
        }
    }

}
