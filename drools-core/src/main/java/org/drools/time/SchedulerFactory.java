package org.drools.time;

import org.drools.time.impl.JDKScheduler;

public class SchedulerFactory {
    private static TimeServices scheduler = new JDKScheduler();
    
    public static TimeServices getScheduler() {
        return scheduler;
    }
}
