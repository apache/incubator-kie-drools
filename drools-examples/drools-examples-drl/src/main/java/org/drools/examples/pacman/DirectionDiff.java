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

package org.drools.examples.pacman;

public class DirectionDiff {
    private Character fromChar;
    private Character toChar;
    private int col;
    private int row;
    private int colDiff;
    private int rowDiff;
    
    public DirectionDiff(Character fromChar,
                         Character toChar,
                         int col,
                         int row,
                         int colDiff,
                         int rowDiff) {
        this.fromChar = fromChar;
        this.toChar = toChar;
        this.col = col;
        this.row = row;
        this.colDiff = colDiff;
        this.rowDiff = rowDiff;
    }

    public Character getFromChar() {
        return fromChar;
    }

    public Character getToChar() {
        return toChar;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getColDiff() {
        return colDiff;
    }

    public int getRowDiff() {
        return rowDiff;
    }

    public String toString() {
        return "from: " + fromChar + " to: " + toChar + " col: " + col + " row: " + row + " colDiff: " + colDiff + " rowDiff: " + rowDiff; 
    }
    
}
