package org.drools.core.time;

import org.drools.core.time.impl.TimerJobInstance;

public interface InternalSchedulerService {
    public void internalSchedule(TimerJobInstance timerJobInstance);
}
