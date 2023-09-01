package org.drools.verifier.core.index.model;

import java.util.Date;

public class ActivationTime {

    private final Date start;
    private final Date end;

    /**
     * Accepts null. Null is infinite
     */
    public ActivationTime(final Date start,
                          final Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean overlaps(final ActivationTime other) {
        final Date max = findMaxDate(start, other.start);
        final Date min = findMinDate(end, other.end);

        if (min == null || max == null) {
            return true;
        } else {
            return min.compareTo(max) >= 0;
        }
    }

    private Date findMaxDate(final Date date,
                             final Date other) {
        if (date == null && other == null) {
            return null;
        } else if (other == null || (date != null && date.after(other))) {
            return date;
        } else {
            return other;
        }
    }

    private Date findMinDate(final Date date,
                             final Date other) {
        if (date == null && other == null) {
            return null;
        } else if (other == null || (date != null && date.before(other))) {
            return date;
        } else {
            return other;
        }
    }
}
