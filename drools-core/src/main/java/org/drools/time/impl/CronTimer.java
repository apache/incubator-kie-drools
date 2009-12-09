package org.drools.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.Date;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class CronTimer
    implements
    Timer,
    Externalizable {
    private Date           startTime;
    private Date           endTime;
    private CronExpression cronExpression;
    
    public CronTimer() {
        
    }

    public CronTimer(Date startTime,
                     Date endTime,
                     CronExpression cronExpression) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.cronExpression = cronExpression;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeObject( cronExpression.getCronExpression() );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (Date) in.readObject();
        this.endTime = (Date) in.readObject();
        String string = (String) in.readObject();
        try {
            this.cronExpression = new CronExpression( string );
        } catch ( ParseException e ) {
            throw new RuntimeException( "Unable to marshal CronExpression '" + string + "'",
                                        e );
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new CronTrigger( timestamp,
                                this.startTime,
                                this.endTime,
                                this.cronExpression,
                                calendarNames,
                                calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cronExpression.getCronExpression() == null) ? 0 : cronExpression.getCronExpression().hashCode());
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CronTimer other = (CronTimer) obj;
        if ( cronExpression.getCronExpression() == null ) {
            if ( other.cronExpression.getCronExpression() != null ) return false;
        } else if ( !cronExpression.getCronExpression().equals( other.cronExpression.getCronExpression() ) ) return false;
        if ( endTime == null ) {
            if ( other.endTime != null ) return false;
        } else if ( !endTime.equals( other.endTime ) ) return false;
        if ( startTime == null ) {
            if ( other.startTime != null ) return false;
        } else if ( !startTime.equals( other.startTime ) ) return false;
        return true;
    }

}
