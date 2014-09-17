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

import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

public class CompositeMaxDurationTrigger
    implements
    Trigger, Externalizable {
    private Trigger            timerTrigger;
    private Date               maxDurationTimestamp;
    private Date               timerCurrentDate;

    public CompositeMaxDurationTrigger() { }

    public CompositeMaxDurationTrigger(Date maxDurationTimestamp, // this max duration of when rules are allowed to fire (cep rules like 'not')
                                       Trigger timerTrigger, // trigger of when a rule should try to fire, should not execute before maxDurationTimestamp
                                       String[] calendarNames,
                                       Calendars calendars) {
        this.maxDurationTimestamp = maxDurationTimestamp;
        this.timerTrigger = timerTrigger;
        
        if ( this.timerTrigger != null ) {
            // if there is a timerTrigger, make sure it's scheduler AFTER the current max duration.
            while ( this.timerTrigger.hasNextFireTime() != null && this.timerTrigger.hasNextFireTime().getTime() <= this.maxDurationTimestamp.getTime() ) {
                this.timerTrigger.nextFireTime(); 
            }
        }
    }

    public Date hasNextFireTime() {
        if (  this.maxDurationTimestamp != null ) {
            return this.maxDurationTimestamp;
        } else if ( this.timerTrigger != null ){
            return this.timerTrigger.hasNextFireTime();
        } else {
            return null;
        }
    }

    public Date nextFireTime() { 
        if (  this.maxDurationTimestamp != null ) {
            this.maxDurationTimestamp = null;
        }
        if ( this.timerTrigger != null ) {
            return this.timerTrigger.nextFireTime();
        } else {
            return null;
        }
    }

    public Date getTimerCurrentDate() {
        return timerCurrentDate;
    }

    public void setTimerCurrentDate(Date timerCurrentDate) {
        this.timerCurrentDate = timerCurrentDate;
    }

    public Trigger getTimerTrigger() {
        return timerTrigger;
    }

    public void setTimerTrigger(Trigger timerTrigger) {
        this.timerTrigger = timerTrigger;
    }

    public Date getMaxDurationTimestamp() {
        return maxDurationTimestamp;
    }

    public void setMaxDurationTimestamp(Date maxDurationTimestamp) {
        this.maxDurationTimestamp = maxDurationTimestamp;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( maxDurationTimestamp );
        out.writeObject( timerCurrentDate );
        out.writeObject( timerTrigger );
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        maxDurationTimestamp = ( Date ) in.readObject();
        timerCurrentDate = ( Date ) in.readObject();
        timerTrigger = ( Trigger )in.readObject();
    }
}
