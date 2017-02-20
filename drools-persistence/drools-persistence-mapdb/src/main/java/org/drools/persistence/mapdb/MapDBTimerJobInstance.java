package org.drools.persistence.mapdb;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.kie.api.runtime.ExecutableRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDBTimerJobInstance  extends DefaultTimerJobInstance {       

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger( MapDBTimerJobInstance.class );
    
    public MapDBTimerJobInstance(Job job,
            JobContext ctx,
            Trigger trigger,
            JobHandle handle,
            InternalSchedulerService scheduler) {
    	super(job, ctx, trigger, handle, scheduler);
    }

    public Void call() throws Exception {
        try { 
            JDKCallableJobCommand command = new JDKCallableJobCommand( this );
            ExecutableRunner commandService = ( (CommandServiceTimerJobFactoryManager) ( (TimerService) scheduler ).getTimerJobFactoryManager() ).getRunner();
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