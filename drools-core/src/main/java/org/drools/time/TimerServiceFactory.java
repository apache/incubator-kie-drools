package org.drools.time;

import org.drools.time.impl.JDKTimerService;

public class TimerServiceFactory {
    private static TimerService timerService = new JDKTimerService();
    
    public static TimerService getTimerService() {
        return timerService;
    }
}
