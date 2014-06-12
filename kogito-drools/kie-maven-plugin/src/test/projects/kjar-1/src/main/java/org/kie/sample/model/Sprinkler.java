/*
 * Copyright 2015 JBoss Inc
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
package org.kie.sample.model;

public class Sprinkler {

    private Room room;
    private boolean on = false;

    public Sprinkler() { }

    public Sprinkler(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public int hashCode() {
        return room.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sprinkler)) { return false; }
        return room.equals(((Sprinkler) obj).getRoom());
    }

    @Override
    public String toString() {
        return "Sprinkler for " + room;
    }
}
