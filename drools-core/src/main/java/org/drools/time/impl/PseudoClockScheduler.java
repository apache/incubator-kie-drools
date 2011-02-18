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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.drools.SystemEventListenerFactory;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalWorkingMemory;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.SessionPseudoClock;
import org.drools.time.TimerService;
import org.drools.time.Trigger;

/**
 * A PseudoClockScheduler is a scheduler based on a user controlled clock 
 * that allows the user to explicitly control current time.
 */
public class PseudoClockScheduler
    implements
    TimerService,
    SessionPseudoClock,
    Externalizable {

    private volatile long                   timer;
    private PriorityQueue<ScheduledJob>     queue;
    private transient InternalWorkingMemory session;

    public PseudoClockScheduler() {
        this( null );
    }

    public PseudoClockScheduler(InternalWorkingMemory session) {
        this.timer = 0;
        this.queue = new PriorityQueue<ScheduledJob>();
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        timer = in.readLong();
        PriorityQueue<ScheduledJob> tmp = (PriorityQueue<ScheduledJob>) in.readObject();
        if ( tmp != null ) {
            queue = tmp;
        }
        session = ((DroolsObjectInputStream) in).getWorkingMemory();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( timer );
        // this is a work around to a bug in the object stream code, where it raises exceptions
        // when trying to de-serialize an empty priority queue.
        out.writeObject( queue.isEmpty() ? null : queue );
    }

    /**
     * @inheritDoc
     * 
     * @see org.drools.temporal.SessionClock#getCurrentTime()
     */
    public synchronized long getCurrentTime() {
        return this.timer;
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.time.TimerService#scheduleJob(org.drools.time.Job, org.drools.time.JobContext, org.drools.time.Trigger)
     */
    public JobHandle scheduleJob(Job job,
                                 JobContext ctx,
                                 Trigger trigger) {

        Date date = trigger.hasNextFireTime();

        if ( date != null ) {
            ScheduledJob callableJob = new ScheduledJob( job,
                                                         ctx,
                                                         trigger );
            queue.add( callableJob );
            return callableJob.getHandle();
        }

        return null;
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.time.TimerService#removeJob(org.drools.time.JobHandle)
     */
    public boolean removeJob(JobHandle jobHandle) {
        return this.queue.remove( ((DefaultJobHandle) jobHandle).getScheduledJob() );
    }

    /**
     * @inheritDoc
     */
    public synchronized long advanceTime(long amount,
                                         TimeUnit unit) {
        this.timer += unit.toMillis( amount );
        this.runCallBacks();
        return this.timer;
    }

    public synchronized void setStartupTime(long i) {
        this.timer = i;
    }

    /**
     * @return the session
     */
    public synchronized InternalWorkingMemory getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public synchronized void setSession(InternalWorkingMemory session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // nothing to do
    }

    private void runCallBacks() {
        ScheduledJob item = queue.peek();
        long fireTime;
        while ( item != null && ((fireTime = item.getTrigger().hasNextFireTime().getTime()) <= this.timer) ) {
            // remove the head
            queue.remove();
            
            // updates the trigger
            item.getTrigger().nextFireTime();
            
            if ( item.getTrigger().hasNextFireTime() != null ) {
                // reschedule for the next fire time, if one exists
                queue.add( item );
            }
            // save the current timer because we are going to override to to the job's trigger time
            long savedTimer = this.timer;
            try {
                // set the clock back to the trigger's fire time
                this.timer = fireTime;
                // execute the call
                item.call();
            } catch ( Exception e ) {
                SystemEventListenerFactory.getSystemEventListener().exception( e );
            } finally {
                this.timer = savedTimer;
            }
            // get next head
            item = queue.peek();
        }
    }

    public synchronized long getTimeToNextJob() {
        ScheduledJob item = queue.peek();
        return ( item != null ) ? item.getTrigger().hasNextFireTime().getTime() - this.timer : -1;
    }

    /**
     * An Scheduled Job class with all fields final to make it
     * multi-thread safe.
     */
    public static final class ScheduledJob
        implements
        Comparable<ScheduledJob>,
        Callable<Void>,
        Serializable {

        private static final long serialVersionUID = 510l;

        private final Job         job;
        private final Trigger     trigger;
        private final JobContext  ctx;

        /**
         * @param timestamp
         * @param behavior
         * @param behaviorContext 
         */
        public ScheduledJob(final Job job,
                            final JobContext context,
                            final Trigger trigger) {
            super();
            this.job = job;
            this.ctx = context;
            this.trigger = trigger;
        }

        public int compareTo(ScheduledJob o) {
            return this.trigger.hasNextFireTime().compareTo( o.getTrigger().hasNextFireTime() );
        }

        public Void call() throws Exception {
            this.job.execute( this.ctx );
            return null;
        }

        public Job getJob() {
            return job;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public JobContext getCtx() {
            return ctx;
        }

        public JobHandle getHandle() {
            return new DefaultJobHandle( this );
        }

        public String toString() {
            return "ScheduledJob( job=" + job + " trigger=" + trigger + " context=" + ctx + " )";
        }

    }

}
