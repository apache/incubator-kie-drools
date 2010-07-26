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

import javax.swing.event.EventListenerList;

public abstract class AbstractSudokuGridModel
{
   private EventListenerList listenerList = new EventListenerList();

   public void addSudokuGridListener(SudokuGridListener l)
   {
      listenerList.add(SudokuGridListener.class, l);
   }
   
   public void removeSudokuGridListener(SudokuGridListener l)
   {
      listenerList.remove(SudokuGridListener.class, l);
   }

   // Notify all listeners that have registered interest for
   // notification on this event type.  The event instance 
   // is lazily created using the parameters passed into 
   // the fire method.

   protected void fireCellResolvedEvent(SudokuGridEvent ev)
   {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == SudokuGridListener.class)
         {
            ((SudokuGridListener) listeners[i + 1]).cellResolved(ev);
         }
      }
   }
   
   protected void fireCellUpdatedEvent(SudokuGridEvent ev)
   {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == SudokuGridListener.class)
         {
            ((SudokuGridListener) listeners[i + 1]).cellModified(ev);
         }
      }
   }
}
