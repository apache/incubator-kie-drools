package org.drools.core.time.impl;

import org.drools.core.time.EnqueuedSelfRemovalJobContext;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.base.time.JobHandle;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.base.time.Trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrackableTimeJobFactoryManager
    implements
    TimerJobFactoryManager {

    protected final Map<Long, TimerJobInstance> timerInstances;

    public TrackableTimeJobFactoryManager() {
        this(new HashMap<>());
    }

    protected TrackableTimeJobFactoryManager(Map<Long, TimerJobInstance> timerInstances) {
        this.timerInstances = timerInstances;
    }

    @Override
    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );

        return new DefaultTimerJobInstance( new SelfRemovalJob( job ),
                                            createJobContext( ctx ),
                                            trigger,
                                            handle,
                                            scheduler );
    }

    protected SelfRemovalJobContext createJobContext( JobContext ctx ) {
        return new EnqueuedSelfRemovalJobContext( ctx, timerInstances );
    }

    @Override
    public void addTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.put( instance.getJobHandle().getId(),
                                 instance );
    }

    @Override
    public void removeTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.remove( instance.getJobHandle().getId() );
    }

    @Override
    public void removeTimerJobInstance(JobHandle handle) {
        this.timerInstances.remove( handle.getId() );
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return timerInstances.values();
    }
}
