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
package org.drools.examples.manners;

import java.io.Serializable;

public class Seating
    implements
    Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -21267542036195318L;

    private Integer         id, pid;

    private Integer         leftSeat, rightSeat;

    private String      leftGuestName, rightGuestName;

    private boolean           pathDone;

    public Seating() {
    }
    
    public Seating(final Integer id,
                   final Integer pid,
                   final boolean pathDone,
                   final Integer leftSeat,
                   final String leftGuestName,
                   final Integer rightSeat,
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

    public boolean isPathDone() {
        return this.pathDone;
    }

    public void setPathDone(final boolean pathDone) {
        this.pathDone = pathDone;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLeftGuestName() {
        return this.leftGuestName;
    }

    public Integer getLeftSeat() {
        return this.leftSeat;
    }

    public Integer getPid() {
        return this.pid;
    }

    public String getRightGuestName() {
        return this.rightGuestName;
    }

    public Integer getRightSeat() {
        return this.rightSeat;
    }

    public String toString() {
        return "[Seating id=" + this.id + " , pid=" + this.pid + " , pathDone=" + this.pathDone + " , leftSeat=" + this.leftSeat + ", leftGuestName=" + this.leftGuestName + ", rightSeat=" + this.rightSeat + ", rightGuestName=" + this.rightGuestName
               + "]";
    }
}