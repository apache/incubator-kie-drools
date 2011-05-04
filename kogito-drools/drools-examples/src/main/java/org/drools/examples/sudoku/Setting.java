/*
 * Copyright 2011 JBoss Inc
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
package org.drools.examples.sudoku;

/**
 * Represents a temporary fact used for assigning a value to a cell.
 */
public class Setting {
    
    private int rowNo;
    private int colNo;
    private Integer value;
    
    /**
     * Constructor.
     * @param row the row number of the Cell to set
     * @param col the column number of the Cell to set
     * @param value the value to set
     */
    public Setting (int row, int col, Integer value) {
        this.rowNo = row;
        this.colNo = col;
        this.value = value;
    }

    /**
     * Returns the row number.
     * @return an int value
     */
    public int getRowNo() {
        return rowNo;
    }

    /**
     * Returns the column number.
     * @return an int value
     */
    public int getColNo() {
        return colNo;
    }
    /**
     * Returns the value.
     * @return an Integer object
     */
    public Integer getValue() {
        return value;
    }
}
