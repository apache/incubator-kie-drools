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

package org.drools.core.time.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

public class CronTrigger
    implements
    Trigger {

    protected static final int YEAR_TO_GIVEUP_SCHEDULING_AT = 2299;

    private CronExpression     cronEx                       = null;
    private Date               startTime                    = null;
    private Date               endTime                      = null;
    private int                repeatLimit;
    private int                repeatCount;
    private Date               nextFireTime                 = null;
    private Date               previousFireTime             = null;
    private transient TimeZone timeZone                     = null;
    private String[]           calendarNames;
    private Calendars          calendars;

    public CronTrigger() {
        
    }
    
    public CronTrigger(long timestamp,
                       Date startTime,
                       Date endTime,
                       int repeatLimit,
                       String cronExpression,
                       String[] calendarNames,
                       Calendars calendars) {
        this( timestamp,
              startTime,
              endTime,
              repeatLimit,
              determineCronExpression( cronExpression ),
              calendarNames,
              calendars );
    }

    public CronTrigger(long timestamp,
                       Date startTime,
                       Date endTime,
                       int repeatLimit,
                       CronExpression cronExpression,
                       String[] calendarNames,
                       Calendars calendars) {
        setCronExpression( cronExpression );
        
        this.repeatLimit = repeatLimit;

        if ( startTime == null ) {
            startTime = new Date( timestamp );
        }
        setStartTime( startTime );

        if ( endTime != null ) {
            setEndTime( endTime );
        }
        setTimeZone( TimeZone.getDefault() );

        // Set the first FireTime, this is sensitive to StartTime
        this.nextFireTime = new Date( timestamp );
        setFirstFireTimeAfter();

        this.calendarNames = calendarNames;
        this.calendars = calendars;

        // Update to next include time, if we have calendars
        updateToNextIncludeDate();
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

    /**
     * <p>
     * Returns the next time at which the <code>Trigger</code> is scheduled to fire. If
     * the trigger will not fire again, <code>null</code> will be returned.  Note that
     * the time returned can possibly be in the past, if the time that was computed
     * for the trigger to next fire has already arrived, but the scheduler has not yet
     * been able to fire the trigger (which would likely be due to lack of resources
     * e.g. threads).
     * </p>
     *
     * <p>The value returned is not guaranteed to be valid until after the <code>Trigger</code>
     * has been added to the scheduler.
     * </p>
     *
     * @see TriggerUtils#computeFireTimesBetween(Trigger, org.quartz.Calendar , Date, Date)
     */
    public Date getNextFireTime() {
        return this.nextFireTime;
    }

    /**
     * <p>
     * Returns the previous time at which the <code>CronTrigger</code> 
     * fired. If the trigger has not yet fired, <code>null</code> will be
     * returned.
     */
    public Date getPreviousFireTime() {
        return this.previousFireTime;
    }

    /**
     * <p>
     * Sets the next time at which the <code>CronTrigger</code> will fire.
     * <b>This method should not be invoked by client code.</b>
     * </p>
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * <p>
     * Set the previous time at which the <code>CronTrigger</code> fired.
     * </p>
     * 
     * <p>
     * <b>This method should not be invoked by client code.</b>
     * </p>
     */
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    /**
     * <p>
     * Returns the time zone for which the <code>cronExpression</code> of
     * this <code>CronTrigger</code> will be resolved.
     * </p>
     */
    public TimeZone getTimeZone() {
        if ( this.cronEx != null ) {
            return this.cronEx.getTimeZone();
        }

        if ( this.timeZone == null ) {
            this.timeZone = TimeZone.getDefault();
        }
        return this.timeZone;
    }

    /**
     * <p>
     * Sets the time zone for which the <code>cronExpression</code> of this
     * <code>CronTrigger</code> will be resolved.
     * </p>
     * 
     * <p>If {@link #setCronExpression(CronExpression)} is called after this
     * method, the TimeZon setting on the CronExpression will "win".  However
     * if {@link #setCronExpression(String)} is called after this method, the
     * time zone applied by this method will remain in effect, since the 
     * String cron expression does not carry a time zone!
     */
    public void setTimeZone(TimeZone timeZone) {
        if ( this.cronEx != null ) {
            this.cronEx.setTimeZone( timeZone );
        }
        this.timeZone = timeZone;
    }

    public void setCronExpression(String cronExpression) {
        setCronExpression( determineCronExpression( cronExpression ) );
    }

    public void setCronExpression(CronExpression cronExpression) {
        TimeZone origTz = getTimeZone();
        this.cronEx = cronExpression;
        this.cronEx.setTimeZone( origTz );
    }
    
    

    public CronExpression getCronEx() {
        return cronEx;
    }

    public void setCronEx(CronExpression cronEx) {
        this.cronEx = cronEx;
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(int repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String[] getCalendarNames() {
        return calendarNames;
    }

    public void setCalendarNames(String[] calendarNames) {
        this.calendarNames = calendarNames;
    }

    public Calendars getCalendars() {
        return calendars;
    }

    public void setCalendars(Calendars calendars) {
        this.calendars = calendars;
    }

    public static CronExpression determineCronExpression(String cronExpression) {
        try {
            return new CronExpression( cronExpression );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to parse cron expression '" + cronExpression + "'",
                                        e );
        }
    }

    public Date hasNextFireTime() {
        return this.nextFireTime;
    }

    public synchronized Date nextFireTime() {
        if ( this.nextFireTime == null ) {
            return null;
        }        
        Date date = this.nextFireTime;
        this.nextFireTime = getTimeAfter( this.nextFireTime );
        updateToNextIncludeDate();
        if ( this.endTime != null && this.nextFireTime.after( this.endTime ) ) {
            this.nextFireTime = null;
        } else if (  repeatLimit != -1 && repeatCount >= repeatLimit ) {
            this.nextFireTime = null;
        }
        return date;
    }

    /**
     * <p>
     * Returns the next time at which the <code>CronTrigger</code> will fire,
     * after the given time. If the trigger will not fire after the given time,
     * <code>null</code> will be returned.
     * </p>
     * 
     * <p>
     * Note that the date returned is NOT validated against the related
     * org.quartz.Calendar (if any)
     * </p>
     */
    public void setFirstFireTimeAfter() {
        if ( getStartTime().after( this.nextFireTime ) ) {
            this.nextFireTime = new Date( getStartTime().getTime() - 1000l );
        }

        if ( getEndTime() != null && (this.nextFireTime.compareTo( getEndTime() ) >= 0) ) {
            this.nextFireTime = null;
        }

        Date pot = getTimeAfter( this.nextFireTime );
        if ( getEndTime() != null && pot != null && pot.after( getEndTime() ) ) {
            this.nextFireTime = null;
        } else {
            this.nextFireTime = pot;
        }
    }

    protected Date getTimeAfter(Date afterTime) {
        this.repeatCount++;
        return (this.cronEx == null) ? null : this.cronEx.getTimeAfter( afterTime );
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
                // all calendars must not block, as soon as one blocks break, so we can check next time slot
                org.kie.api.time.Calendar cal = this.calendars.get( calName );
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
                this.nextFireTime = getTimeAfter( this.nextFireTime );
            }
        }
    }
    
    

}
