package org.drools.examples.sudoku.swing;

/**
 * An interface representing a 9x9 Sudoku Grid of Cells.
 */
public interface SudokuGridModel {
   /**
    * The number of rows in the Grid, i.e. the height
    */
   public static int NUM_ROWS = 9;

   /**
    * The number of columns in the Grid, i.e. the width
    */
   public static int NUM_COLS = 9;

   /**
    * The number of colums that make up a zone within the Grid
    */
   public static int INNER_GRID_WIDTH = 3;   
   
   /**
    * The number of rows that make up a zone within the Grid
    */
   public static int INNER_GRID_HEIGHT = 3;

   public void setCellValues(Integer[][] cellValues);
      
   public String getCellValue( int iRow, int iCol );
   
   public void solve();
   
   public void step();
      
   public void addSudokuGridListener(SudokuGridListener l);   
   
   public void removeSudokuGridListener(SudokuGridListener l);
}
