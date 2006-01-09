package org.drools.examples.manners;

/*
 * $Id: Seating.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
 *
 * Copyright 2002 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Seating
    implements
    Serializable {
    private final int id, pid;
    
    private final int     leftSeat, rightSeat;

    private final String   leftGuestName, rightGuestName;

    private boolean pathDone;
    
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

    public boolean isPathDone() {
        return this.pathDone;
    }

    public void setPathDone(boolean pathDone) {
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
        return "[Seating id=" + this.id + " , pid=" + this.pid + " , pathDone=" + this.pathDone + " , leftSeat=" + this.leftSeat + ", leftGuestName=" + this.leftGuestName + ", rightSeat=" + this.rightSeat + ", rightGuestName=" + this.rightGuestName + "]";
    }
}