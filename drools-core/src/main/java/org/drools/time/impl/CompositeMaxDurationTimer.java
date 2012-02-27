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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.runtime.Calendars;
import org.drools.spi.Activation;
import org.drools.time.Trigger;

/**
 * While a rule might have multiple DurationTimers, due to LHS CEP rules, there can only ever
 * be one timer attribute. Duration rules should be considered a priority over the one timer rule.
 * So the Timer cannot fire, until the maximum duration has passed.
 */
public class CompositeMaxDurationTimer
    implements
    Timer {

    private static final long   serialVersionUID = -2531364489959820962L;

    private List<DurationTimer> durations;

    private Timer               timer;

    public CompositeMaxDurationTimer() {

    }

    public void addDurationTimer( final DurationTimer durationTimer ) {
        if ( this.durations == null ) {
            this.durations = new LinkedList<DurationTimer>();
        }
        this.durations.add( durationTimer );
    }

    public void setTimer( Timer timer ) {
        this.timer = timer;
    }


    public Trigger createTrigger( Activation item, WorkingMemory wm ) {
        long timestamp = ((InternalWorkingMemory) wm).getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = ((InternalWorkingMemory) wm).getCalendars();
        return createTrigger( timestamp, calendarNames, calendars );
    }

    public Trigger createTrigger( long timestamp, // current time
                                  String[] calendarNames,
                                  Calendars calendars ) {
        if ( this.durations == null ) {
            throw new IllegalStateException( "CompositeMaxDurationTimer cannot have no durations" );
        }
        
        Date maxDurationDate = new Date( getMaxDuration() + timestamp );
        
        return new CompositeMaxDurationTrigger( maxDurationDate,
                                                timer != null ? timer.createTrigger( timestamp,
                                                                                     calendarNames,
                                                                                     calendars ) : null,
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
