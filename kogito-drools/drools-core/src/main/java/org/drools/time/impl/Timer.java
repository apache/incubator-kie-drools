package org.drools.time.impl;

import org.drools.time.Trigger;

public interface Timer {
    Trigger createTrigger(long timestamp);
}
