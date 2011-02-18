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
