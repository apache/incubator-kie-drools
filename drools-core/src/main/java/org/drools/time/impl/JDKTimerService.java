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

package org.drools.time.impl;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.time.AcceptsTimerJobFactoryManager;
import org.drools.time.InternalSchedulerService;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.SessionClock;
import org.drools.time.TimerService;
import org.drools.time.Trigger;

/**
 * A default Scheduler implementation that uses the
 * JDK built-in ScheduledThreadPoolExecutor as the
 * scheduler and the system clock as the clock.
 */
public class JDKTimerService
    implements
    TimerService,
    SessionClock,
    InternalSchedulerService,
    AcceptsTimerJobFactoryManager {

    private static ScheduledThreadPoolExecutor scheduler;
    static {
        int corePoolSize = Integer.valueOf(System.getProperty(
                "drools.timerService.scheduler.corePoolSize",
                "5"));
        scheduler = new ScheduledThreadPoolExecutor(corePoolSize);
    }
    private AtomicLong                    idCounter = new AtomicLong();


    protected TimerJobFactoryManager        jobFactoryManager = DefaultTimerJobFactoryManager.instance;

    public JDKTimerService() {
    }


    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager) {
        this.jobFactoryManager = timerJobFactoryManager;
    }

    public void setCounter(long counter) {
        idCounter = new AtomicLong( counter );
    }
    
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return this.jobFactoryManager;
    }

    /**
     * @inheritDoc
     */
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void shutdown() {
        // forcing a shutdownNow instead of a regular shutdown()
        // to avoid delays on shutdown. This is an irreversible 
        // operation anyway, called on session dispose.
        for (TimerJobInstance timerJobInstance : getTimerJobInstances()) {
            JDKJobHandle jobHandle = (JDKJobHandle) timerJobInstance
                    .getJobHandle();
            removeJob(jobHandle);
        }
    }

    public JobHandle scheduleJob(Job job,
                                 JobContext ctx,
                                 Trigger trigger) {
        Date date = trigger.hasNextFireTime();
        if ( date != null ) {
            JDKJobHandle jobHandle = new JDKJobHandle( idCounter.getAndIncrement() );
            
            TimerJobInstance jobInstance = jobFactoryManager.createTimerJobInstance( job,
                                                                                     ctx,
                                                                                     trigger,
                                                                                     jobHandle,
                                                                                     this );
            jobHandle.setTimerJobInstance( (TimerJobInstance) jobInstance );
            internalSchedule( (TimerJobInstance) jobInstance );

            return jobHandle;
        } else {
            return null;
        }
    }

    public void internalSchedule(TimerJobInstance timerJobInstance) {
        Date date = timerJobInstance.getTrigger().hasNextFireTime();
        Callable<Void> item = (Callable<Void>) timerJobInstance;

        JDKJobHandle jobHandle = (JDKJobHandle) timerJobInstance.getJobHandle();
        long then = date.getTime();
        long now = System.currentTimeMillis();
        ScheduledFuture<Void> future = null;
        if ( then >= now ) {
            future = scheduler.schedule( item,
                                         then - now,
                                         TimeUnit.MILLISECONDS );
        } else {
            future = scheduler.schedule( item,
                                         0,
                                         TimeUnit.MILLISECONDS );
        }

        jobHandle.setFuture( future );
        jobFactoryManager.addTimerJobInstance( timerJobInstance );
    }

    public boolean removeJob(JobHandle jobHandle) {
        jobHandle.setCancel( true );
        jobFactoryManager.removeTimerJobInstance( ((JDKJobHandle) jobHandle).getTimerJobInstance() );
        return scheduler.remove( (Runnable) ((JDKJobHandle) jobHandle).getFuture() );
    }

    public static class JDKJobHandle extends DefaultJobHandle
        implements
        JobHandle {

        private static final long     serialVersionUID = 510l;

        private ScheduledFuture<Void> future;       

        public JDKJobHandle(long id) {
            super(id);
        }
        
        public ScheduledFuture<Void> getFuture() {
            return future;
        }

        public void setFuture(ScheduledFuture<Void> future) {
            this.future = future;
        }    

    }

    public long getTimeToNextJob() {
        return 0;
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return jobFactoryManager.getTimerJobInstances();
    }

}
