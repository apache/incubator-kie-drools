package org.drools.util;

public class LongQueueable extends BaseQueueable
    implements
    Comparable {
    private final Long value;

    public LongQueueable(long value) {
        this.value = new Long( value );
    }

    public int compareTo(Object object) {
        return this.value.compareTo( ((LongQueueable) object).value );
    }

    public String toString() {
        return this.value.toString();
    }
}
