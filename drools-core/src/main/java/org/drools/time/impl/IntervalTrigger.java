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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class IntervalTrigger
    implements
    Trigger {
    private Date      startTime;
    private Date      endTime;
    private int       repeatLimit;
    private int       repeatCount;
    private Date      nextFireTime;
    private long      period;
    private String[]  calendarNames;
    private Calendars calendars;

    public IntervalTrigger() {

    }

    public IntervalTrigger(long timestamp,
                           Date startTime,
                           Date endTime,
                           int repeatLimit,
                           long delay,
                           long period,
                           String[] calendarNames,
                           Calendars calendars) {
        this.period = period;

        if ( startTime == null ) {
            startTime = new Date( timestamp );
        }
        setStartTime( startTime );

        if ( endTime != null ) {
            setEndTime( endTime );
        }

        this.repeatLimit = repeatLimit;
        
        this.calendarNames = calendarNames;
        this.calendars = calendars;

        this.nextFireTime = new Date( timestamp + delay );

        setFirstFireTime();

        // Update to next include time, if we have calendars
        updateToNextIncludeDate();
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public long getPeriod() {
        return period;
    }

    public String[] getCalendarNames() {
        return calendarNames;
    }

    public Calendars getCalendars() {
        return calendars;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        if ( startTime == null ) {
            throw new IllegalArgumentException( "Start time cannot be null" );
        }

        Date eTime = getEndTime();
        if ( eTime != null && startTime != null && eTime.before( startTime ) ) {
            throw new IllegalArgumentException( "End time cannot be before start time" );
        }

        // round off millisecond...
        // Note timeZone is not needed here as parameter for
        // Calendar.getInstance(),
        // since time zone is implicit when using a Date in the setTime method.
        Calendar cl = Calendar.getInstance();
        cl.setTime( startTime );
        cl.set( Calendar.MILLISECOND,
                0 );

        this.startTime = cl.getTime();
    }

    /**
     * <p>
     * Get the time at which the <code>CronTrigger</code> should quit
     * repeating - even if repeastCount isn't yet satisfied.
     * </p>
     * 
     * @see #getFinalFireTime()
     */
    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        Date sTime = getStartTime();
        if ( sTime != null && endTime != null && sTime.after( endTime ) ) {
            throw new IllegalArgumentException( "End time cannot be before start time" );
        }

        this.endTime = endTime;
    }

    public void setFirstFireTime() {
        if ( getStartTime().after( this.nextFireTime ) ) {
            this.nextFireTime = new Date( getStartTime().getTime() - 1000l );
        }

        if ( getEndTime() != null && (this.nextFireTime.compareTo( getEndTime() ) >= 0) ) {
            this.nextFireTime = null;
        }

        Date pot = getTimeAfter();
        if ( getEndTime() != null && pot != null && pot.after( getEndTime() ) ) {
            this.nextFireTime = null;
        }
    }

    public Date hasNextFireTime() {
        return nextFireTime;
    }

    public Date nextFireTime() {
        if ( this.nextFireTime == null ) {
            return null;
        }
        Date date = this.nextFireTime;
        // FIXME: this is not safe for serialization
        this.nextFireTime = getTimeAfter();
        updateToNextIncludeDate();
        if ( this.endTime != null && this.nextFireTime.after( this.endTime ) ) {
            this.nextFireTime = null;
        } else if (  repeatLimit != -1 && repeatCount >= repeatLimit ) {
            this.nextFireTime = null;
        }
        return date;
    }

    private Date getTimeAfter() {
        this.repeatCount++;
        
        Date date;
        if ( this.period != 0 ) {
            // repeated fires for the given period
            date = new Date( nextFireTime.getTime() + this.period );
        } else {
            date = null;
        }
        return date;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.nextFireTime = (Date) in.readObject();
        this.period = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.nextFireTime );
        out.writeLong( this.period );
    }

    public void updateToNextIncludeDate() {
        if ( this.calendars == null || calendarNames == null || calendarNames.length == 0 ) {
            // There are no assigned calendars
            return;
        }

        // If we have calendars, check we can fire, or get next time until we can fire.
        while ( this.nextFireTime != null && (this.endTime == null || this.nextFireTime.before( this.endTime )) ) {
            // this will loop forever if the trigger repeats forever and
            // included calendar position cannot be found
            boolean included = true;
            for ( String calName : this.calendarNames ) {
                // all calendars must not block, as soon as one blocks break
                org.drools.time.Calendar cal = this.calendars.get( calName );
                if ( cal != null && !cal.isTimeIncluded( this.nextFireTime.getTime() ) ) {
                    included = false;
                    break;
                }
            }
            if ( included == true ) {
                // if no calendars blocked, break
                break;
            } else {
                // otherwise increase the time and try again
                this.nextFireTime = getTimeAfter();
            }
        }
    }

    public void setRepeatLimit(int repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void setCalendarNames(String[] calendarNames) {
        this.calendarNames = calendarNames;
    }

    public void setCalendars(Calendars calendars) {
        this.calendars = calendars;
    }

    @Override
    public String toString() {
        return "IntervalTrigger [startTime=" + startTime + ", endTime=" + endTime + ", repeatLimit=" + repeatLimit + ", repeatCount=" + repeatCount + ", nextFireTime=" + nextFireTime + ", period=" + period + ", calendarNames=" + Arrays.toString( calendarNames ) + ", calendars=" + calendars + "]";
    }
    
    
    
}
