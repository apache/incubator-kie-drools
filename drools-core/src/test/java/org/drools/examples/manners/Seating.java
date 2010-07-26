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

package org.drools.examples.manners;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Seating
    implements
    Externalizable {
    /**
     *
     */
    private static final long serialVersionUID = 400L;

    private int               id, pid;

    private int               leftSeat, rightSeat;

    private String            leftGuestName, rightGuestName;

    private boolean           pathDone;

    public Seating() {
    }

    public Seating(final int id,
                   final int pid,
                   final boolean pathDone,
                   final int leftSeat,
                   final String leftGuestName,
                   final int rightSeat,
                   final String rightGuestName) {
        super();
        this.id = id;
        this.pid = pid;
        this.pathDone = pathDone;
        this.leftSeat = leftSeat;
        this.leftGuestName = leftGuestName;
        this.rightSeat = rightSeat;
        this.rightGuestName = rightGuestName;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id  = in.readInt();
        pid = in.readInt();
        leftSeat = in.readInt();
        rightSeat = in.readInt();
        leftGuestName   = (String)in.readObject();
        rightGuestName  = (String)in.readObject();
        pathDone    = in.readBoolean();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeInt(pid);
        out.writeInt(leftSeat);
        out.writeInt(rightSeat);
        out.writeObject(leftGuestName);
        out.writeObject(rightGuestName);
        out.writeBoolean(pathDone);
    }

    public boolean isPathDone() {
        return this.pathDone;
    }

    public void setPathDone(final boolean pathDone) {
        this.pathDone = pathDone;
    }

    public int getId() {
        return this.id;
    }

    public String getLeftGuestName() {
        return this.leftGuestName;
    }

    public int getLeftSeat() {
        return this.leftSeat;
    }

    public int getPid() {
        return this.pid;
    }

    public String getRightGuestName() {
        return this.rightGuestName;
    }

    public int getRightSeat() {
        return this.rightSeat;
    }

    public String toString() {
        return "[Seating id=" + this.id + " , pid=" + this.pid + " , pathDone=" + this.pathDone + " , leftSeat=" + this.leftSeat + ", leftGuestName=" + this.leftGuestName + ", rightSeat=" + this.rightSeat + ", rightGuestName=" + this.rightGuestName
               + "]";
    }
}