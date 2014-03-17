package org.drools.impl.adapters;

import org.drools.time.Calendar;
import org.kie.api.runtime.Calendars;

public class CalendarsAdapter implements org.drools.runtime.Calendars {

    private final Calendars delegate;

    public CalendarsAdapter(Calendars delegate) {
        this.delegate = delegate;
    }

    public Calendar get(String identifier) {
        return new CalendarAdapter(delegate.get(identifier));
    }

    @Override
    public void set(String identifier, Calendar value) {
        delegate.set(identifier, ((CalendarAdapter)value).getDelegate());
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CalendarsAdapter && delegate.equals(((CalendarsAdapter)obj).delegate);
    }
}
