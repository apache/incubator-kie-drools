package org.drools.base.facttemplates;

import java.util.concurrent.TimeUnit;

public interface Event extends Fact {

    long getTimestamp();

    long getExpiration();

    Event withExpiration( long value, TimeUnit unit );

    default boolean isEvent() {
        return true;
    }
}
