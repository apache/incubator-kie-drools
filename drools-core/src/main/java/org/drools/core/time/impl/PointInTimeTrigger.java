package org.drools.core.time.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Date;

import org.drools.base.time.Trigger;
import org.kie.api.time.Calendar;

public class PointInTimeTrigger implements Trigger {

    private Date timestamp;

    public static PointInTimeTrigger createPointInTimeTrigger(final long timestamp, final Collection<Calendar> calendars) {
        if (calendars != null && !calendars.isEmpty()) {
            if (calendars.stream().noneMatch(calendar -> calendar.isTimeIncluded(timestamp))) {
                return null;
            }
        }
        return new PointInTimeTrigger(timestamp);
    }

    public PointInTimeTrigger() {
    }

    public PointInTimeTrigger(final long timestamp) {
        this.timestamp = new Date(timestamp);
    }

    public Date hasNextFireTime() {
        return this.timestamp;
    }

    public Date nextFireTime() {
        final Date next = timestamp;
        this.timestamp = null;
        return next;
    }

    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.timestamp = (Date) in.readObject();
    }

    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.timestamp);
    }

    @Override
    public String toString() {
        return "PointInTimeTrigger @ " + timestamp;
    }
}
