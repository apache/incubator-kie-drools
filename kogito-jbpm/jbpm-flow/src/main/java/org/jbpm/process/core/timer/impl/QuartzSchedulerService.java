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
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Quartz based <code>GlobalSchedulerService</code> that is configured according
 * to Quartz rules and allows to store jobs in data base. With that it survives 
 * server crashes and operates as soon as service is initialized without session 
 * being active.
 *
 */
public class QuartzSchedulerService implements GlobalSchedulerService {

    private AtomicLong idCounter = new AtomicLong();
    private TimerService globalTimerService;
    private Scheduler scheduler;
    
 
    public QuartzSchedulerService() {
        
    }
    
    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        Long id = idCounter.getAndIncrement();
        String jobname = null;
        
        if (ctx instanceof ProcessJobContext) {
            ProcessJobContext processCtx = (ProcessJobContext) ctx;
            jobname = processCtx.getSessionId() + "-" + processCtx.getProcessInstanceId() + "-" + processCtx.getTimer().getId();
            if (processCtx instanceof StartProcessJobContext) {
                jobname = "StartProcess-"+((StartProcessJobContext) processCtx).getProcessId()+ "-" + processCtx.getTimer().getId();
            }
        } else {
            jobname = "Timer-"+ctx.getClass().getSimpleName()+ "-" + id;
        
        }
        
        // check if this scheduler already has such job registered if so there is no need to schedule it again        
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobname, "jbpm");
        
            if (jobDetail != null) {
                TimerJobInstance timerJobInstance = (TimerJobInstance) jobDetail.getJobDataMap().get("timerJobInstance");
                return timerJobInstance.getJobHandle();
            }
        } catch (SchedulerException e) {
            
        }
        GlobalQuartzJobHandle quartzJobHandle = new GlobalQuartzJobHandle(id, jobname, "jbpm");
        TimerJobInstance jobInstance = ((AcceptsTimerJobFactoryManager) globalTimerService).
                getTimerJobFactoryManager().createTimerJobInstance( job,
                                                                    ctx,
                                                                    trigger,
                                                                    quartzJobHandle,
                                                                    (InternalSchedulerService) globalTimerService );
        quartzJobHandle.setTimerJobInstance( (TimerJobInstance) jobInstance );

        internalSchedule(jobInstance);
        return quartzJobHandle;
    }

    @Override
    public boolean removeJob(JobHandle jobHandle) {
        GlobalQuartzJobHandle quartzJobHandle = (GlobalQuartzJobHandle) jobHandle;
        
        try {
            
            boolean removed =  this.scheduler.deleteJob(quartzJobHandle.getJobName(), quartzJobHandle.getJobGroup());            
            return removed;
        } catch (SchedulerException e) {            
            throw new RuntimeException("Exception while removing job", e);
        }
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {

        GlobalQuartzJobHandle quartzJobHandle = (GlobalQuartzJobHandle) timerJobInstance.getJobHandle();
        // Define job instance
        JobDetail jobq = new JobDetail(quartzJobHandle.getJobName(), quartzJobHandle.getJobGroup(), QuartzJob.class);

        jobq.getJobDataMap().put("timerJobInstance", timerJobInstance);
            
        // Define a Trigger that will fire "now"
        org.quartz.Trigger triggerq = new SimpleTrigger(quartzJobHandle.getJobName()+"_trigger", quartzJobHandle.getJobGroup(), timerJobInstance.getTrigger().hasNextFireTime());
            
        // Schedule the job with the trigger
        try {
            if (scheduler.isShutdown()) {
                return;
            }
            ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().addTimerJobInstance( timerJobInstance );
            JobDetail jobDetail = scheduler.getJobDetail(quartzJobHandle.getJobName(), quartzJobHandle.getJobGroup());
            if (jobDetail == null) {
                scheduler.scheduleJob(jobq, triggerq);
            } else {
                // need to add the job again to replace existing especially important if jobs are persisted in db
                scheduler.addJob(jobq, true);
                triggerq.setJobName(quartzJobHandle.getJobName());
                triggerq.setJobGroup(quartzJobHandle.getJobGroup());
                scheduler.rescheduleJob(quartzJobHandle.getJobName()+"_trigger", quartzJobHandle.getJobGroup(), triggerq);
            }
            
        } catch (JobPersistenceException e) {
            internalSchedule(new InmemoryTimerJobInstanceDelegate(quartzJobHandle.getJobName(), ((GlobalTimerService) globalTimerService).getTimerServiceId()));
        } catch (SchedulerException e) {
            ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().removeTimerJobInstance(timerJobInstance);
            throw new RuntimeException("Exception while scheduling job", e);
        }
    }

    @Override
    public void initScheduler(TimerService timerService) {
        this.globalTimerService = timerService;
        
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            this.scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException("Exception when initializing QuartzSchedulerService", e);
        }
    }

    @Override
    public void shutdown() {
        if (scheduler != null) {
            try {
                this.scheduler.shutdown();
            } catch (SchedulerException e) {
//                e.printStackTrace();
            }
        }
    }

    public static class GlobalQuartzJobHandle extends GlobalJobHandle {
        
        private static final long     serialVersionUID = 510l;
        private String jobName;
        private String jobGroup;
       
        public GlobalQuartzJobHandle(long id, String name, String group) {
            super(id);
            this.jobName = name;
            this.jobGroup = group;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getJobGroup() {
            return jobGroup;
        }

        public void setJobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
        }
    
    }
    
    public static class QuartzJob implements org.quartz.Job {

        @SuppressWarnings("unchecked")
        @Override
        public void execute(JobExecutionContext quartzContext) throws JobExecutionException {
            TimerJobInstance timerJobInstance = (TimerJobInstance) quartzContext.getJobDetail().getJobDataMap().get("timerJobInstance");
            try {
                ((Callable<Void>)timerJobInstance).call();
            } catch (Exception e) {
                throw new RuntimeException("Exception when executing scheduled job", e);
            }
            
        }
        
    }
    
    public static class InmemoryTimerJobInstanceDelegate implements TimerJobInstance, Serializable, Callable<Void> {
        
        private static final long serialVersionUID = 1L;
        private String jobname;
        private String timerServiceId;
        private transient TimerJobInstance delegate;
        
        public InmemoryTimerJobInstanceDelegate(String jobName, String timerServiceId) {
            this.jobname = jobName;
            this.timerServiceId = timerServiceId;
        }
        
        @Override
        public JobHandle getJobHandle() {
            findDelegate();
            return delegate.getJobHandle();
        }

        @Override
        public Job getJob() {
            findDelegate();
            return delegate.getJob();
        }

        @Override
        public Trigger getTrigger() {
            findDelegate();
            return delegate.getTrigger();
        }

        @Override
        public JobContext getJobContext() {
            findDelegate();
            return delegate.getJobContext();
        }
        
        protected void findDelegate() {
            if (delegate == null) {
                Collection<TimerJobInstance> timers = ((AcceptsTimerJobFactoryManager)TimerServiceRegistry.getInstance().get(timerServiceId))
                .getTimerJobFactoryManager().getTimerJobInstances();
                for (TimerJobInstance instance : timers) {
                    if (((GlobalQuartzJobHandle)instance.getJobHandle()).getJobName().equals(jobname)) {
                        delegate = instance;
                        break;
                    }
                }
            }
        }

        @Override
        public Void call() throws Exception {
            findDelegate();
            return ((Callable<Void>)delegate).call();
        }
        
    }
}
