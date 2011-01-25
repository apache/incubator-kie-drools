/*
 * Copyright 2005 JBoss Inc
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

package org.drools;

import java.io.Serializable;


public class Cell implements Serializable {
    public static final int LIVE = 1;
    public static final int DEAD = 2;

    int value = 0;
    int row;
    int col;
    int state;

    public Cell() {

    }

    public Cell(final int value) {
        this.value = value;
    }
    
    public Cell(int state,
                int row,
                int col) {
        super();
        this.state = state;
        this.row = row;
        this.col = col;
//        if( row == 1 && col == 1 ) {
//            value = 8;
//        } else if( row+col == 2 ) {
//            value = 3;
//        } else {
//            value = 5;
//        }
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public int getX() {
        return row;
    }

    public void setX(int x) {
        this.row = x;
    }

    public int getY() {
        return col;
    }

    public void setY(int y) {
        this.col = y;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return "Cell( ["+row+","+col+"] "+( (state==DEAD)?"DEAD":"LIVE") +" = "+value+" )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + col;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Cell other = (Cell) obj;
        if ( row != other.row ) return false;
        if ( col != other.col ) return false;
        return true;
    }
    
    
}
