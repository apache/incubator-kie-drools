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

import org.drools.core.base.mvel.MVELObjectExpression;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ScheduledAgendaItem;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.Map;

import static org.drools.core.time.TimeUtils.evalDateExpression;

public class CronTimer extends BaseTimer
    implements
    Timer,
    Externalizable {
    private MVELObjectExpression startTime;
    private MVELObjectExpression endTime;
    private int                  repeatLimit;
    private CronExpression       cronExpression;
    
    public CronTimer() {
        
    }

    public CronTimer(MVELObjectExpression startTime,
                     MVELObjectExpression endTime,
                     int repeatLimit,
                     CronExpression cronExpression) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatLimit = repeatLimit;
        this.cronExpression = cronExpression;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeInt( repeatLimit );
        out.writeObject( cronExpression.getCronExpression() );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (MVELObjectExpression) in.readObject();
        this.endTime = (MVELObjectExpression) in.readObject();
        this.repeatLimit = in.readInt();
        String string = (String) in.readObject();
        try {
            this.cronExpression = new CronExpression( string );
        } catch ( ParseException e ) {
            throw new RuntimeException( "Unable to marshal CronExpression '" + string + "'",
                                        e );
        }
    }

    public Declaration[] getStartDeclarations() {
        return this.startTime != null ? this.startTime.getMVELCompilationUnit().getPreviousDeclarations() : null;
    }

    public Declaration[] getEndDeclarations() {
        return this.endTime != null ? this.endTime.getMVELCompilationUnit().getPreviousDeclarations() : null;
    }

    public Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs) {
        return new Declaration[][] { sortDeclarations(outerDeclrs, getStartDeclarations()),
                                     sortDeclarations(outerDeclrs, getEndDeclarations()) };
    }

    public CronExpression getCronExpression() {
        return cronExpression;
    }


    public Trigger createTrigger( Activation item, InternalWorkingMemory wm ) {
        long timestamp = wm.getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = wm.getCalendars();

        Declaration[][] timerDeclrs = ((AgendaItem)item).getTerminalNode().getTimerDeclarations();

        ScheduledAgendaItem schItem = ( ScheduledAgendaItem ) item;
        DefaultJobHandle jh = null;
        if ( schItem.getJobHandle() != null ) {
            jh = ( DefaultJobHandle) schItem.getJobHandle();
        }

        return createTrigger( timestamp, item.getTuple(), jh, calendarNames, calendars, timerDeclrs, wm );
    }

    public Trigger createTrigger(long timestamp,
                                 Tuple leftTuple,
                                 DefaultJobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 InternalWorkingMemory wm) {
        Declaration[] startDeclarations = declrs[0];

        return new CronTrigger( timestamp,
                                evalDateExpression( this.startTime, leftTuple, startDeclarations, wm ),
                                evalDateExpression( this.endTime, leftTuple, startDeclarations, wm ),
                                this.repeatLimit,
                                this.cronExpression,
                                calendarNames,
                                calendars );
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new CronTrigger( timestamp,
                                null, // this.startTime,
                                null, // this.endTime,
                                this.repeatLimit,
                                this.cronExpression,
                                calendarNames,
                                calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cronExpression.getCronExpression() == null) ? 0 : cronExpression.getCronExpression().hashCode());
        result = prime * result + repeatLimit;
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CronTimer other = (CronTimer) obj;
        if ( repeatLimit != other.repeatLimit ) return false;
        if ( cronExpression.getCronExpression() == null ) {
            if ( other.cronExpression.getCronExpression() != null ) return false;
        } else if ( !cronExpression.getCronExpression().equals( other.cronExpression.getCronExpression() ) ) return false;
        if ( endTime == null ) {
            if ( other.endTime != null ) return false;
        } else if ( !endTime.equals( other.endTime ) ) return false;
        if ( startTime == null ) {
            if ( other.startTime != null ) return false;
        } else if ( !startTime.equals( other.startTime ) ) return false;
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new CronTimer(startTime, endTime, repeatLimit, cronExpression) ;
    }

}
