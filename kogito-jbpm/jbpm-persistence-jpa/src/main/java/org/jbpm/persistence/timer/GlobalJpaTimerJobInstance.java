/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.persistence.timer;

import org.drools.core.command.CommandService;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.drools.persistence.jpa.JDKCallableJobCommand;
import org.drools.persistence.jpa.JpaTimerJobInstance;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService.DisposableCommandService;

/**
 * Extension to the regular <code>JpaTimerJobInstance</code> that makes use of
 * GlobalTimerService to allow auto reactivate session.
 * 
 * Important to note is that when timer service created session this job instance
 * will dispose that session to leave it in the same state it was before job was executed
 * to avoid concurrent usage of the same session by different threads
 *
 */
public class GlobalJpaTimerJobInstance extends JpaTimerJobInstance {

    private static final long serialVersionUID = -5383556604449217342L;
    private String timerServiceId;

    public GlobalJpaTimerJobInstance(Job job, JobContext ctx, Trigger trigger,
            JobHandle handle, InternalSchedulerService scheduler) {
        super(job, ctx, trigger, handle, scheduler);
        timerServiceId = ((GlobalTimerService) scheduler).getTimerServiceId();
    }

    @Override
    public Void call() throws Exception {
        CommandService commandService = null;
        try { 
            JDKCallableJobCommand command = new JDKCallableJobCommand( this );
            if (scheduler == null) {
                scheduler = (InternalSchedulerService) TimerServiceRegistry.getInstance().get(timerServiceId);
            }
            commandService = ((GlobalTimerService) scheduler).getCommandService(getJobContext());            
            commandService.execute( command );
            
            return null;
        } catch( Exception e ) { 

            throw e;
        } finally {
            if (commandService != null && commandService instanceof DisposableCommandService) {
                ((DisposableCommandService) commandService).dispose();
            }
        }
    }

}
