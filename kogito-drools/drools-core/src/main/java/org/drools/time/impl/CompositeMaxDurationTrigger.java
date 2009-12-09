package org.drools.time.impl;

import java.util.Date;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class CompositeMaxDurationTrigger
    implements
    Trigger {
    private Trigger            trigger;
    private Date               timestamp;

    public CompositeMaxDurationTrigger(Date timestamp, // this is the first duration that takes priority
                                       Trigger trigger,
                                       String[] calendarNames,
                                       Calendars calendars) {
        this.timestamp = timestamp;
        this.trigger = trigger;
    }

    public Date hasNextFireTime() {
        if ( this.timestamp != null ) {
            return this.timestamp;
        } else {
            return this.trigger.hasNextFireTime();
        }
    }

    public Date nextFireTime() {
        if ( this.timestamp != null ) {
            Date next = this.timestamp;
            this.timestamp = null;
            return next;
        } else {
            return trigger.nextFireTime();
        }        
    }

}
