/*
 * Copyright 2010 JBoss Inc
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

package org.drools.persistence.jpa;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.drools.command.CommandService;
import org.drools.command.impl.GenericCommand;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.SelfRemovalJob;
import org.drools.time.SelfRemovalJobContext;
import org.drools.time.Trigger;
import org.drools.time.impl.DefaultTimerJobInstance;
import org.drools.time.impl.JDKTimerService;
import org.drools.time.impl.TimerJobInstance;
import org.kie.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default Scheduler implementation that uses the
 * JDK built-in ScheduledThreadPoolExecutor as the
 * scheduler and the system clock as the clock.
 */
public class JpaJDKTimerService extends JDKTimerService {

    private static Logger logger = LoggerFactory.getLogger( JpaTimerJobInstance.class );
    
    private CommandService              commandService;

    private Map<Long, TimerJobInstance> timerInstances;

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public JpaJDKTimerService() {
        timerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
    }

    protected Callable<Void> createCallableJob(Job job,
                                               JobContext ctx,
                                               Trigger trigger,
                                               JDKJobHandle handle,
                                               InternalSchedulerService scheduler) {
        JpaJDKCallableJob jobInstance = new JpaJDKCallableJob( new SelfRemovalJob( job ),
                                                               new SelfRemovalJobContext( ctx,
                                                                                          timerInstances ),
                                                               trigger,
                                                               handle,
                                                               scheduler );

        this.timerInstances.put( handle.getId(),
                                 jobInstance );
        return jobInstance;
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return timerInstances.values();
    }

    public class JpaJDKCallableJob extends DefaultTimerJobInstance {

        public JpaJDKCallableJob(Job job,
                                 JobContext ctx,
                                 Trigger trigger,
                                 JDKJobHandle handle,
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
                commandService.execute( command );
            } catch(Exception e ) { 
                logger.error("Unable to execute job!", e);
                throw e;
            }
            
            return null;
        }

        private Void internalCall() throws Exception {
            return super.call();
        }
    }

    public static class JDKCallableJobCommand
        implements
        GenericCommand<Void> {

        private static final long serialVersionUID = 4L;

        private JpaJDKCallableJob job;

        public JDKCallableJobCommand(JpaJDKCallableJob job) {
            this.job = job;
        }

        public Void execute(Context context) {
            try {
                return job.internalCall();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
