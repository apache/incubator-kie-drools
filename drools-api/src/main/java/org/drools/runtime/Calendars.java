package org.drools.runtime;

import org.drools.time.Calendar;

public interface Calendars {
    Calendar get(String identifier);

    void set(String identifier,
             Calendar value);
}
