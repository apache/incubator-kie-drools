package org.drools.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class IntervalTimer
    implements
    Timer,
    Externalizable {
    private Date startTime;
    private Date endTime;
    private long delay;
    private long period;
    
    public IntervalTimer() {
        
    }

    public IntervalTimer(Date startTime,
                         Date endTime,
                         long delay,
                         long period) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.delay = delay;
        this.period = period;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeLong( delay );
        out.writeLong( period );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (Date) in.readObject();
        this.endTime = (Date) in.readObject();
        this.delay = in.readLong();
        this.period = in.readLong();
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new IntervalTrigger( timestamp,
                                    delay,
                                    period,
                                    calendarNames,
                                    calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        IntervalTimer other = (IntervalTimer) obj;
        if ( delay != other.delay ) return false;
        if ( endTime == null ) {
            if ( other.endTime != null ) return false;
        } else if ( !endTime.equals( other.endTime ) ) return false;
        if ( period != other.period ) return false;
        if ( startTime == null ) {
            if ( other.startTime != null ) return false;
        } else if ( !startTime.equals( other.startTime ) ) return false;
        return true;
    }
}
