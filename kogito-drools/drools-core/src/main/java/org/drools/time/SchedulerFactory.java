package org.drools.time;

import org.drools.time.impl.JDKTimerService;

public class SchedulerFactory {
    private static TimerService timerService = new JDKTimerService();
    
    public static TimerService getScheduler() {
        return timerService;
    }
}
