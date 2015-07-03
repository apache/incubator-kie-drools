package org.drools.persistence.jpa;

import org.drools.core.command.CommandService;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.ThreadSafeTrackableTimeJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;

public class JpaTimeJobFactoryManager
        extends ThreadSafeTrackableTimeJobFactoryManager
        implements CommandServiceTimerJobFactoryManager {

    private CommandService commandService;

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public CommandService getCommandService() {
        return commandService;
    }

    public TimerJobInstance createTimerJobInstance(Job job,
                                                   JobContext ctx,
                                                   Trigger trigger,
                                                   JobHandle handle,
                                                   InternalSchedulerService scheduler) {
        ctx.setJobHandle( handle );

        return new JpaTimerJobInstance( new SelfRemovalJob( job ),
                                        createJobContext( ctx ),
                                        trigger,
                                        handle,
                                        scheduler );
    }
}
