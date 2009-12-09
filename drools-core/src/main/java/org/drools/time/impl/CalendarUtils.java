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
