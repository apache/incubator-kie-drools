package org.drools.time.impl;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.drools.command.CommandService;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;

public interface TimerJobFactoryManager {
    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler);
    
    public void addTimerJobInstance(TimerJobInstance instance);
    
    public void removeTimerJobInstance(TimerJobInstance instance);
        
    
    public Collection<TimerJobInstance> getTimerJobInstances();    
    
    public void setCommandService(CommandService commandService);
    
    public CommandService getCommandService();
}
