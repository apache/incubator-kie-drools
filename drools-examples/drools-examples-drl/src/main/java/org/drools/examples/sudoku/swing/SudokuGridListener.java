/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.swing;

import java.util.EventListener;

/**
 * Defines the callbacks that can be implemented to listen to events from a SudokuGridModel.
 * 
 * @see SudokuGridModel
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
public interface SudokuGridListener
   extends EventListener
{
   /**
    * Fired when a cell in the Sudoku grid is resolved for the first time to a specific value
    * 
    * @param ev an event containing the cell which is resolved along with the value it now has
    */
   public void cellResolved(SudokuGridEvent ev);

   /**
    * Fired when a cell in the Sudoku grid is updated to a new value
    * 
    * @param ev an event containing the cell which is updated along with the value it now has
    */
   public void cellModified(SudokuGridEvent ev);
}
