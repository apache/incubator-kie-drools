package org.drools.examples.sudoku.swing;

import java.util.EventObject;

public class SudokuGridEvent extends EventObject {
   private static final long serialVersionUID = 510l;
   
   private int row;
   private int col;
   private int value;
   
   public SudokuGridEvent(Object source) {
      super(source);
   }
   
   public SudokuGridEvent(Object source, int row, int col, int value) {
      this(source);
      this.row=row;
      this.col=col;
      this.value=value;
   }

   public int getCol() {
      return col;
   }

   public int getRow() {
      return row;
   }

   public int getValue() {
      return value;
   }
}
