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

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELObjectExpression;
import org.drools.common.AgendaItem;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.ScheduledAgendaItem;
import org.drools.runtime.Calendars;
import org.drools.spi.Activation;
import org.drools.time.TimeUtils;
import org.drools.time.Trigger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

public class ExpressionIntervalTimer
    implements
    Timer,
    Externalizable {

    private Date startTime;

    private Date endTime;

    private int  repeatLimit;

    private MVELObjectExpression delay;
    private MVELObjectExpression period;

    public ExpressionIntervalTimer() {

    }



    public ExpressionIntervalTimer(Date startTime,
                                   Date endTime,
                                   int repeatLimit,
                                   MVELObjectExpression delay,
                                   MVELObjectExpression period) {
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
        out.writeObject( delay );
        out.writeObject( period );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (Date) in.readObject();
        this.endTime = (Date) in.readObject();
        this.repeatLimit = in.readInt();
        this.delay = (MVELObjectExpression) in.readObject();
        this.period = (MVELObjectExpression) in.readObject();
    }
    
    public MVELCompilationUnit getDelayMVELCompilationUnit() {
        return this.delay.getMVELCompilationUnit();
    }  
    
    public MVELCompilationUnit getPeriodMVELCompilationUnit() {
        return this.period.getMVELCompilationUnit();
    }       

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MVELObjectExpression getDelay() {
        return delay;
    }

    public MVELObjectExpression getPeriod() {
        return period;
    }


    public Trigger createTrigger( Activation item, WorkingMemory wm ) {

        long timestamp = ((InternalWorkingMemory) wm).getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = ((InternalWorkingMemory) wm).getCalendars();
        
        long timeSinceLastFire = 0;
        ScheduledAgendaItem schItem = ( ScheduledAgendaItem ) item;
        if ( schItem.getJobHandle() != null ) {
            DefaultJobHandle jh = ( DefaultJobHandle) schItem.getJobHandle();
            IntervalTrigger preTrig = ( IntervalTrigger ) jh.getTimerJobInstance().getTrigger();
            if ( preTrig.hasNextFireTime() != null ) {
                timeSinceLastFire = timestamp - preTrig.getLastFireTime().getTime();
            }
        }
        
        long newDelay = (delay  != null ? evalDelay( item, wm ) : 0) - timeSinceLastFire;
        if ( newDelay < 0 ) {
            newDelay = 0;
        }        

        return new IntervalTrigger( timestamp,
                                    this.startTime,
                                    this.endTime,
                                    this.repeatLimit,
                                    newDelay,
                                    period != null ? evalPeriod( item, wm ) : 0,
                                    calendarNames,
                                    calendars );
    }

    private long evalPeriod( Activation item, WorkingMemory wm ) {
        Object p = this.period.getValue( item, ((AgendaItem)item).getRuleTerminalNode().getTimerPeriodDeclarations(),
                                         item.getRule(), wm );
        if ( p instanceof Number ) {
            return ((Number) p).longValue();
        } else {
            return TimeUtils.parseTimeString( p.toString() );
        }
    }

    private long evalDelay(Activation item, WorkingMemory wm) {
        Object d = this.delay.getValue( item,  ((AgendaItem)item).getRuleTerminalNode().getTimerDelayDeclarations(), 
                                        item.getRule(), wm );
        if ( d instanceof Number ) {
            return ((Number) d).longValue();
        } else {
            return TimeUtils.parseTimeString( d.toString() );
        }
    }


    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new IntervalTrigger( timestamp,
                                    this.startTime,
                                    this.endTime,
                                    this.repeatLimit,
                                    0,
                                    0,
                                    calendarNames,
                                    calendars );
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + delay.hashCode();
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + period.hashCode();
        result = prime * result + repeatLimit;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ExpressionIntervalTimer other = (ExpressionIntervalTimer) obj;
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
}
