/**
 * 
 */
package org.drools.time.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class IntervalTrigger
    implements
    Trigger {
    private Date nextFireTime;
    private long period;
    private String[] calendarNames;
    private Calendars calendars;    

    public IntervalTrigger() {

    }

    public IntervalTrigger(long currentTS,
                           long delay,
                           long period,
                           String[] calendarNames,
                           Calendars calendars) {
        this.nextFireTime = new Date( currentTS + delay );
        this.period = period;
        
        this.calendarNames = calendarNames;
        this.calendars = calendars;
        
        
        // Update to next include time, if we have calendars
        updateToNextIncludeDate( );         
    }

    public Date hasNextFireTime() {
        return nextFireTime;
    }

    public Date nextFireTime() {
        Date date = nextFireTime;
        // FIXME: this is not safe for serialization
        this.nextFireTime = getTimeAfter();
        updateToNextIncludeDate();
        return date;
    }
    
    private Date getTimeAfter() {
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
        if ( calendarNames == null || calendarNames.length == 0 ) {
            // There are no assigned calendars
            return;
        }

        // If we have calendars, check we can fire, or get next time until we can fire.
        while ( this.nextFireTime != null ) {
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
                this.nextFireTime = getTimeAfter( );
            }
        }
    }       

}