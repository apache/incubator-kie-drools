package org.drools.scheduler;

import org.drools.scheduler.impl.jdk.JDKScheduler;

public class SchedulerFactory {
    private static Scheduler scheduler = new JDKScheduler();
    
    public static Scheduler getScheduler() {
        return scheduler;
    }
}
