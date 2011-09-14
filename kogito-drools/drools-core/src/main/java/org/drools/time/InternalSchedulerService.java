package org.drools.time;

import org.drools.time.impl.TimerJobInstance;

public interface InternalSchedulerService {
    public void internalSchedule(TimerJobInstance timerJobInstance);
}
