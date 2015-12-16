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
package org.drools.template;

import org.drools.template.parser.DataListener;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.TemplateDataListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An object of this class acts as a template compiler, inserting spreadsheet
 * data into templates. Template data may come from a resource or an
 * InputStream, or you may provide a TemplateDataListener.
 */
public class DataProviderCompiler {

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider the data provider for the spreadsheet data
     * @param template     the string containing the template resource name
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final String template) {
        final InputStream templateStream = this.getClass().getResourceAsStream(template);
        return compile(dataProvider,
                       templateStream);
    }

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider   the data provider for the spreadsheet data
     * @param templateStream the InputStream for reading the templates
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final InputStream templateStream) {
        return compile(dataProvider,templateStream, true );
    }

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider the data provider for the spreadsheet data
     * @param listener     a template data listener
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final TemplateDataListener listener) {
        return compile(dataProvider, listener, true);
    }

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider the data provider for the spreadsheet data
     * @param template     the string containing the template resource name
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final String template,
                          boolean replaceOptionals) {
        final InputStream templateStream = this.getClass().getResourceAsStream(template);
        return compile(dataProvider,
                       templateStream);
    }

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider   the data provider for the spreadsheet data
     * @param templateStream the InputStream for reading the templates
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final InputStream templateStream,
                          boolean replaceOptionals) {
        DefaultTemplateContainer tc = new DefaultTemplateContainer(templateStream, replaceOptionals);
        closeStream(templateStream);
        return compile(dataProvider,
                       new TemplateDataListener(tc));
    }

    /**
     * Generates DRL from a data provider for the spreadsheet data and templates.
     *
     * @param dataProvider the data provider for the spreadsheet data
     * @param listener     a template data listener
     * @return the generated DRL text as a String
     */
    public String compile(final DataProvider dataProvider,
                          final TemplateDataListener listener,
                          boolean replaceOptionals) {
        List<DataListener> listeners = new ArrayList<DataListener>();
        listeners.add(listener);
        processData(dataProvider,
                    listeners);
        return listener.renderDRL();
    }

    private void processData(final DataProvider dataProvider,
                             List<DataListener> listeners) {
        for (int i = 0; dataProvider.hasNext(); i++) {
            String[] row = dataProvider.next();
            newRow(listeners,
                   i,
                   row.length);
            for (int cellNum = 0; cellNum < row.length; cellNum++) {
                String cell = row[cellNum];

                newCell(listeners,
                        i,
                        cellNum,
                        cell,
                        DataListener.NON_MERGED);
            }
        }
        finishData(listeners);
    }

    private void finishData(List<DataListener> listeners) {
        for (DataListener listener : listeners) {
            listener.finishSheet();
        }
    }

    private void newRow(List<DataListener> listeners,
                        int row,
                        int cols) {
        for (DataListener listener : listeners) {
            listener.newRow(row,
                            cols);
        }
    }

    public void newCell(List<DataListener> listeners,
                        int row,
                        int column,
                        String value,
                        int mergedColStart) {
        for (DataListener listener : listeners) {
            listener.newCell(row,
                             column,
                             value,
                             mergedColStart);
        }
    }

    protected void closeStream(final InputStream stream) {
        try {
            stream.close();
        } catch (final Exception e) {
            System.err.print("WARNING: Wasn't able to correctly close stream for rule template. " + e.getMessage());
        }
    }

}
