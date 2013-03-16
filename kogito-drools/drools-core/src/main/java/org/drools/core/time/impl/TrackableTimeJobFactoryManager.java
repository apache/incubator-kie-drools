package org.drools.core.time.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.command.CommandService;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.Trigger;

public class TrackableTimeJobFactoryManager
    implements
    TimerJobFactoryManager {

    private Map<Long, TimerJobInstance> timerInstances;

    public TrackableTimeJobFactoryManager() {
        timerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
    }

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );
        DefaultTimerJobInstance jobInstance = new DefaultTimerJobInstance( new SelfRemovalJob( job ),
                                                                           new SelfRemovalJobContext( ctx,
                                                                                                      timerInstances ),
                                                                           trigger,
                                                                           handle,
                                                                           scheduler );

        return jobInstance; 
    }

    public void addTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.put( instance.getJobHandle().getId(),
                                 instance );
    }

    public void removeTimerJobInstance(TimerJobInstance instance) {

        this.timerInstances.remove( instance.getJobHandle().getId() );
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return timerInstances.values();
    }

    public void setCommandService(CommandService commandService) {
        
    }
    
    public CommandService getCommandService() {
        return null;
    }

}
