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
    private Trigger            trigger;
    private Date               timestamp;

    public CompositeMaxDurationTrigger(Date timestamp, // this is the first duration that takes priority
                                       Trigger trigger,
                                       String[] calendarNames,
                                       Calendars calendars) {
        this.timestamp = timestamp;
        this.trigger = trigger;
    }

    public Date hasNextFireTime() {
        if ( this.timestamp != null ) {
            return this.timestamp;
        } else {
            return this.trigger != null ? this.trigger.hasNextFireTime() : null;
        }
    }

    public Date nextFireTime() {
        if ( this.timestamp != null ) {
            Date next = this.timestamp;
            this.timestamp = null;
            return next;
        } else {
            return this.trigger != null ? trigger.nextFireTime() : null;
        }
    }

}
