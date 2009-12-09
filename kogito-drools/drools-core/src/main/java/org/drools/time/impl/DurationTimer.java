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
