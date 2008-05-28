package org.drools.scheduler.impl.jdk;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.drools.scheduler.Job;
import org.drools.scheduler.JobContext;
import org.drools.scheduler.JobHandle;
import org.drools.scheduler.Scheduler;
import org.drools.scheduler.Trigger;

public class JDKScheduler
    implements
    Scheduler {
    private ScheduledThreadPoolExecutor scheduler;

    public JDKScheduler() {
        this( 3 );
    }

    public JDKScheduler(int size) {
        this.scheduler = new ScheduledThreadPoolExecutor( size );
    }

    public JobHandle scheduleJob(Job job,
                                 JobContext ctx,
                                 Trigger trigger) {
        JDKJobHandle jobHandle = new JDKJobHandle();

        Date date = trigger.getNextFireTime();

        if ( date != null ) {
            JDKCallableJob callableJob = new JDKCallableJob( job,
                                                             ctx,
                                                             trigger,
                                                             jobHandle,
                                                             this.scheduler );
            ScheduledFuture future = schedule( date,
                                               callableJob,
                                               this.scheduler );
            jobHandle.setFuture( future );

            return jobHandle;
        } else {
            return null;
        }
    }

    public boolean removeJob(JobHandle jobHandle) {
        return this.scheduler.remove( (RunnableScheduledFuture) ((JDKJobHandle) jobHandle).getFuture() );
    }

    public static class JDKCallableJob
        implements
        Callable {
        private Job                         job;
        private Trigger                     trigger;
        private JobContext                  ctx;
        private ScheduledThreadPoolExecutor scheduler;
        private JDKJobHandle                handle;

        public JDKCallableJob(Job job,
                              JobContext ctx,
                              Trigger trigger,
                              JDKJobHandle handle,
                              ScheduledThreadPoolExecutor scheduler) {
            this.job = job;
            this.ctx = ctx;
            this.trigger = trigger;
            this.handle = handle;
            this.scheduler = scheduler;
        }

        public Object call() throws Exception {
            this.job.execute( this.ctx );

            // our triggers allow for flexible rescheduling
            Date date = this.trigger.getNextFireTime();
            ScheduledFuture future = schedule( date,
                                               this,
                                               this.scheduler );
            this.handle.setFuture( future );

            return null;
        }
    }

    public static class JDKJobHandle
        implements
        JobHandle {
        private ScheduledFuture future;

        public JDKJobHandle() {

        }

        public ScheduledFuture getFuture() {
            return future;
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

    }

    private static ScheduledFuture schedule(Date date,
                                            JDKCallableJob callableJob,
                                            ScheduledThreadPoolExecutor scheduler) {
        long then = date.getTime();
        long now = System.currentTimeMillis();
        ScheduledFuture future = null;
        if ( then >= now ) {
            future = scheduler.schedule( callableJob,
                                         then - now,
                                         TimeUnit.MILLISECONDS );
        } else {
            future = scheduler.schedule( callableJob,
                                         0,
                                         TimeUnit.MILLISECONDS );
        }
        return future;
    }

}
