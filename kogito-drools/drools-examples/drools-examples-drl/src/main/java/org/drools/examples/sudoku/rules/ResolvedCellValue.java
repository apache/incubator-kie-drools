/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.rules;

/**
 * Represents a single, resolved value for a given cell, used when we 
 * have determined what the value for this cell is, only a 
 * single ResolvedCellValue object should be associated with a 
 * given row and column in a solved Grid.
 * 
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public class ResolvedCellValue
   extends AbstractCellValue
{
   /**
    * Uses the constructor on the superclass to construct a new ResolvedCellValue
    * 
    * @param value the resolved value in the cell from 1-9
    * @param row the row index from 0-8
    * @param col the column index from 0-8
    */   
   public ResolvedCellValue(int value, int row, int col)
   {
      super(value, row, col);
   }
   
   /**
    * Uses the constructor on the superclass to construct a new PossibleCellValue
    * 
    * @param cellValue the existing cellValue to copy the information from
    */ 
   public ResolvedCellValue(AbstractCellValue cellValue)
   {
      super(cellValue);
   }   
}
