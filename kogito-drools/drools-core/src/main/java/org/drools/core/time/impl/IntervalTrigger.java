/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.time.impl;

import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IntervalTrigger
    implements
    Trigger {
    private Date      startTime;
    private Date      endTime;
    private int       repeatLimit;
    private int       repeatCount;
    private Date      nextFireTime;
    private Date      lastFireTime;
    private Date      createdTime;
    private long      delay;
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
        this(timestamp, startTime, endTime, repeatLimit, delay, period, calendarNames, calendars, null, null);
    }

    public IntervalTrigger(long timestamp,
                           Date startTime,
                           Date endTime,
                           int repeatLimit,
                           long delay,
                           long period,
                           String[] calendarNames,
                           Calendars calendars,
                           Date createdTime,
                           Date lastFireTime) {
        this.delay = delay;
        this.period = period;
        this.createdTime = createdTime == null ? new Date(timestamp) : createdTime;
        this.lastFireTime = lastFireTime;

        if ( startTime == null ) {
            this.nextFireTime = new Date( timestamp + delay );
            startTime = new Date( timestamp );
        }
        setStartTime( startTime );

        if ( endTime != null ) {
            setEndTime( endTime );
        }

        this.repeatLimit = repeatLimit;
        
        this.calendarNames = calendarNames;
        this.calendars = calendars;

        setFirstFireTime(timestamp);

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
        if ( eTime != null && eTime.before( startTime ) ) {
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

    public Date getLastFireTime() {
        return lastFireTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    private void setFirstFireTime(long timestamp) {
        if ( this.nextFireTime == null ) {
            long start = this.startTime.getTime() + delay;
            if ( timestamp > start ) {
                long distanceFromLastPhase = ( timestamp - start ) % period;
                if ( distanceFromLastPhase == 0) {
                    this.nextFireTime = new Date( timestamp );
                } else {
                    long phase = period - distanceFromLastPhase;
                    this.nextFireTime = new Date( timestamp + phase );
                }
            } else {
                this.nextFireTime = new Date( start );
            }
        }

        if ( getEndTime() != null && this.nextFireTime.after( getEndTime() ) ) {
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

    public synchronized Date nextFireTime() {
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
        lastFireTime = date;
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
        this.delay = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.nextFireTime );
        out.writeLong( this.period );
        out.writeLong( this.delay );
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
                org.kie.api.time.Calendar cal = this.calendars.get( calName );
                if ( cal != null && !cal.isTimeIncluded( this.nextFireTime.getTime() ) ) {
                    included = false;
                    break;
                }
            }
            if ( included ) {
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
        return "IntervalTrigger [startTime=" + startTime + ", endTime=" + endTime + ", repeatLimit=" + repeatLimit + ", repeatCount=" + repeatCount + ", nextFireTime=" + nextFireTime + ", delay=" + delay + ", period=" + period + ", calendarNames=" + Arrays.toString( calendarNames ) + ", calendars=" + calendars + "]";
    }
    
    
    
}
