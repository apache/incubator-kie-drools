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
