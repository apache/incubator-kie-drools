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
package org.jbpm.process.core.timer.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.process.core.timer.SchedulerServiceInterceptor;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;

/**
 * ThreadPool based scheduler service backed by <code>ThreadPoolSchedulerService</code>
 *
 */
public class ThreadPoolSchedulerService implements GlobalSchedulerService {
    
    private AtomicLong idCounter = new AtomicLong();
    private ScheduledThreadPoolExecutor scheduler;
    private TimerService globalTimerService;
    private SchedulerServiceInterceptor interceptor = new DelegateSchedulerServiceInterceptor(this);
    
    private int poolSize;
    
    private ConcurrentHashMap<String, JobHandle> activeTimer = new ConcurrentHashMap<String, JobHandle>();
    
    public ThreadPoolSchedulerService(int poolSize) {
        this.poolSize = poolSize;        
    }
    

    @Override
    public void initScheduler(TimerService globalTimerService) {
        this.globalTimerService = globalTimerService;
        
        this.scheduler = new ScheduledThreadPoolExecutor(poolSize);
    }

    @Override
    public void shutdown() {
        this.scheduler.shutdownNow();
    }

    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {

        Date date = trigger.hasNextFireTime();
        if ( date != null ) {
            String jobname = null;
            if (ctx instanceof ProcessJobContext) {
                ProcessJobContext processCtx = (ProcessJobContext) ctx;
                jobname = processCtx.getSessionId() + "-" + processCtx.getProcessInstanceId() + "-" + processCtx.getTimer().getId();
                if (processCtx instanceof StartProcessJobContext) {
                    jobname = "StartProcess-"+((StartProcessJobContext) processCtx).getProcessId()+ "-" + processCtx.getTimer().getId();
                }
                if (activeTimer.containsKey(jobname)) {
                    return activeTimer.get(jobname);
                }
            
            }
            GlobalJDKJobHandle jobHandle = new GlobalJDKJobHandle( idCounter.getAndIncrement() );
            
            TimerJobInstance jobInstance = ((AcceptsTimerJobFactoryManager) globalTimerService).
                                 getTimerJobFactoryManager().createTimerJobInstance( job,
                                                                                     ctx,
                                                                                     trigger,
                                                                                     jobHandle,
                                                                                     (InternalSchedulerService) globalTimerService );
            jobHandle.setTimerJobInstance( (TimerJobInstance) jobInstance );
            interceptor.internalSchedule( (TimerJobInstance) jobInstance );
            if (jobname != null) {
                activeTimer.put(jobname, jobHandle);
            }
            return jobHandle;
        } else {
            return null;
        }

    }

    @Override
    public boolean removeJob(JobHandle jobHandle) {
        if (jobHandle == null) {
            return false;
        }
        jobHandle.setCancel( true );
        JobContext jobContext = ((GlobalJDKJobHandle) jobHandle).getTimerJobInstance().getJobContext();
        try {
            ProcessJobContext processCtx = null;
            if (jobContext instanceof SelfRemovalJobContext) {
                processCtx = (ProcessJobContext) ((SelfRemovalJobContext) jobContext).getJobContext();
            } else {
                processCtx = (ProcessJobContext) jobContext;
            }
            
            String jobname = processCtx.getSessionId() + "-" + processCtx.getProcessInstanceId() + "-" + processCtx.getTimer().getId();
            if (processCtx instanceof StartProcessJobContext) {
                jobname = "StartProcess-"+((StartProcessJobContext) processCtx).getProcessId()+ "-" + processCtx.getTimer().getId();
            }
            activeTimer.remove(jobname);
            ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().
            removeTimerJobInstance( ((GlobalJDKJobHandle) jobHandle).getTimerJobInstance() );
        } catch (ClassCastException e) {
            // do nothing in case ProcessJobContext was not given
        }
        boolean removed =  this.scheduler.remove( (Runnable) ((GlobalJDKJobHandle) jobHandle).getFuture() );
        return removed;       
    }
    
    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        if (scheduler.isShutdown()) {
            return;
        }
        Date date = timerJobInstance.getTrigger().hasNextFireTime();
        Callable<Void> item = (Callable<Void>) timerJobInstance;

        GlobalJDKJobHandle jobHandle = (GlobalJDKJobHandle) timerJobInstance.getJobHandle();
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
        ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().addTimerJobInstance( timerJobInstance );
    }
    
    public static class GlobalJDKJobHandle extends GlobalJobHandle implements Serializable {
    
        private static final long     serialVersionUID = 510l;
    
        private transient ScheduledFuture<Void> future;       
    
        public GlobalJDKJobHandle(long id) {
            super(id);
        }
    
        public ScheduledFuture<Void> getFuture() {
            return future;
        }
    
        public void setFuture(ScheduledFuture<Void> future) {
            this.future = future;
        }    
    
    }

    @Override
    public JobHandle buildJobHandleForContext(NamedJobContext ctx) {
        // this is in memory scheduler and the building of context is required for permanent ScueduleService only
        return null;
    }


    @Override
    public boolean isTransactional() {
        return false;
    }


    @Override
    public void setInterceptor(SchedulerServiceInterceptor interceptor) {
        this.interceptor = interceptor;        
    }


}
