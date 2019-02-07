/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.timer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CronTrigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.ProtobufProcessMarshaller;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.event.SignalManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class TimerManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerManager.class);

    private long timerId = 0;

    private TimerManagerRuntime runtime;
    private TimerService timerService;
    private Map<Long, TimerInstance> timers = new ConcurrentHashMap<Long, TimerInstance>();
    public static final Job processJob = new ProcessJob();
    public static final Job startProcessJob = new StartProcessJob();

    public TimerManager(TimerManagerRuntime runtime, TimerService timerService) {
        this.runtime = runtime;
        this.timerService = timerService;
    }

    public void registerTimer(final TimerInstance timer, ProcessInstance processInstance) {
        try {
            runtime.startOperation();

            timer.setId(++timerId);
            timer.setProcessInstanceId(processInstance.getId());
            timer.setSessionId(((KieSession) runtime).getIdentifier());
            timer.setActivated(new Date());
            
            Trigger trigger = null;
            
            if (timer.getCronExpression() != null) {
                Date startTime = new Date(timerService.getCurrentTime() + 1000);
                trigger = new CronTrigger(timerService.getCurrentTime(), startTime, null, -1, timer.getCronExpression(), null, null);
                // cron timers are by nature repeatable
                timer.setPeriod(1);
            } else {
            	trigger = new IntervalTrigger(timerService.getCurrentTime(), null, null, timer.getRepeatLimit(),
                    timer.getDelay(), timer.getPeriod(), null, null);
            }
            ProcessJobContext ctx = new ProcessJobContext(timer, trigger, processInstance.getId(), this.runtime);

            JobHandle jobHandle = this.timerService.scheduleJob(processJob, ctx, trigger);

            timer.setJobHandle(jobHandle);
            timers.put(timer.getId(), timer);
        } finally {
            runtime.endOperation();
        }
    }

    public void registerTimer(final TimerInstance timer, String processId, Map<String, Object> params) {
        try {
            runtime.startOperation();

            timer.setId(++timerId);
            timer.setProcessInstanceId(-1l);
            timer.setSessionId(((StatefulKnowledgeSession) runtime).getIdentifier());
            timer.setActivated(new Date());

            Trigger trigger = null;

            if (timer.getCronExpression() != null) {
                Date startTime = new Date(timerService.getCurrentTime() + 1000);
                trigger = new CronTrigger(timerService.getCurrentTime(), startTime, null, -1, timer.getCronExpression(), null, null);
                // cron timers are by nature repeatable
                timer.setPeriod(1);
            } else {
                trigger = new IntervalTrigger(timerService.getCurrentTime(), null, null, timer.getRepeatLimit(), timer.getDelay(),
                        timer.getPeriod(), null, null);
            }
            StartProcessJobContext ctx = new StartProcessJobContext(timer, trigger, processId, params, this.runtime);

            JobHandle jobHandle = this.timerService.scheduleJob(startProcessJob, ctx, trigger);

            timer.setJobHandle(jobHandle);
            timers.put(timer.getId(), timer);
        } finally {
            runtime.endOperation();
        }
    }

    public void internalAddTimer(final TimerInstance timer) {
        long delay;
        Date lastTriggered = timer.getLastTriggered();
        if (lastTriggered == null) {
            Date activated = timer.getActivated();
            Date now = new Date();
            long timespan = now.getTime() - activated.getTime();
            delay = timer.getDelay() - timespan;
            if (delay < 0) {
                delay = 0;
            }
        } else {
            Date now = new Date();
            long timespan = now.getTime() - lastTriggered.getTime();
            delay = timespan - timer.getPeriod();
            if (delay < 0) {
                delay = 0;
            }
        }
        Trigger trigger = new IntervalTrigger(timerService.getCurrentTime(), null, null, -1, delay, timer.getPeriod(), null, null);
        ProcessJobContext ctx = new ProcessJobContext(timer, trigger, timer.getProcessInstanceId(), this.runtime);

        JobHandle jobHandle = this.timerService.scheduleJob(processJob, ctx, trigger);
        timer.setJobHandle(jobHandle);
        timers.put(timer.getId(), timer);
    }

    public void cancelTimer(long timerId) {
		try {
			runtime.startOperation();

			TimerInstance timer = timers.remove(timerId);
			if (timer != null) {
				timerService.removeJob(timer.getJobHandle());
			}
		} finally {
			runtime.endOperation();
		}
    }

    public void dispose() {
        // for ( TimerInstance timer : timers.values() ) {
        // timerService.removeJob( timer.getJobHandle() );
        // }
        for (Iterator<TimerInstance> it = timers.values().iterator(); it.hasNext();) {
            TimerInstance timer = it.next();
            timerService.removeJob(timer.getJobHandle());
            it.remove();
        }
        timerService.shutdown();
    }

    public TimerService getTimerService() {
        return this.timerService;
    }

    public Collection<TimerInstance> getTimers() {
        return timers.values();
    }

    public Map<Long, TimerInstance> getTimerMap() {
        return this.timers;
    }

    public long internalGetTimerId() {
        return timerId;
    }

    public void internalSetTimerId(long timerId) {
        this.timerId = timerId;
    }

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }

    public static class ProcessTimerOutputMarshaller implements TimersOutputMarshaller {
        
        public Timer serialize(JobContext jobCtx, MarshallerWriteContext outputCtx) {
            // do not store StartProcess timers as they are registered whenever session starts
            if (jobCtx instanceof StartProcessJobContext) {
                return null;
            }
            ProcessJobContext pctx = (ProcessJobContext) jobCtx;

            return ProtobufMessages.Timers.Timer
                    .newBuilder()
                    .setType(ProtobufMessages.Timers.TimerType.PROCESS)
                    .setExtension(
                            JBPMMessages.procTimer,
                            JBPMMessages.ProcessTimer.newBuilder()
                                    .setTimer(ProtobufProcessMarshaller.writeTimer(outputCtx, pctx.getTimer()))
                                    .setTrigger(ProtobufOutputMarshaller.writeTrigger(pctx.getTrigger(), outputCtx)).build())
                    .build();
        }
    }

    public static class ProcessTimerInputMarshaller implements TimersInputMarshaller {

        public void deserialize(MarshallerReaderContext inCtx, Timer timer) throws ClassNotFoundException {
            JBPMMessages.ProcessTimer ptimer = timer.getExtension(JBPMMessages.procTimer);

            TimerService ts = inCtx.wm.getTimerService();

            long processInstanceId = ptimer.getTimer().getProcessInstanceId();

            Trigger trigger = ProtobufInputMarshaller.readTrigger(inCtx, ptimer.getTrigger());

            TimerInstance timerInstance = ProtobufProcessMarshaller.readTimer(inCtx, ptimer.getTimer());

            TimerManager tm = ((InternalProcessRuntime) inCtx.wm.getProcessRuntime()).getTimerManager();

            // check if the timer instance is not already registered to avoid duplicated timers
            if (!tm.getTimerMap().containsKey(timerInstance.getId())) {
                TimerManagerRuntimeAdaptor rt = new TimerManagerRuntimeAdaptor(inCtx.wm.getKnowledgeRuntime());
                ProcessJobContext pctx = new ProcessJobContext(timerInstance, trigger, processInstanceId,
                                                               rt, false);
                Date date = trigger.hasNextFireTime();

                if (date != null) {
                    long then = date.getTime();
                    long now = pctx.getKnowledgeRuntime().getSessionClock().getCurrentTime();
                    // overdue timer                    
                    if (then < now) {
                        trigger = new OverdueTrigger(trigger, pctx.getKnowledgeRuntime());
                    }
                }
                JobHandle jobHandle = ts.scheduleJob(processJob, pctx, trigger);
                timerInstance.setJobHandle(jobHandle);
                pctx.setJobHandle(jobHandle);

                tm.getTimerMap().put(timerInstance.getId(), timerInstance);
            }
        }
    }

    public static class ProcessJob implements Job, Serializable {

        private static final long serialVersionUID = 6004839244692770390L;

        public void execute(JobContext c) {

            ProcessJobContext ctx = (ProcessJobContext) c;

            Long processInstanceId = ctx.getProcessInstanceId();
            TimerManagerRuntime runtime = ctx.getKnowledgeRuntime();
            try {
                runtime.startOperation();
                if (processInstanceId == null) {
                    throw new IllegalArgumentException("Could not find process instance for timer ");
                }

                ctx.getTimer().setLastTriggered(
                        new Date(ctx.getKnowledgeRuntime().<SessionClock> getSessionClock().getCurrentTime()));

                
                // if there is no more trigger reset period on timer so its node instance can be removed
                if (ctx.getTrigger().hasNextFireTime() == null) {
                    ctx.getTimer().setPeriod(0);
                }
                
                runtime.getSignalManager().signalEvent(processInstanceId,
                        "timerTriggered", ctx.getTimer());

                TimerManager tm = runtime.getTimerManager();

                if (ctx.getTimer().getPeriod() == 0) {
                    tm.getTimerMap().remove(ctx.getTimer().getId());
                    tm.getTimerService().removeJob(ctx.getJobHandle());
                }

            } catch (Throwable e) {
                logger.error("Error when executing timer job", e);
                throw new RuntimeException(e);
            } finally {
                runtime.endOperation();
            }
        }

    }

    public static class StartProcessJob implements Job, Serializable {

        private static final long serialVersionUID = 1039445333595469160L;

        public void execute(JobContext c) {

            StartProcessJobContext ctx = (StartProcessJobContext) c;

            TimerManagerRuntime runtime = ctx.getKnowledgeRuntime();
            TimerManager tm = runtime.getTimerManager();
            
            if (!runtime.isActive()) {
                logger.debug("Timer for starting process {} is ignored as the deployment is in deactivated state", ctx.getProcessId());
                tm.getTimerMap().remove(ctx.getTimer().getId());
                tm.getTimerService().removeJob(ctx.getJobHandle());
                
                return;
            }
            try {
                runtime.startOperation();
                ctx.getTimer().setLastTriggered(
                        new Date(ctx.getKnowledgeRuntime().<SessionClock> getSessionClock().getCurrentTime()));

                // if there is no more trigger reset period on timer so its node instance can be removed
                if (ctx.getTrigger().hasNextFireTime() == null) {
                    ctx.getTimer().setPeriod(0);
                }
                runtime.startProcess(ctx.getProcessId(), ctx.getParamaeters(), "timer");

                if (ctx.getTimer().getPeriod() == 0) {
                    tm.getTimerMap().remove(ctx.getTimer().getId());
                    tm.getTimerService().removeJob(ctx.getJobHandle());
                }

            } catch (Throwable e) {
                logger.error("Error when executing start process " + ctx.getProcessId() + " timer job", e);

            } finally {
                runtime.endOperation();
            }
        }

    }

    public static class ProcessJobContext implements JobContext {
        private static final long serialVersionUID = 476843895176221627L;

        private Long processInstanceId;
        private transient TimerManagerRuntime runtime;
        private TimerInstance timer;
        private Trigger trigger;

        private JobHandle jobHandle;
        private Long sessionId;
        
        private boolean newTimer;

        public ProcessJobContext(final TimerInstance timer, final Trigger trigger, final Long processInstanceId,
                final TimerManagerRuntime runtime) {
            this.timer = timer;
            this.trigger = trigger;
            this.processInstanceId = processInstanceId;
            this.runtime = runtime;
            this.sessionId = timer.getSessionId();
            this.newTimer = true;
        }
        
        public ProcessJobContext(final TimerInstance timer, final Trigger trigger, final Long processInstanceId,
                                 final TimerManagerRuntime runtime, boolean newTimer) {
            this.timer = timer;
            this.trigger = trigger;
            this.processInstanceId = processInstanceId;
            this.runtime = runtime;
            this.sessionId = timer.getSessionId();
            this.newTimer = newTimer;
        }

        public Long getProcessInstanceId() {
            return processInstanceId;
        }

        public TimerManagerRuntime getKnowledgeRuntime() {
            return runtime;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public TimerInstance getTimer() {
            return timer;
        }

        public Long getSessionId() {
            return sessionId;
        }

        public void setKnowledgeRuntime(TimerManagerRuntime runtime) {
            this.runtime = runtime;
        }

        @Override
        public InternalWorkingMemory getWorkingMemory() {
            return runtime instanceof InternalWorkingMemory ? (InternalWorkingMemory)runtime : null;
        }
        
        public boolean isNewTimer() {
            return newTimer;
        }
    }

    public static class StartProcessJobContext extends ProcessJobContext {

        private static final long serialVersionUID = -5219141659893424294L;
        private String processId;
        private Map<String, Object> paramaeters;

        public StartProcessJobContext(TimerInstance timer, Trigger trigger, String processId, Map<String, Object> params,
                TimerManagerRuntime runtime) {
            super(timer, trigger, null, runtime);
            this.processId = processId;
            this.paramaeters = params;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public Map<String, Object> getParamaeters() {
            return paramaeters;
        }

        public void setParamaeters(Map<String, Object> paramaeters) {
            this.paramaeters = paramaeters;
        }

        @Override
        public boolean isNewTimer() {
            return false;
        }

    }

    /**
     * Overdue aware trigger that introduces fixed delay to allow completion of session initialization
     * 
     */
    public static class OverdueTrigger implements Trigger {

        private static final long serialVersionUID = -2368476147776308013L;

        public static final long OVERDUE_DELAY = Long.parseLong(System.getProperty("jbpm.overdue.timer.delay", "2000"));

        private Trigger orig;
        private TimerManagerRuntime runtime;

        public OverdueTrigger(Trigger orig, TimerManagerRuntime runtime) {
            this.orig = orig;
            this.runtime = runtime;
        }

        public Date hasNextFireTime() {
            Date date = orig.hasNextFireTime();
            if (date == null) {
                return null;
            }
            long then = date.getTime();
            long now = runtime.getSessionClock().getCurrentTime();
            // overdue timer
            if (then < now) {
                return new Date((now + OVERDUE_DELAY));
            } else {
                return orig.hasNextFireTime();
            }
        }

        public Date nextFireTime() {
            return orig.nextFireTime();
        }

    }

}
