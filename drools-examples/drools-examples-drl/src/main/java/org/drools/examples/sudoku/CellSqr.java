/*
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
package org.drools.examples.sudoku;

/**
 * Represents a 3x3 area of Sudoku grid cells.
 *
 * @author Wolfgang Laun
 */
public class CellSqr extends CellGroup {
    
    /**
     * Constructor.
     * @param cellRow0 the 1st row passing through this block.
     * @param cellRow1 the 2nd row passing through this block.
     * @param cellRow2 the 3rd row passing through this block.
     * @param cellCol0 the 1st column passing through this block.
     * @param cellCol1 the 2nd column passing through this block.
     * @param cellCol2 the 3rd column passing through this block.
     */
    public CellSqr( CellRow cellRow0, CellRow cellRow1, CellRow cellRow2,
               CellCol cellCol0, CellCol cellCol1, CellCol cellCol2 ){
        super();

        for( int iRow = cellRow0.getNumber(); iRow <=  cellRow2.getNumber(); iRow++ ){
            addCell( cellCol0.getCells().get( iRow ) );
            addCell( cellCol1.getCells().get( iRow ) );
            addCell( cellCol2.getCells().get( iRow ) );
        }
    }
}
