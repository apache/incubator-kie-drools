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

import org.drools.runtime.Calendars;
import org.drools.time.Trigger;

public class CompositeMaxDurationTrigger
    implements
    Trigger {
    private Trigger            timerTrigger;
    private Date               maxDurationTimestamp;
    private Date               timerCurrentDate;

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

}
