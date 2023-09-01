/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.persistence.jpa;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.base.time.Trigger;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.drools.core.time.impl.JDKTimerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default Scheduler implementation that uses the
 * JDK built-in ScheduledThreadPoolExecutor as the
 * scheduler and the system clock as the clock.
 */
public class JpaJDKTimerService extends JDKTimerService {

    private static Logger logger = LoggerFactory.getLogger( JpaTimerJobInstance.class );
    
    private ExecutableRunner runner;

    private Map<Long, TimerJobInstance> timerInstances;

    public void setCommandService(ExecutableRunner runner ) {
        this.runner = runner;
    }

    public JpaJDKTimerService() {
        this( 1 );
    }

    public JpaJDKTimerService(int size) {
        super( size );
        timerInstances = new ConcurrentHashMap<>();
    }

    @Override
    public void reset() {
        super.reset();
        timerInstances.clear();
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
                runner.execute( command );
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
        ExecutableCommand<Void> {

        private static final Logger LOG = LoggerFactory.getLogger(JDKCallableJobCommand.class);

        private static final long serialVersionUID = 4L;

        private JpaJDKCallableJob job;

        public JDKCallableJobCommand(JpaJDKCallableJob job) {
            this.job = job;
        }

        public Void execute(Context context) {
            try {
                return job.internalCall();
            } catch ( Exception e ) {
                LOG.error("Exception", e);
            }
            return null;
        }

    }

}
