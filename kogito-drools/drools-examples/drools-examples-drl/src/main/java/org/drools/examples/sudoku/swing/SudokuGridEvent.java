/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.swing;

import java.util.EventObject;

public class SudokuGridEvent 
   extends EventObject
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   
   private int row;
   private int col;
   private int value;
   
   public SudokuGridEvent(Object source)
   {
      super(source);
   }
   
   public SudokuGridEvent(Object source, int row, int col, int value)
   {
      this(source);
      this.row=row;
      this.col=col;
      this.value=value;
   }

   public int getCol()
   {
      return col;
   }

   public int getRow()
   {
      return row;
   }

   public int getValue()
   {
      return value;
   }
}
