package org.drools.core.time.impl;

import java.util.Collection;

import org.drools.core.command.CommandService;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;

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
