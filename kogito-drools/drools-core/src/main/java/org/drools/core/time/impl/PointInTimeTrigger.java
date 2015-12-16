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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

public class PointInTimeTrigger
    implements
    Trigger {
    private Date timestamp;

    public PointInTimeTrigger() {
    }

    public PointInTimeTrigger(long timestamp,
                              String[] calendarNames,
                              Calendars calendars) {
        boolean included = true;

        if ( calendars != null && calendarNames != null && calendarNames.length > 0 ) {
            for ( String calName : calendarNames ) {
                // all calendars must not block, as soon as one blocks break
                org.kie.api.time.Calendar cal = calendars.get( calName );
                if ( cal != null && !cal.isTimeIncluded( timestamp ) ) {
                    included = false;
                    break;
                }
            }
        }

        if ( included ) {
            this.timestamp = new Date( timestamp );
        }

    }

    public Date hasNextFireTime() {
        return this.timestamp;
    }

    public Date nextFireTime() {
        Date next = timestamp;
        this.timestamp = null;
        return next;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.timestamp = (Date) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.timestamp );
    }

    @Override
    public String toString() {
        return "PointInTimeTrigger @ " + timestamp;
    }
}
