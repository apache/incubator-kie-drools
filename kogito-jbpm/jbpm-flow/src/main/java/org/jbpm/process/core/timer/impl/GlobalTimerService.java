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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.NamedJobContext;
import org.jbpm.process.instance.timer.TimerManager.ProcessJobContext;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.command.Context;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlobalTimerService implements TimerService, InternalSchedulerService, AcceptsTimerJobFactoryManager {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalTimerService.class);
    
    protected TimerJobFactoryManager jobFactoryManager;
    protected GlobalSchedulerService schedulerService;
    protected RuntimeManager manager;
    protected ConcurrentHashMap<Integer, List<GlobalJobHandle>> timerJobsPerSession = new ConcurrentHashMap<Integer, List<GlobalJobHandle>>();
    private String timerServiceId;
    
    public GlobalTimerService(RuntimeManager manager, GlobalSchedulerService schedulerService) {
        this.manager = manager;
        this.schedulerService = schedulerService;
        this.schedulerService.initScheduler(this);
        try {
            this.jobFactoryManager = (TimerJobFactoryManager) Class.forName("org.jbpm.persistence.timer.GlobalJPATimerJobFactoryManager").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        if (ctx instanceof ProcessJobContext) {
            ProcessJobContext processCtx = (ProcessJobContext) ctx; 
 
            List<GlobalJobHandle> jobHandles = timerJobsPerSession.get(processCtx.getSessionId());
            if (jobHandles == null) {
                jobHandles = new CopyOnWriteArrayList<GlobalJobHandle>();
                timerJobsPerSession.put(processCtx.getSessionId(), jobHandles);
            } else {
                // check if the given job is already scheduled
                for (GlobalJobHandle handle : jobHandles) {
                    long timerId = handle.getTimerId();
                    if (timerId == processCtx.getTimer().getId()) {
                        // this timer job is already registered
                        return handle;
                    }
                }
            }
            GlobalJobHandle jobHandle = (GlobalJobHandle) this.schedulerService.scheduleJob(job, ctx, trigger);
            if (jobHandle != null) {
            	jobHandles.add(jobHandle);
            }
                       
            return jobHandle;
        }
        GlobalJobHandle jobHandle = (GlobalJobHandle) this.schedulerService.scheduleJob(job, ctx, trigger);
        return jobHandle;
    }

    @Override
    public boolean removeJob(JobHandle jobHandle) {
        if (jobHandle == null) {
            return false;
        }
        
        int sessionId = ((GlobalJobHandle) jobHandle).getSessionId();
        List<GlobalJobHandle> handles = timerJobsPerSession.get(sessionId);
        if (handles == null) {
        	logger.debug("No known job handles for session {}", sessionId);
            return this.schedulerService.removeJob(jobHandle);
        }       

        if (handles.contains(jobHandle)) {
        	logger.debug("Found match so removing job handle {} from sessions {} handles", jobHandle, sessionId);
            handles.remove(jobHandle);
            if (handles.isEmpty()) {
                timerJobsPerSession.remove(sessionId);
            }
            return this.schedulerService.removeJob(jobHandle);
        } else {
        	logger.debug("No match for job handle {} within handles of session {}", jobHandle, sessionId);
            return false;
        }
    }

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void shutdown() {
        //do nothing, this timer service is always active

    }
    
    public void destroy() {
        Collection<List<GlobalJobHandle>> activeTimers = timerJobsPerSession.values();
        for (List<GlobalJobHandle> handles : activeTimers) {
            for (GlobalJobHandle handle : handles) {
                this.schedulerService.removeJob(handle);
            }
        }
    }

    @Override
    public long getTimeToNextJob() {
        return 0;
    }

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances(int id) {
        Collection<TimerJobInstance> timers = new ArrayList<TimerJobInstance>();
        List<GlobalJobHandle> jobs = timerJobsPerSession.get(id); {
            if (jobs != null) {
                for (GlobalJobHandle job : jobs) {
                	if (job != null && schedulerService.isValid(job)) {
                		timers.add(job.getTimerJobInstance());
                	}
                }
            }
        }   
        logger.debug("Returning  timers {} for session {}", timers, id);
        return timers;
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        if (this.schedulerService instanceof InternalSchedulerService) {
            ((InternalSchedulerService) this.schedulerService).internalSchedule(timerJobInstance);
        } else {
            throw new UnsupportedOperationException("Unsupported scheduler operation internalSchedule on class " + this.schedulerService.getClass()); 
        }
    }

    @Override
    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager) {
    	if (this.jobFactoryManager.getCommandService() == null) {
    		this.jobFactoryManager.setCommandService(timerJobFactoryManager.getCommandService());
    	}
    }

    @Override
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return this.jobFactoryManager;
    }
    
    public CommandService getCommandService(JobContext jobContext) {
        JobContext ctxorig = jobContext;
        if (ctxorig instanceof SelfRemovalJobContext) {
            ctxorig = ((SelfRemovalJobContext) ctxorig).getJobContext();
        }
        ProcessJobContext ctx = null;
        if (ctxorig instanceof ProcessJobContext) {
            ctx = (ProcessJobContext) ctxorig;
        } else if(ctxorig instanceof NamedJobContext){
        	return getCommandService(((NamedJobContext)ctxorig).getProcessInstanceId(), ctx);
        } else {
            return jobFactoryManager.getCommandService(); 
        }
        
        return getCommandService(ctx.getProcessInstanceId(), ctx);
    }
    
    public String getTimerServiceId() {
        return timerServiceId;
    }

    public void setTimerServiceId(String timerServiceId) {
        this.timerServiceId = timerServiceId;
    }
    
    public JobHandle buildJobHandleForContext(NamedJobContext ctx) {
        return this.schedulerService.buildJobHandleForContext(ctx);
    }
    
    public InternalRuntimeManager getRuntimeManager() {
    	return (InternalRuntimeManager) manager;
    }
    
    protected CommandService getCommandService(Long processInstanceId, ProcessJobContext ctx) {
    	RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        if (runtime == null) {
            throw new RuntimeException("No runtime engine found, could not be initialized yet");
        }
        
        if (runtime.getKieSession() instanceof CommandBasedStatefulKnowledgeSession) {
            CommandBasedStatefulKnowledgeSession cmd = (CommandBasedStatefulKnowledgeSession) runtime.getKieSession();
            if (ctx != null) {
            	ctx.setKnowledgeRuntime((InternalKnowledgeRuntime) ((KnowledgeCommandContext) cmd.getCommandService().getContext()).getKieSession());
            }
            return new DisposableCommandService(cmd.getCommandService(), manager, runtime, schedulerService.retryEnabled());
        } else if (runtime.getKieSession() instanceof InternalKnowledgeRuntime && ctx != null) {
            ctx.setKnowledgeRuntime((InternalKnowledgeRuntime) runtime.getKieSession());
        }
        
        return new DisposableCommandService(jobFactoryManager.getCommandService(), manager, runtime, schedulerService.retryEnabled());
    }


    public static class GlobalJobHandle extends DefaultJobHandle
        implements
        JobHandle{
    
        private static final long     serialVersionUID = 510l;
    
        public GlobalJobHandle(long id) {
            super(id);
        }
        
        public long getTimerId() {
            JobContext ctx = this.getTimerJobInstance().getJobContext();
            if (ctx instanceof SelfRemovalJobContext) {
                ctx = ((SelfRemovalJobContext) ctx).getJobContext();
            }
            return ((ProcessJobContext)ctx).getTimer().getId();
        }
    
        public int getSessionId() {
        	if (this.getTimerJobInstance() != null) {
	            JobContext ctx = this.getTimerJobInstance().getJobContext();
	            if (ctx instanceof SelfRemovalJobContext) {
	                ctx = ((SelfRemovalJobContext) ctx).getJobContext();
	            }
	            if (ctx instanceof ProcessJobContext) {
	                return ((ProcessJobContext)ctx).getSessionId();
	            }
        	}
            return -1;
        }

    }
    
    public static class DisposableCommandService implements CommandService {

        private CommandService delegate;
        private RuntimeManager manager;
        private RuntimeEngine runtime;
        private boolean retry = false;
        
        
        public DisposableCommandService(CommandService delegate, RuntimeManager manager, RuntimeEngine runtime, boolean retry) {
            this.delegate = delegate;
            this.manager = manager;
            this.runtime = runtime;
            this.retry = retry;
        }

        @Override
        public <T> T execute(Command<T> command) {
        	try {
        		if (delegate == null) {
        			return runtime.getKieSession().execute(command);
        		}
        		return delegate.execute(command);
        	} catch (RuntimeException e) {
        		if (retry) {
        			return delegate.execute(command);
        		} else {
        			throw e;
        		}
        	}
        }

        @Override
        public Context getContext() {
            return delegate.getContext();
        }
        
        public void dispose() {
            manager.disposeRuntimeEngine(runtime);
        }
        
        public Environment getEnvironment() {
        	
        	return runtime.getKieSession().getEnvironment();
        }
        
    }

}
