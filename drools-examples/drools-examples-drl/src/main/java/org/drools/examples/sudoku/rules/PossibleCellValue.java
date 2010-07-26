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

/**
 * Represents a possible value for a given cell, used when we are not 
 * currently certain what the value for this cell is, so we need 
 * to associated multiple PossibleCellValue objects with the 
 * same row and column in the Grid.
 * 
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public final class PossibleCellValue
   extends AbstractCellValue
{
   /**
    * Uses the constructor on the superclass to construct a new PossibleCellValue
    * 
    * @param value the potential value in the cell from 1-9
    * @param row the row index from 0-8
    * @param col the column index from 0-8
    */
   public PossibleCellValue(int value, int row, int col)
   {
      super(value, row, col);
   }
   
   /**
    * Uses the constructor on the superclass to construct a new PossibleCellValue
    * 
    * @param cellValue the existing cellValue to copy the information from
    */ 
   public PossibleCellValue(AbstractCellValue cellValue)
   {
      super(cellValue);
   }
}
