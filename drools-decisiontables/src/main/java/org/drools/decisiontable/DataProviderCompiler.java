package org.drools.decisiontable;

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
import java.util.Iterator;
import java.util.List;

import org.drools.decisiontable.parser.DefaultTemplateContainer;
import org.drools.decisiontable.parser.ExternalSheetListener;
import org.drools.decisiontable.parser.SheetListener;
import org.drools.decisiontable.parser.TemplateContainer;

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
                        new ExternalSheetListener( tc ) );
    }

    public String compile(final DataProvider dataProvider,
                          final ExternalSheetListener listener) {
        List listeners = new ArrayList();
        listeners.add( listener );
        processData( dataProvider,
                     listeners );
        return listener.renderDRL();
    }

    private void processData(final DataProvider dataProvider,
                             List listeners) {
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
                         SheetListener.NON_MERGED );
            }
        }
        finishData( listeners );
    }

    private void finishData(List listeners) {
        for ( Iterator it = listeners.iterator(); it.hasNext(); ) {
            SheetListener listener = (SheetListener) it.next();
            listener.finishSheet();
        }
    }

    private void newRow(List listeners,
                        int row,
                        int cols) {
        for ( Iterator it = listeners.iterator(); it.hasNext(); ) {
            SheetListener listener = (SheetListener) it.next();
            listener.newRow( row,
                             cols );
        }
    }

    public void newCell(List listeners,
                        int row,
                        int column,
                        String value,
                        int mergedColStart) {
        for ( Iterator it = listeners.iterator(); it.hasNext(); ) {
            SheetListener listener = (SheetListener) it.next();
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
