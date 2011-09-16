package org.drools.time.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.command.CommandService;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.SelfRemovalJob;
import org.drools.time.SelfRemovalJobContext;
import org.drools.time.Trigger;
import org.drools.time.impl.TimerJobFactoryManager;
import org.drools.time.impl.TimerJobInstance;

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
