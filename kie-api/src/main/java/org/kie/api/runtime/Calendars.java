package org.kie.api.runtime;

import org.kie.api.time.Calendar;

public interface Calendars {
    Calendar get(String identifier);

    void set(String identifier,
             Calendar value);
}
