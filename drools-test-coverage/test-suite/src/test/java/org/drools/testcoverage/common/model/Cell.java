/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Cell implements Serializable {

    public static final int LIVE = 1;
    private static final int DEAD = 2;

    private int value = 0;
    private int row;
    private int col;

    private int state;

    public Cell() {

    }

    public Cell(final int value) {
        this.value = value;
    }

    public Cell(final int state,
                final int row,
                final int col) {
        super();
        this.state = state;
        this.row = row;
        this.col = col;
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

    public void setX(final int x) {
        this.row = x;
    }

    public int getY() {
        return col;
    }

    public void setY(final int y) {
        this.col = y;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Cell( [" + row + "," + col + "] " + ((state == DEAD) ? "DEAD" : "LIVE") + " = " + value + " )";
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (row != other.row) {
            return false;
        }
        if (col != other.col) {
            return false;
        }
        return true;
    }
}
