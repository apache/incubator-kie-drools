package org.drools.persistence.jpa;

import java.util.Map;

import org.drools.command.CommandService;
import org.drools.time.AcceptsTimerJobFactoryManager;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;
import org.drools.time.impl.DefaultTimerJobInstance;
import org.drools.time.impl.TimerJobInstance;

public class JpaTimerJobInstance extends DefaultTimerJobInstance {       

    public JpaTimerJobInstance(Job job,
                               JobContext ctx,
                               Trigger trigger,
                               JobHandle handle,
                               InternalSchedulerService scheduler) {
        super( job,
               ctx,
               trigger,
               handle,
               scheduler );
    }

    public Void call() throws Exception {
        JDKCallableJobCommand command = new JDKCallableJobCommand( this );
        CommandService commandService = ((AcceptsTimerJobFactoryManager)scheduler).getTimerJobFactoryManager().getCommandService();
        commandService.execute( command );
        return null;
    }

    Void internalCall() throws Exception {
        return super.call();
    }
}