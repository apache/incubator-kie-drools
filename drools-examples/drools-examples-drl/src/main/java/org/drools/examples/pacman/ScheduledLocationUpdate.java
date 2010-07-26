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

package org.drools.examples.pacman;

public class ScheduledLocationUpdate {
    private Character character;
    private Location  location;
    private int       row;
    private int       col;
    private int       tock;

    public ScheduledLocationUpdate(Character character,
                                   Location location,
                                   int row,
                                   int col,
                                   int tock) {
        this.character = character;
        this.location = location;
        this.row = row;
        this.col = col;
        this.tock = tock;
    }

    public Character getCharacter() {
        return character;
    }

    public Location getLocation() {
        return location;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getTock() {
        return tock;
    }

    public void setTock(int tock) {
        this.tock = tock;
    }

    @Override
    public String toString() {
        return "ScheduledLocationUpdate " + location.getCharacter() + " " + row + ":" + col;
    }
}
