package org.drools.core.time.impl;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;

import java.util.Collection;
import java.util.Collections;

public class DefaultTimerJobFactoryManager
    implements
    TimerJobFactoryManager {
    
    public static final DefaultTimerJobFactoryManager instance = new DefaultTimerJobFactoryManager();

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );
        return new DefaultTimerJobInstance( job,
                                            ctx,
                                            trigger,
                                            handle,
                                            scheduler );
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return Collections.emptyList();
    }

    public void addTimerJobInstance(TimerJobInstance instance) { }

    public void removeTimerJobInstance(TimerJobInstance instance) { }
}
