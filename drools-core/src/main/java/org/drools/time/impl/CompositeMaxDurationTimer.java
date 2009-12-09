package org.drools.time.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

/**
 * While a rule might have multiple DurationTimers, due to LHS CEP rules, there can only ever
 * be one timer attribute. Duration rules should be considered a priority over the one timer rule.
 * So the Timer cannot fire, until the maximum duration has passed.
 *
 */
public class CompositeMaxDurationTimer
    implements
    Timer {

    private List<DurationTimer> durations;

    private Timer               timer;

    public CompositeMaxDurationTimer() {

    }

    public void addDurationTimer(final DurationTimer durationTimer) {
        if ( this.durations == null ) {
            this.durations = new LinkedList<DurationTimer>();
        }
        this.durations.add( durationTimer );
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new CompositeMaxDurationTrigger( new Date( getMaxDuration() + timestamp ),
                                                timer.createTrigger( timestamp,
                                                                     calendarNames,
                                                                     calendars ),
                                                calendarNames,
                                                calendars );
    }

    private long getMaxDuration() {
        long result = 0;
        for ( DurationTimer durationTimer : durations ) {
            result = Math.max( result,
                               durationTimer.getDuration() );
        }
        return result;
    }
}
