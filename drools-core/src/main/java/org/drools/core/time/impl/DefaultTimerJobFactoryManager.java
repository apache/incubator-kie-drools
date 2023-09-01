package org.drools.core.time.impl;

import java.util.Collection;
import java.util.Collections;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;

public class DefaultTimerJobFactoryManager
    implements
    TimerJobFactoryManager {
    
    public static final DefaultTimerJobFactoryManager INSTANCE = new DefaultTimerJobFactoryManager();

    @Override
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

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances() {
        return Collections.emptyList();
    }

    @Override
    public void addTimerJobInstance(TimerJobInstance instance) { }

    @Override
    public void removeTimerJobInstance(TimerJobInstance instance) { }

    @Override
    public void removeTimerJobInstance(JobHandle handle) {

    }
}
