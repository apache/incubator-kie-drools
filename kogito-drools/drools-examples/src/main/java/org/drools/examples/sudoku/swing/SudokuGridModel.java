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
package org.drools.examples.sudoku.swing;

import java.util.Set;

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
