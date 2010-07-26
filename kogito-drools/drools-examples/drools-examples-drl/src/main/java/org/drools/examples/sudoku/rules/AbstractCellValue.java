/**
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

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.rules;

import org.drools.examples.sudoku.swing.SudokuGridModel;

/**
 * An abstract class which contains the methods common to both a PossibleCellValue
 * in a Sudoku grid or a ResolvedCellValue. Stores the row and col for the cell
 * as well as the 3x3 "zone" in which the cell is contained and the value 
 * entered in the cell.
 * 
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractCellValue
{
   // The value for this cell in the Grid from 1-9
   private int value;
   
   // The row for this cell in the Grid
   private int row;
   
   // The column for this cell in the Grid
   private int col;
   
   // The zone for this cell in the Grid
   private int zone;         
   
   /**
    * Create a new AbstractCellValue with the specified value at the given
    * row and column in the Grid. The zone is added automatically.
    * 
    * @param value the value for this cell in the Grid from 1-9
    * @param row the value for this cell in the Grid from 1-9
    * @param col the value for this cell in the Grid from 1-9
    */
   protected AbstractCellValue(int value, int row, int col)
   {
      this.value = value;
      this.row = row;
      this.col = col;
      this.zone = deriveZone(row, col);
   }

   /**
    * Create a new AbstractCellValue with the same value, row
    * and column as the passed cellValue. The zone is added automatically.
    * 
    * @param cellValue the existing cellValue to copy the information from
    */ 
   protected AbstractCellValue(AbstractCellValue cellValue)
   {
      this(cellValue.getValue(), cellValue.getRow(), cellValue.getCol());
   }
   
   /**
    * Derives the 3x3 zone in which the cell is located based on its row and column
    * 
    * @param row the cell's row, from 0-8
    * @param col the cell's column, from 0-8
    * @return the cells's zone from 1-9
    */
   private int deriveZone(int row, int col)
   {
      return SudokuGridModel.ZONE_LOOKUP[row][col];
   }
   
   /**
    * Return the value stored in this cell
    * 
    * @return the cell's value from 1-9
    */
   public int getValue()
   {
      return value;
   }

   /**
    * Return the row for this cell
    * 
    * @return the cell's row from 0-8
    */
   public int getRow()
   {
      return row;
   }
   
   /**
    * Return the column for this cell
    * 
    * @return the cell's column from 0-8
    */
   public int getCol()
   {
      return col;
   }

   /**
    * Return the zone for this cell
    * 
    * @return the cell's zone from 1-9
    */
   public int getZone()
   {
      return zone;
   }
}
