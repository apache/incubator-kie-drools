package org.drools.process.instance.timer;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.impl.IntervalTrigger;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class TimerManager {
    private long                     timerId    = 0;

    private WorkingMemory            workingMemory;
    private TimerService             timerService;
    private Map<Long, TimerInstance> timers     = new HashMap<Long, TimerInstance>();
    private Job                      processJob = new ProcessJob();

    public TimerManager(WorkingMemory workingMemory,
                        TimerService timerService) {
        this.workingMemory = workingMemory;
        this.timerService = timerService;
    }

    public void registerTimer(final TimerInstance timer,
                              ProcessInstance processInstance) {
        timer.setId( ++timerId );
        timer.setProcessInstanceId( processInstance.getId() );
        timer.setActivated( new Date() );

        ProcessJobContext ctx = new ProcessJobContext( timer,
                                                       processInstance.getId(),
                                                       this.workingMemory );

        JobHandle jobHandle = this.timerService.scheduleJob( processJob,
                                                             ctx,
                                                             new IntervalTrigger( timerService.getCurrentTime(),
                                                                                  null,
                                                                                  null,
                                                                                  timer.getDelay(),
                                                                                  timer.getPeriod(),
                                                                                  null,
                                                                                  null ) );
        timer.setJobHandle( jobHandle );
        timers.put( timer.getId(),
                    timer );
    }

    public void internalAddTimer(final TimerInstance timer) {
        ProcessJobContext ctx = new ProcessJobContext( timer,
                                                       timer.getProcessInstanceId(),
                                                       this.workingMemory );

        long delay;
        Date lastTriggered = timer.getLastTriggered();
        if ( lastTriggered == null ) {
            Date activated = timer.getActivated();
            Date now = new Date();
            long timespan = now.getTime() - activated.getTime();
            delay = timer.getDelay() - timespan;
            if ( delay < 0 ) {
                delay = 0;
            }
        } else {
            Date now = new Date();
            long timespan = now.getTime() - lastTriggered.getTime();
            delay = timespan - timer.getPeriod();
            if ( delay < 0 ) {
                delay = 0;
            }
        }
        JobHandle jobHandle = this.timerService.scheduleJob( processJob,
                                                             ctx,
                                                             new IntervalTrigger( timerService.getCurrentTime(),
                                                                                  null,
                                                                                  null,
                                                                                  delay,
                                                                                  timer.getPeriod(),
                                                                                  null,
                                                                                  null ) );
        timer.setJobHandle( jobHandle );
        timers.put( timer.getId(),
                    timer );
    }

    public void cancelTimer(long timerId) {
        TimerInstance timer = timers.remove( timerId );
        if ( timer != null ) {
            timerService.removeJob( timer.getJobHandle() );
        }
    }

    public void dispose() {
        for ( TimerInstance timer : timers.values() ) {
            timerService.removeJob( timer.getJobHandle() );
        }
        timerService.shutdown();
    }

    public TimerService getTimerService() {
        return this.timerService;
    }

    public Collection<TimerInstance> getTimers() {
        return timers.values();
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

    public class ProcessJob
        implements
        Job {

        public void execute(JobContext c) {
            ProcessJobContext ctx = (ProcessJobContext) c;

            Long processInstanceId = ctx.getProcessInstanceId();
            WorkingMemory workingMemory = ctx.getWorkingMemory();

            if ( processInstanceId == null ) {
                throw new IllegalArgumentException( "Could not find process instance for timer " );
            }

            ctx.getTimer().setLastTriggered( new Date() );

            workingMemory.getSignalManager().signalEvent( processInstanceId,
                                                          "timerTriggered",
                                                          ctx.getTimer() );

            if ( ctx.getTimer().getPeriod() == 0 ) {
                TimerManager.this.timers.remove( ctx.getTimer().getId() );
            }
        }

    }

    public static class ProcessJobContext
        implements
        JobContext {
        private Long          processInstanceId;
        private WorkingMemory workingMemory;
        private TimerInstance timer;

        private JobHandle     jobHandle;

        public ProcessJobContext(final TimerInstance timer,
                                 final Long processInstanceId,
                                 final WorkingMemory workingMemory) {
            this.timer = timer;
            this.processInstanceId = processInstanceId;
            this.workingMemory = workingMemory;
        }

        public Long getProcessInstanceId() {
            return processInstanceId;
        }

        public WorkingMemory getWorkingMemory() {
            return workingMemory;
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

    }

}
