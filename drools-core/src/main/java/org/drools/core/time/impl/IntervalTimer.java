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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Map;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Tuple;
import org.drools.core.time.TimerExpression;
import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

import static org.drools.core.time.TimeUtils.evalDateExpression;

public class IntervalTimer extends BaseTimer
    implements
    Timer,
    Externalizable {
    private TimerExpression startTime;
    private TimerExpression endTime;
    private int  repeatLimit;
    private long delay;
    private long period;
    
    public IntervalTimer() {
        
    }

    public IntervalTimer(TimerExpression startTime,
                         TimerExpression endTime,
                         int repeatLimit,
                         long delay,
                         long period) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatLimit = repeatLimit;
        this.delay = delay;
        this.period = period;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeInt( repeatLimit );
        out.writeLong( delay );
        out.writeLong( period );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (TimerExpression) in.readObject();
        this.endTime = (TimerExpression) in.readObject();
        this.repeatLimit = in.readInt();
        this.delay = in.readLong();
        this.period = in.readLong();
    }

    private Declaration[] getStartDeclarations() {
        return this.startTime != null ? this.startTime.getDeclarations() : null;
    }

    private Declaration[] getEndDeclarations() {
        return this.endTime != null ? this.endTime.getDeclarations() : null;
    }

    public Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs) {
        return new Declaration[][] { sortDeclarations(outerDeclrs, getStartDeclarations()),
                                     sortDeclarations(outerDeclrs, getEndDeclarations()) };
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public Trigger createTrigger(long timestamp,
                                 Tuple leftTuple,
                                 DefaultJobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 InternalWorkingMemory wm) {
        Declaration[] startDeclarations = declrs[0];

        Date lastFireTime = null;
        Date createdTime = null;
        long newDelay = delay;

        if ( jh != null ) {
            IntervalTrigger preTrig = (IntervalTrigger) jh.getTimerJobInstance().getTrigger();
            lastFireTime = preTrig.getLastFireTime();
            createdTime = preTrig.getCreatedTime();
            if (lastFireTime != null) {
                // it is already fired calculate the new delay using the period instead of the delay
                newDelay = period - timestamp + lastFireTime.getTime();
            } else {
                newDelay = delay - timestamp + createdTime.getTime();
            }
        }

        if (newDelay < 0) {
            newDelay = 0;
        }

        return new IntervalTrigger( timestamp,
                                    evalDateExpression( this.startTime, leftTuple, startDeclarations, wm ),
                                    evalDateExpression( this.endTime, leftTuple, startDeclarations, wm ),
                                    this.repeatLimit,
                                    newDelay,
                                    this.period,
                                    calendarNames,
                                    calendars,
                                    createdTime,
                                    lastFireTime );
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new IntervalTrigger( timestamp,
                                    null, // this.startTime,
                                    null, // this.endTime,
                                    this.repeatLimit,
                                    this.delay,
                                    this.period,
                                    calendarNames,
                                    calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + repeatLimit;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        IntervalTimer other = (IntervalTimer) obj;
        if ( delay != other.delay ) return false;
        if ( repeatLimit != other.repeatLimit ) return false;
        if ( endTime == null ) {
            if ( other.endTime != null ) return false;
        } else if ( !endTime.equals( other.endTime ) ) return false;
        if ( period != other.period ) return false;
        if ( startTime == null ) {
            if ( other.startTime != null ) return false;
        } else if ( !startTime.equals( other.startTime ) ) return false;
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new IntervalTimer(startTime,
                                 endTime,
                                 repeatLimit,
                                 delay,
                                 period);
    }
}
