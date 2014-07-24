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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * While a rule might have multiple DurationTimers, due to LHS CEP rules, there can only ever
 * be one timer attribute. Duration rules should be considered a priority over the one timer rule.
 * So the Timer cannot fire, until the maximum duration has passed.
 */
public class CompositeMaxDurationTimer extends BaseTimer
    implements
    Timer {

    private static final long   serialVersionUID = -2531364489959820962L;

    private List<DurationTimer> durations;

    private Timer               timer;

    public CompositeMaxDurationTimer() {

    }

    public Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs) {
        return null;
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


    public Trigger createTrigger( Activation item, InternalWorkingMemory wm ) {
        long timestamp = ((InternalWorkingMemory) wm).getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = ((InternalWorkingMemory) wm).getCalendars();
        return createTrigger( getMaxTimestamp(item.getTuple(), timestamp), calendarNames, calendars );
    }

    public Trigger createTrigger(long timestamp,
                                 LeftTuple leftTuple,
                                 DefaultJobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 InternalWorkingMemory wm) {
        return createTrigger( getMaxTimestamp(leftTuple, timestamp), calendarNames, calendars );
    }

    public Trigger createTrigger( long timestamp, // current time
                                  String[] calendarNames,
                                  Calendars calendars ) {
        if ( this.durations == null ) {
            throw new IllegalStateException( "CompositeMaxDurationTimer cannot have no durations" );
        }
        
        Date maxDurationDate = new Date( timer != null ? getMaxDuration() + timestamp : timestamp );
        
        return new CompositeMaxDurationTrigger( maxDurationDate,
                                                timer != null ? timer.createTrigger( timestamp,
                                                                                     calendarNames,
                                                                                     calendars ) : null,
                                                calendarNames,
                                                calendars );
    }

    private long getMaxTimestamp(LeftTuple leftTuple, long timestamp) {
        if (timer != null) {
            return timestamp;
        }
        long result = 0;
        for ( DurationTimer durationTimer : durations ) {
            result = Math.max( result,
                               durationTimer.getDuration() + durationTimer.getEventTimestamp(leftTuple, timestamp) );
        }
        return result;
    }

    private long getMaxDuration() {
        long result = 0;
        for ( DurationTimer durationTimer : durations ) {
            result = Math.max( result,
                               durationTimer.getDuration() );
        }
        return result;
    }

    @Override
    public ConditionalElement clone() {
        CompositeMaxDurationTimer clone = new CompositeMaxDurationTimer();
        if ( durations != null && !durations.isEmpty() ) {
            for ( DurationTimer timer : durations ) {
                clone.addDurationTimer(timer);
            }
        }

        if ( timer != null) {
            clone.timer = timer;
        }
        return clone;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( durations );
        out.writeObject( timer );
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        durations = ( List<DurationTimer>  ) in.readObject();
        timer = ( Timer )in.readObject();
    }
}
