/**
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

public class CalendarUtils {
    public static Date updateToNextIncludeDate(String[] calendarNames,
                                               Calendars calendars,
                                               Trigger trigger,
                                               Date next) {
        if ( calendarNames == null || calendarNames.length == 0 ) {
            // There are no assigned calendars
            return next;
        }

        // If we have calendars, check we can fire, or get next time until we can fire.
        while ( next != null ) {
            // this will loop forever if the trigger repeats forever and
            // included calendar position can be found
            boolean included = true;
            for ( String cal : calendarNames ) {
                // all calendars must not block, as soon as one blocks break
                if ( !calendars.get( cal ).isTimeIncluded( next.getTime() ) ) {
                    included = false;
                    break;
                }
            }
            if ( included == true ) {
                // if no calendars blocked, break
                break;
            } else {
                // otherwise increase the time and try again
                next = trigger.nextFireTime();
            }
        }

        return next;
    }
}
