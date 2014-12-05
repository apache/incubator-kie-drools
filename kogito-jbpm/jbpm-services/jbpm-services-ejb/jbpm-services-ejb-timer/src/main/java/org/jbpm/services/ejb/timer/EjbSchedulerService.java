package org.jbpm.services.ejb.timer;

import java.util.concurrent.atomic.AtomicLong;

import javax.naming.InitialContext;
import javax.naming.NamingException;

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
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;


public class EjbSchedulerService implements GlobalSchedulerService {

	private AtomicLong idCounter = new AtomicLong();
	private TimerService globalTimerService;
	private EJBTimerScheduler scheduler;
	

	@Override
	public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
		Long id = idCounter.getAndIncrement();
		String jobName = getJobName(ctx, id);
		EjbGlobalJobHandle jobHandle = new EjbGlobalJobHandle(id, jobName, ((GlobalTimerService) globalTimerService).getTimerServiceId());
		
		TimerJobInstance jobInstance = scheduler.getTimerByName(jobName);
		if (jobInstance != null) {
			return jobInstance.getJobHandle();
		}
		
		jobInstance = ((AcceptsTimerJobFactoryManager) globalTimerService).getTimerJobFactoryManager().createTimerJobInstance(
														job, 
														ctx, 
														trigger, 
														jobHandle, 
														(InternalSchedulerService) globalTimerService);
		
		jobHandle.setTimerJobInstance((TimerJobInstance) jobInstance);
		
		internalSchedule(jobInstance);
		return jobHandle;
	}

	@Override
	public boolean removeJob(JobHandle jobHandle) {
		
		boolean result = scheduler.removeJob(jobHandle);
	
		return result;
	}

	@Override
	public void internalSchedule(TimerJobInstance timerJobInstance) {
		scheduler.internalSchedule(timerJobInstance);
	}

	@Override
	public void initScheduler(TimerService timerService) {
		this.globalTimerService = timerService;
		try {
			this.scheduler = InitialContext.doLookup("java:module/EJBTimerScheduler");
		} catch (NamingException e) {
			throw new RuntimeException("Unable to find EJB scheduler for jBPM timer service", e);
		}
	}

	@Override
	public void shutdown() {
		// managed by container - no op

	}

	@Override
	public JobHandle buildJobHandleForContext(NamedJobContext ctx) {

		return new EjbGlobalJobHandle(-1, getJobName(ctx, -1l), ((GlobalTimerService) globalTimerService).getTimerServiceId());
	}

	@Override
	public boolean isTransactional() {
		return true;
	}

	@Override
	public boolean retryEnabled() {
		return false;
	}

	@Override
	public void setInterceptor(SchedulerServiceInterceptor interceptor) {
		// not used here
	}

	@Override
	public boolean isValid(GlobalJobHandle jobHandle) {
		return scheduler.isValid(jobHandle);
	}
	
	private String getJobName(JobContext ctx, Long id) {

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
        return jobname;
	}

}
