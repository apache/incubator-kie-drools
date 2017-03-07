/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;

public class Door extends Thing {
    @Position(2)
    private Room fromRoom;

    @Position(3)
    private Room toRoom;

    @Position(4)
    private LockStatus lockStatus;

    @Position(5)
    private Key key;

    public Door(Room fromRoom, Room toRoom) {
        super( "Door from " + fromRoom.getName() +" to "+ toRoom.toString(), false );
        this.fromRoom = fromRoom;
        this.toRoom = toRoom;
        lockStatus = LockStatus.UNLOCKED;
    }

    public Room getFromRoom() {
        return fromRoom;
    }

    public void setFromRoom(Room fromRoom) {
        this.fromRoom = fromRoom;
    }

    public Room getToRoom() {
        return toRoom;
    }

    public void setToRoom(Room toRoom) {
        this.toRoom = toRoom;
    }

    public LockStatus getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(LockStatus lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }

        Door door = (Door) o;

        if (!fromRoom.equals(door.fromRoom)) { return false; }
        if (!toRoom.equals(door.toRoom)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + fromRoom.hashCode();
        result = 31 * result + toRoom.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Door{" +
               "fromRoom=" + fromRoom +
               ", toRoom=" + toRoom +
               '}';
    }
}
