/*
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
import java.text.ParseException;
import java.util.Date;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.runtime.Calendars;
import org.drools.spi.Activation;
import org.drools.time.Trigger;

public class CronTimer
    implements
    Timer,
    Externalizable {
    private Date           startTime;
    private Date           endTime;
    private int            repeatLimit;
    private CronExpression cronExpression;
    
    public CronTimer() {
        
    }

    public CronTimer(Date startTime,
                     Date endTime,
                     int repeatLimit,
                     CronExpression cronExpression) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatLimit = repeatLimit;
        this.cronExpression = cronExpression;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeInt( repeatLimit );
        out.writeObject( cronExpression.getCronExpression() );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (Date) in.readObject();
        this.endTime = (Date) in.readObject();
        this.repeatLimit = in.readInt();
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


    public Trigger createTrigger( Activation item, WorkingMemory wm ) {
        long timestamp = ((InternalWorkingMemory) wm).getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = ((InternalWorkingMemory) wm).getCalendars();
        return createTrigger( timestamp, calendarNames, calendars );
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new CronTrigger( timestamp,
                                this.startTime,
                                this.endTime,
                                this.repeatLimit,
                                this.cronExpression,
                                calendarNames,
                                calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cronExpression.getCronExpression() == null) ? 0 : cronExpression.getCronExpression().hashCode());
        result = prime * result + repeatLimit;
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
        if ( repeatLimit != other.repeatLimit ) return false;
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
