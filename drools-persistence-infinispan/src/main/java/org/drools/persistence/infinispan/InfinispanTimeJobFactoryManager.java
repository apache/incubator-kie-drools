package org.drools.persistence.infinispan;

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
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;

public class InfinispanTimeJobFactoryManager
    implements
    TimerJobFactoryManager {

    private CommandService              commandService;

    private Map<Long, TimerJobInstance> timerInstances;

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public InfinispanTimeJobFactoryManager() {
        timerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
    }

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );
        InfinispanTimerJobInstance jobInstance = new InfinispanTimerJobInstance( new SelfRemovalJob( job ),
                                                                   new SelfRemovalJobContext( ctx,
                                                                                              timerInstances ),
                                                                   trigger,
                                                                   handle,
                                                                   scheduler);

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

    public CommandService getCommandService() {
        return commandService;
    }

}
