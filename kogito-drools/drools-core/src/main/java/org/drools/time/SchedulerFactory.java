package org.drools.time;

import org.drools.time.impl.JDKScheduler;

public class SchedulerFactory {
    private static TimerService scheduler = new JDKScheduler();
    
    public static TimerService getScheduler() {
        return scheduler;
    }
}
