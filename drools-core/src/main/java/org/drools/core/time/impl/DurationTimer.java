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

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Trigger;
import org.drools.core.util.NumberUtils;
import org.kie.api.runtime.Calendars;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public class DurationTimer extends BaseTimer
    implements
    Timer,
    Externalizable {

    private long duration;
    private Declaration eventFactHandle;

    public DurationTimer() {

    }

    public DurationTimer(long duration) {
        this.duration = duration;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(duration);
        out.writeObject(eventFactHandle);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        duration = in.readLong();
        eventFactHandle = (Declaration ) in.readObject();
    }

    public long getDuration() {
        return duration;
    }

    public Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs) {
        return new Declaration[][] { new Declaration[] { getEventFactHandleDeclaration()}, null };
    }

    public Trigger createTrigger(Activation item, InternalWorkingMemory wm) {
        long timestamp;
        if (eventFactHandle != null) {
            Tuple leftTuple = item.getTuple();
            EventFactHandle  fh = (EventFactHandle) leftTuple.get(eventFactHandle);
            timestamp = fh.getStartTimestamp();
        } else {
            timestamp = wm.getTimerService().getCurrentTime();
        }
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = wm.getCalendars();
        return createTrigger(timestamp, calendarNames, calendars);
    }

    public Trigger createTrigger(long timestamp,
                                 Tuple leftTuple,
                                 DefaultJobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 InternalWorkingMemory wm) {
        return createTrigger(getEventTimestamp(leftTuple, timestamp), calendarNames, calendars);
    }

    long getEventTimestamp(Tuple leftTuple, long timestamp) {
        return eventFactHandle != null ?
               ((EventFactHandle) leftTuple.get(eventFactHandle)).getStartTimestamp() :
               timestamp;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        long offset = timestamp + duration;
        if( NumberUtils.isAddOverflow( timestamp, duration, offset ) ) {
            // this should not happen, but possible in some odd simulation scenarios, so creating a trigger for immediate execution instead
            return new PointInTimeTrigger( timestamp,
                                           calendarNames,
                                           calendars );
        } else {
            return new PointInTimeTrigger( offset,
                                           calendarNames,
                                           calendars );
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (duration ^ (duration >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DurationTimer other = (DurationTimer) obj;
        if ( duration != other.duration ) return false;
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new DurationTimer( duration );
    }

    @Override
    public String toString() {
        return "DurationTimer: " + duration + "ms";
    }

    public void setEventFactHandle(Declaration eventFactHandle) {
        this.eventFactHandle = eventFactHandle;
    }

    public Declaration getEventFactHandleDeclaration() {
        return eventFactHandle;
    }
}
