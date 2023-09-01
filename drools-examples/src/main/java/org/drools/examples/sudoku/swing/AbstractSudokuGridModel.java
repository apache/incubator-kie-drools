package org.drools.examples.sudoku.swing;

import javax.swing.event.EventListenerList;

public abstract class AbstractSudokuGridModel {
   private EventListenerList listenerList = new EventListenerList();

   public void addSudokuGridListener(SudokuGridListener l){
      listenerList.add(SudokuGridListener.class, l);
   }
   
   public void removeSudokuGridListener(SudokuGridListener l){
      listenerList.remove(SudokuGridListener.class, l);
   }

   private void forwardEvent(SudokuGridEvent ev){
       // Guaranteed to return a non-null array
       Object[] listeners = listenerList.getListenerList();
       // Process the listeners last to first, notifying
       // those that are interested in this event
       for (int i = listeners.length - 2; i >= 0; i -= 2){
           if (listeners[i] == SudokuGridListener.class){
               ((SudokuGridListener) listeners[i + 1]).restart(ev);
           }
       }
   }
   
   protected void fireRestartEvent(SudokuGridEvent ev) {
       forwardEvent(ev);
   }
   
   protected void fireCellUpdatedEvent(SudokuGridEvent ev) {
       forwardEvent(ev);
   }
}
