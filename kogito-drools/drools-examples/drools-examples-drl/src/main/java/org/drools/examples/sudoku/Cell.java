/*
 * Copyright 2010 JBoss Inc
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

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single cell in the Sudoku grid.
 * 
 * @author Wolfgang Laun
 */
public class Cell extends SetOfNine {

    private Integer   value;
    private CellRow   cellRow;
    private CellCol   cellCol;
    private CellSqr   cellSqr;
    private Set<Cell> exCells;

    /**
     * Constructor, leaving all references at null. You must call
     * makeReferences to complete the object. 
     */
    public Cell(){
        super();
    }
    
    /**
     * Set references to all cell groups containing this cell. 
     * @param row the cell group for the row
     * @param col the cell group for the column
     * @param sqr the cell group for the square 3x3 area
     */
    public void makeReferences( CellRow row, CellCol col, CellSqr sqr ){
        this.cellRow = row;
        this.cellCol = col;
        this.cellSqr = sqr;
        this.exCells = new HashSet<Cell>();
        this.exCells.addAll( this.cellRow.getCells() );
        this.exCells.addAll( this.cellCol.getCells() );
        this.exCells.addAll( this.cellSqr.getCells() );
        this.exCells.remove( this );
    }

    /**
     * Retrieves the value.
     * @return an Integer or null
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Set the cell value.
     * @param value an Integer object
     */
    public void setValue(Integer value) {
        blockExcept();
        this.value = value;
    }
    
    /**
     * Return the set of Cell objects where contents are mutually exclusive with
     * this cell; they are in the same row or same column or same block.
     * 
     * @return a Set of Cell objects not including this cell.
     */
    public Set<Cell> getExCells(){
        return exCells;
    }
    
    /**
     * Returns the row group of nine of this cell.
     * @return a CellRow object.
     */
    public CellRow getCellRow() {
        return cellRow;
    }
    
    /**
     * Returns the row number.
     * @return an int value.
     */
    public int getRowNo() {
        return cellRow.getNumber();
    }

    /**
     * Returns the column group of nine of this cell.
     * @return a CellCol object.
     */
    public CellCol getCellCol() {
        return cellCol;
    }

    /**
     * Returns the column number.
     * @return an int value.
     */
    public int getColNo() {
        return cellCol.getNumber();
    }

    /**
     * Returns the 3x3 block group of nine of this cell.
     * @return a cellSqr object.
     */
    public CellSqr getCellSqr() {
        return cellSqr;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        String rowNo = cellRow == null ? "?" : Integer.toString( cellRow.getNumber() );
        String colNo = cellCol == null ? "?" : Integer.toString( cellCol.getNumber() );
        return "[" + rowNo + "," + colNo + "]: " + (value == null ? " " : value.toString());
    }

    public String valueAsString(){
        return value == null ? " " : value.toString();
    }

}
