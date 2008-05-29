package org.drools.process.instance.timer;

import java.util.Date;

import org.drools.WorkingMemory;
import org.drools.process.core.timer.Timer;
import org.drools.process.instance.ProcessInstance;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.SchedulerFactory;
import org.drools.time.Trigger;

public class TimerManager {
    private long                        timerId    = 0;

    private WorkingMemory               workingMemory;
    private TimerService                   scheduler;

    public TimerManager(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.scheduler = SchedulerFactory.getScheduler();
    }

    public void registerTimer(final Timer timer,
                              ProcessInstance processInstance) {
        timer.setId( ++timerId );

        ProcessJobContext ctx = new ProcessJobContext( timer,
                                                       processInstance.getId(),
                                                       this.workingMemory );

        JobHandle jobHandle = this.scheduler.scheduleJob( ProcessJob.instance,
                                                          ctx,
                                                          new TimerTrigger( timer.getDelay(),
                                                                            timer.getPeriod() ) );
        timer.setJobHandle( jobHandle );
    }

    public void cancelTimer(Timer timer) {
        scheduler.removeJob( timer.getJobHandle() );
    }

    public static class ProcessJob
        implements
        Job {
        public final static ProcessJob instance = new ProcessJob();

        public void execute(JobContext c) {
            ProcessJobContext ctx = (ProcessJobContext) c;

            Long processInstanceId = ctx.getProcessInstanceId();
            WorkingMemory workingMemory = ctx.getWorkingMemory();

            if ( processInstanceId == null ) {
                throw new IllegalArgumentException( "Could not find process instance for timer " );
            }

            ProcessInstance processInstance = workingMemory.getProcessInstance( processInstanceId );
            // process instance may have finished already
            if ( processInstance != null ) {
                processInstance.timerTriggered( ctx.getTimer() );
            }
        }

    }

    public static class TimerTrigger
        implements
        Trigger {
        private long delay;
        private long period;
        private int  count;

        public TimerTrigger(long delay,
                            long period) {
            this.delay = delay;
            this.period = period;
        }

        public Date getNextFireTime() {
            Date date = null;
            if ( delay == 0 ) {
                if ( count == 0 ) {
                    date = new Date( System.currentTimeMillis() + this.period );
                }
            } else if ( count == 0 ) {
                date = new Date( System.currentTimeMillis() + this.delay + this.period );
            } else {
                date = new Date( System.currentTimeMillis() + this.period );
            }
            count++;
            return date;
        }

    }

    public static class ProcessJobContext
        implements
        JobContext {
        private Long          processInstanceId;
        private WorkingMemory workingMemory;
        private Timer         timer;

        private JobHandle     jobHandle;

        public ProcessJobContext(final Timer timer,
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

        public Timer getTimer() {
            return timer;
        }

    }

}
