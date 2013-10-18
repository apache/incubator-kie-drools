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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.process.core.timer.SchedulerServiceInterceptor;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.impl.jdbcjobstore.JobStoreSupport;
import org.quartz.spi.JobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz based <code>GlobalSchedulerService</code> that is configured according
 * to Quartz rules and allows to store jobs in data base. With that it survives 
 * server crashes and operates as soon as service is initialized without session 
 * being active.
 *
 */
public class QuartzSchedulerService implements GlobalSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerService.class);

    private AtomicLong idCounter = new AtomicLong();
    private TimerService globalTimerService;
    private SchedulerServiceInterceptor interceptor = new DelegateSchedulerServiceInterceptor(this);
    
    // global data shared across all scheduler service instances
    private static Scheduler scheduler;    
    private static AtomicInteger timerServiceCounter = new AtomicInteger();
 
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
        } else if (ctx instanceof NamedJobContext) {
            jobname = ((NamedJobContext) ctx).getJobName();
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

        interceptor.internalSchedule(jobInstance);
        return quartzJobHandle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeJob(JobHandle jobHandle) {
        GlobalQuartzJobHandle quartzJobHandle = (GlobalQuartzJobHandle) jobHandle;
        
        try {
            
            boolean removed =  scheduler.deleteJob(quartzJobHandle.getJobName(), quartzJobHandle.getJobGroup());            
            return removed;
        } catch (SchedulerException e) {     
            
            throw new RuntimeException("Exception while removing job", e);
        } catch (RuntimeException e) {
            SchedulerMetaData metadata;
            try {
                metadata = scheduler.getMetaData();
                if (metadata.getJobStoreClass().isAssignableFrom(JobStoreCMT.class)) {
                    return true;
                }
            } catch (SchedulerException e1) {
                
            }
            throw e;
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
            
        } catch (ObjectAlreadyExistsException e) {
            // in general this should not happen even in clustered environment but just in case
            // already registered jobs should be caught in scheduleJob but due to race conditions it might not 
            // catch it in time - clustered deployments only
            logger.warn("Job has already been scheduled, most likely running in cluster: {}", e.getMessage());
            
        } catch (JobPersistenceException e) {
            if (e.getCause() instanceof NotSerializableException) {
                // in case job cannot be persisted, like rule timer then make it in memory
                internalSchedule(new InmemoryTimerJobInstanceDelegate(quartzJobHandle.getJobName(), ((GlobalTimerService) globalTimerService).getTimerServiceId()));
            } else {
                ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().removeTimerJobInstance(timerJobInstance);
                throw new RuntimeException(e);
            }
        } catch (SchedulerException e) {
            ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().removeTimerJobInstance(timerJobInstance);
            throw new RuntimeException("Exception while scheduling job", e);
        }
    }

    @Override
    public synchronized void initScheduler(TimerService timerService) {
        this.globalTimerService = timerService;
        timerServiceCounter.incrementAndGet();
        
        if (scheduler == null) {            
            try {
                scheduler = StdSchedulerFactory.getDefaultScheduler();            
                scheduler.start();
            } catch (SchedulerException e) {
                throw new RuntimeException("Exception when initializing QuartzSchedulerService", e);
            }
        
        }
    }

    @Override
    public void shutdown() {
        int current = timerServiceCounter.decrementAndGet();
        if (scheduler != null && current == 0) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                logger.warn("Error encountered while shutting down the scheduler", e);
            }
            scheduler = null;
        }
    }
    
    public void forceShutdown() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                logger.warn("Error encountered while shutting down (forced) the scheduler", e);
            }
            scheduler = null;
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
                boolean reschedule = true;
                Integer failedCount = (Integer) quartzContext.getJobDetail().getJobDataMap().get("failedCount");
                if (failedCount == null) {
                    failedCount = new Integer(0);
                }
                failedCount++;
                quartzContext.getJobDetail().getJobDataMap().put("failedCount", failedCount);
                if (failedCount > 5) {
                    logger.error("Timer execution failed 5 times in a roll, unscheduling ({})", quartzContext.getJobDetail().getFullName());
                    reschedule = false;
                }
                // let's give it a bit of time before failing/retrying
                try {
                    Thread.sleep(failedCount * 1000);
                } catch (InterruptedException e1) {
                    logger.debug("Got interrupted", e1);
                }
                throw new JobExecutionException("Exception when executing scheduled job", e, reschedule);
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

        @SuppressWarnings("unchecked")
        @Override
        public Void call() throws Exception {
            findDelegate();
            return ((Callable<Void>)delegate).call();
        }
        
    }

    @Override
    public JobHandle buildJobHandleForContext(NamedJobContext ctx) {
        return new GlobalQuartzJobHandle(-1, ctx.getJobName(), "jbpm");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isTransactional() {
        try {
            Class<JobStore> jobStoreClass = scheduler.getMetaData().getJobStoreClass();
            if (JobStoreSupport.class.isAssignableFrom(jobStoreClass)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("Unable to determine if quartz is transactional due to problems when checking job store class", e);
        }
        return false;
    }

    @Override
    public void setInterceptor(SchedulerServiceInterceptor interceptor) {
        this.interceptor = interceptor;
        
    }

	@Override
	public boolean retryEnabled() {
		return false;
	}
}
