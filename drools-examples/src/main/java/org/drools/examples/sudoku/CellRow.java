package org.drools.examples.sudoku;

/**
 * Represents a row of Sudoku grid cells.
 */
public class CellRow extends CellFile {
    
    /**
     * Constructor.
     * 
     * @param number the row number.
     */
    public CellRow(int number) {
        super(number);
    }
    
    /*
     * (non-Javadoc)
     * @see sudoku.CellFile#toString()
     */
    @Override
    public String toString() {
        return "Row " + getNumber() + ": " + super.toString();
    }
}
