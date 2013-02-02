package org.drools.persistence.jpa;

import org.drools.command.CommandService;
import org.drools.time.*;
import org.drools.time.impl.DefaultTimerJobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaTimerJobInstance extends DefaultTimerJobInstance {

    private static Logger logger = LoggerFactory.getLogger(JpaTimerJobInstance.class);

    public JpaTimerJobInstance(Job job,
                               JobContext ctx,
                               Trigger trigger,
                               JobHandle handle,
                               InternalSchedulerService scheduler) {
        super(job,
              ctx,
              trigger,
              handle,
              scheduler);
    }

    public Void call() throws Exception {
        try {
            JDKCallableJobCommand command = new JDKCallableJobCommand(this);
            CommandService commandService = ((AcceptsTimerJobFactoryManager) scheduler).getTimerJobFactoryManager().getCommandService();
            commandService.execute(command);
            return null;
        } catch (Exception e) {
            logger.error("Unable to execute timer job!", e);
            throw e;
        }
    }

    Void internalCall() throws Exception {
        return super.call();
    }
}