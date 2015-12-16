/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.EventListener;

/**
 * Defines the callbacks that can be implemented to listen to events from a SudokuGridModel.
 * 
 * @see SudokuGridModel
 */
public interface SudokuGridListener  extends EventListener {
   /**
    * Fired when a cell in the Sudoku grid is resolved for the first time to a specific value
    * 
    * @param ev an event containing the cell which is resolved along with the value it now has
    */
   public void restart(SudokuGridEvent ev);

   /**
    * Fired when a cell in the Sudoku grid is updated to a new value
    * 
    * @param ev an event containing the cell which is updated along with the value it now has
    */
   public void cellModified(SudokuGridEvent ev);
}
