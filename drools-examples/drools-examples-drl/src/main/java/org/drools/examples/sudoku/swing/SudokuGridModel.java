/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.swing;

import java.util.Set;

/**
 * An interface representing a 9x9 Sudoku Grid of Cells.
 * <p>
 * In a solved Sodoku grid, each cell must contain an 
 * integer from 1-9 and the same integer must not be 
 * repeated in the same row, in the same column or in 
 * the same 3x3 subsection of the grid.
 * 
 * @see SudokuGridListener
 * @see SudokuGridEvent
 * @see SudokuGridView
 * @see AbstractSudokuGridModel
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface SudokuGridModel
{
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

   /**
    * A NUM_ROWSxNUM_COLS two dimensional array which maps from rows and columns
    * to the 3x3 subzones in a Sudoku grid
    */
   public static int[][] ZONE_LOOKUP = 
   { { 1 , 1 , 1 , 2 , 2 , 2 , 3 , 3 , 3},
     { 1 , 1 , 1 , 2 , 2 , 2 , 3 , 3 , 3},
     { 1 , 1 , 1 , 2 , 2 , 2 , 3 , 3 , 3},
     { 4 , 4 , 4 , 5 , 5 , 5 , 6 , 6 , 6},
     { 4 , 4 , 4 , 5 , 5 , 5 , 6 , 6 , 6},
     { 4 , 4 , 4 , 5 , 5 , 5 , 6 , 6 , 6},
     { 7 , 7 , 7 , 8 , 8 , 8 , 9 , 9 , 9},
     { 7 , 7 , 7 , 8 , 8 , 8 , 9 , 9 , 9},
     { 7 , 7 , 7 , 8 , 8 , 8 , 9 , 9 , 9} };  
   
   public void setCellValues(Integer[][] cellValues);
   
   public boolean isCellEditable(int row, int col);
   
   public boolean isCellResolved(int row, int col);
   
   public Set<Integer> getPossibleCellValues(int row, int col);
   
   public boolean solve();
   
   public boolean isGridSolved();
   
   public void addSudokuGridListener(SudokuGridListener l);   
   
   public void removeSudokuGridListener(SudokuGridListener l);
}
