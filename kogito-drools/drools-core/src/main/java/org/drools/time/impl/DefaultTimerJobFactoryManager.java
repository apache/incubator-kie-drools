package org.drools.time.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.drools.command.CommandService;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;

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

    public void addTimerJobInstance(TimerJobInstance instance) {
  
    }

    public void removeTimerJobInstance(TimerJobInstance instance) {

    }

    public void setCommandService(CommandService commandService) { 
    }

    public CommandService getCommandService() {
        return null;
    }

}
