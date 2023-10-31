/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ConditionalElement;
import org.drools.base.rule.Declaration;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.base.time.impl.Timer;
import org.drools.core.time.TimerExpression;
import org.kie.api.runtime.Calendars;

import static org.drools.core.time.TimerExpressionUtil.evalDateExpression;

//import static org.drools.core.time.TimerExpressionUtils.evalDateExpression;

public class CronTimer extends BaseTimer
    implements
        Timer,
    Externalizable {
    private TimerExpression startTime;
    private TimerExpression endTime;
    private int                  repeatLimit;
    private CronExpression       cronExpression;
    
    public CronTimer() {
        
    }

    public CronTimer(TimerExpression startTime,
                     TimerExpression endTime,
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
        this.startTime = (TimerExpression) in.readObject();
        this.endTime = (TimerExpression) in.readObject();
        this.repeatLimit = in.readInt();
        String string = (String) in.readObject();
        try {
            this.cronExpression = new CronExpression( string );
        } catch ( ParseException e ) {
            throw new RuntimeException( "Unable to marshal CronExpression '" + string + "'",
                                        e );
        }
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

    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public Trigger createTrigger(long timestamp,
                                 BaseTuple leftTuple,
                                 JobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 ValueResolver valueResolver) {
        Declaration[] startDeclarations = declrs[0];

        return new CronTrigger( timestamp,
                                evalDateExpression( this.startTime, leftTuple, startDeclarations, valueResolver ),
                                evalDateExpression( this.endTime, leftTuple, startDeclarations, valueResolver ),
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
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        CronTimer other = (CronTimer) obj;
        if ( repeatLimit != other.repeatLimit ) {
            return false;
        }
        if ( cronExpression.getCronExpression() == null ) {
            if ( other.cronExpression.getCronExpression() != null ) {
                return false;
            }
        } else if ( !cronExpression.getCronExpression().equals( other.cronExpression.getCronExpression() ) ) {
            return false;
        }
        if ( endTime == null ) {
            if ( other.endTime != null ) {
                return false;
            }
        } else if ( !endTime.equals( other.endTime ) ) {
            return false;
        }
        if ( startTime == null ) {
            if ( other.startTime != null ) {
                return false;
            }
        } else if ( !startTime.equals( other.startTime ) ) {
            return false;
        }
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new CronTimer(startTime, endTime, repeatLimit, cronExpression) ;
    }

}
