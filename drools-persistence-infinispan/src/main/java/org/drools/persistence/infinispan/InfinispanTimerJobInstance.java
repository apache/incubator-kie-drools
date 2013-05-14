package org.drools.persistence.infinispan;

import org.drools.core.command.CommandService;
import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfinispanTimerJobInstance extends DefaultTimerJobInstance {       

    private static Logger logger = LoggerFactory.getLogger( InfinispanTimerJobInstance.class );
    
    public InfinispanTimerJobInstance(Job job,
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
        try { 
            JDKCallableJobCommand command = new JDKCallableJobCommand( this );
            CommandService commandService = ((AcceptsTimerJobFactoryManager)scheduler).getTimerJobFactoryManager().getCommandService();
            commandService.execute( command );
            return null;
        } catch( Exception e ) { 
            logger.error("Unable to execute timer job!", e);
            throw e;
        }
    }

    Void internalCall() throws Exception {
        return super.call();
    }
}
