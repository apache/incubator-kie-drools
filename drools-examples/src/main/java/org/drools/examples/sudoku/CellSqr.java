package org.drools.examples.sudoku;

/**
 * Represents a 3x3 area of Sudoku grid cells.
 */
public class CellSqr extends CellGroup {
    
    /**
     * Constructor.
     * @param cellRow0 the 1st row passing through this block.
     * @param cellRow1 the 2nd row passing through this block.
     * @param cellRow2 the 3rd row passing through this block.
     * @param cellCol0 the 1st column passing through this block.
     * @param cellCol1 the 2nd column passing through this block.
     * @param cellCol2 the 3rd column passing through this block.
     */
    public CellSqr(CellRow cellRow0, CellRow cellRow1, CellRow cellRow2,
                   CellCol cellCol0, CellCol cellCol1, CellCol cellCol2) {
        super();

        for (int iRow = cellRow0.getNumber(); iRow <=  cellRow2.getNumber(); iRow++) {
            addCell(cellCol0.getCells().get(iRow));
            addCell(cellCol1.getCells().get(iRow));
            addCell(cellCol2.getCells().get(iRow));
        }
    }
}
