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

package org.drools.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class DurationTimer
    implements
    Timer,
    Externalizable {
    private long duration;
    
    public DurationTimer() {
        
    }
    
    public DurationTimer(long duration) {
        this.duration = duration;
    }    

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( duration );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        duration = in.readLong();
    }

    public long getDuration() {
        return duration;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new PointInTimeTrigger( timestamp + duration,
                                       calendarNames,
                                       calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (duration ^ (duration >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DurationTimer other = (DurationTimer) obj;
        if ( duration != other.duration ) return false;
        return true;
    }

}
