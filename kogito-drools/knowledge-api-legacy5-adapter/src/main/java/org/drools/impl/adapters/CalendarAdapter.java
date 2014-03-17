package org.drools.impl.adapters;

import org.kie.api.time.Calendar;

public class CalendarAdapter implements org.drools.time.Calendar {

    private final Calendar delegate;

    public CalendarAdapter(Calendar delegate) {
        this.delegate = delegate;
    }

    public boolean isTimeIncluded(long timestamp) {
        return delegate.isTimeIncluded(timestamp);
    }

    public Calendar getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CalendarAdapter && delegate.equals(((CalendarAdapter)obj).delegate);
    }
}
