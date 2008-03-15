/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Oct 17, 2007
 */
package org.drools.temporal;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * A SessionPseudoClock is a clock that allows the user to explicitly 
 * control current time.
 * 
 * @author etirelli
 *
 */
public class SessionPseudoClock
    implements
    SessionClock {

    private long timer;

    public SessionPseudoClock() {
        this.timer = 0;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        timer   = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(timer);
    }

    /* (non-Javadoc)
    * @see org.drools.temporal.SessionClock#getCurrentTime()
    */
    public long getCurrentTime() {
        return this.timer;
    }

    public long advanceTime( long millisecs ) {
        this.timer += millisecs;
        return this.timer;
    }

    public void setStartupTime(int i) {
        this.timer = i;
    }

}
