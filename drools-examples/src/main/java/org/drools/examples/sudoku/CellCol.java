package org.drools.examples.sudoku;

/**
 * Represents a column of Sudoku grid cells.
 */
public class CellCol extends CellFile {
    
    /**
     * Constructor.
     * 
     * @param number the column number.
     */
    public CellCol(int number) {
        super( number );
    }
    
    /*
     * (non-Javadoc)
     * @see sudoku.CellFile#toString()
     */
    @Override
    public String toString(){
        return "Column " + getNumber() + ": " + super.toString();
    }
}
