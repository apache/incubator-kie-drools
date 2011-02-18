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

package org.drools.template.parser;

/**
 * @author <a href="mailto:shaun.addison@gmail.com"> Shaun Addison </a>
 * 
 * Callback interface for scanning an spreadsheet.
 */
public interface DataListener {

    public static final int NON_MERGED = -1;

    /**
     * Start a new sheet
     * 
     * @param name
     *            the sheet name
     */
    public void startSheet(String name);

    /**
     * Come to the end of the sheet.
     */
    public void finishSheet();

    /**
     * Enter a new row.
     * 
     * 
     * @param rowNumber
     *            
     * @param columns
     *            
     */
    public void newRow(int rowNumber,
                       int columns);

    /**
     * Enter a new cell.
     * Do NOT call this event for trailling cells at the end of the line. 
     * It will just confuse the parser. If all the trailing cells are empty, just 
     * stop raising events.
     * 
     * 
     * @param row
     *            the row number
     * @param column
     *            the column alpha character label
     * @param value
     *            the string value of the cell
     * @param mergedCol
     *            the "source" column if it is merged. -1 otherwise.           
     */
    public void newCell(int row,
                        int column,
                        String value,
                        int mergedColStart);

}
