package org.drools.persistence.jpa;

import org.kie.api.runtime.ExecutableRunner;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.base.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.base.time.Trigger;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaTimerJobInstance extends DefaultTimerJobInstance {       

    private static Logger logger = LoggerFactory.getLogger( JpaTimerJobInstance.class );
    
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
        try { 
            JDKCallableJobCommand command = new JDKCallableJobCommand( this );
            ExecutableRunner runner = ( (CommandServiceTimerJobFactoryManager) ( (TimerService) scheduler ).getTimerJobFactoryManager() ).getRunner();
            runner.execute( command );
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
