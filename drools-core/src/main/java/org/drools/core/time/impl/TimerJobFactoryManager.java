package org.drools.core.time.impl;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;

import java.util.Collection;

public interface TimerJobFactoryManager {
    TimerJobInstance createTimerJobInstance(Job job,
                                            JobContext ctx,
                                            Trigger trigger,
                                            JobHandle handle,
                                            InternalSchedulerService scheduler);
    
    void addTimerJobInstance(TimerJobInstance instance);
    
    void removeTimerJobInstance(TimerJobInstance instance);

    Collection<TimerJobInstance> getTimerJobInstances();
}
