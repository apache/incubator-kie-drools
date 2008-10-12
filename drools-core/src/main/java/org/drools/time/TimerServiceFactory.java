package org.drools.time;

import org.drools.ClockType;
import org.drools.time.impl.JDKTimerService;
import org.drools.time.impl.PseudoClockScheduler;

public class TimerServiceFactory {
    
    public static TimerService getTimerService( ClockType type ) {
        switch( type ) {
            case REALTIME_CLOCK:
                return new JDKTimerService();
            case PSEUDO_CLOCK:
                return new PseudoClockScheduler();
        }
        return null;
    }
}
