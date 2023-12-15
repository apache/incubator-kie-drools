/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.time.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PseudoClockScheduler is a scheduler based on a user controlled clock 
 * that allows the user to explicitly control current time.
 */
public class PseudoClockScheduler implements TimerService, SessionPseudoClock, Externalizable, InternalSchedulerService {
    
    private final Logger logger = LoggerFactory.getLogger( PseudoClockScheduler.class );

    protected AtomicLong timer = new AtomicLong(0);

    protected PriorityQueue<TimerJobInstance> queue = new PriorityQueue<>();

    private TimerJobFactoryManager jobFactoryManager = DefaultTimerJobFactoryManager.INSTANCE;

    protected AtomicLong idCounter = new AtomicLong(0);

    private int cancelledJob = 0;

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        timer = new AtomicLong( in.readLong() );
        PriorityQueue<TimerJobInstance> tmp = (PriorityQueue<TimerJobInstance>) in.readObject();
        if ( tmp != null ) {
            queue = tmp;
        }
//        session = ((DroolsObjectInputStream) in).getWorkingMemory();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( timer.get() );
        // this is a work around to a bug in the object stream code, where it raises exceptions
        // when trying to de-serialize an empty priority queue.
        out.writeObject( queue.isEmpty() ? null : queue );
    }

    @Override
    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager) {
        this.jobFactoryManager = timerJobFactoryManager;
    }

    @Override
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return this.jobFactoryManager;
    }

    @Override
    public long getCurrentTime() {
        return this.timer.get();
    }

    @Override
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger) {
        Date date = trigger.hasNextFireTime();
        if ( date == null ){
            return null;
        }

        DefaultJobHandle jobHandle = new DefaultJobHandle( idCounter.getAndIncrement() );
        TimerJobInstance jobInstance = jobFactoryManager.createTimerJobInstance( job, ctx, trigger, jobHandle, this );
        jobHandle.setTimerJobInstance( jobInstance );
        internalSchedule( jobInstance );
        return jobHandle;
    }

    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        jobFactoryManager.addTimerJobInstance(timerJobInstance);
        synchronized (this) {
            queue.add( timerJobInstance );
        }
    }

    @Override
    public synchronized void removeJob(JobHandle jobHandle) {
        jobHandle.cancel();
        jobFactoryManager.removeTimerJobInstance(jobHandle);
        if ( ++cancelledJob > 1000 ) {
            purgeCancelledJob();
        }
    }

    @Override
    public long advanceTime(long amount, TimeUnit unit) {
        return this.runCallBacksAndIncreaseTimer( unit.toMillis( amount ) );
    }

    public void setStartupTime(long i) {
        this.timer.set( i );
    }

    @Override
    public synchronized void reset() {
        idCounter.set(0);
        timer.set(0);
        queue.clear();
        cancelledJob = 0;
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    @SuppressWarnings("unchecked")
    private synchronized long runCallBacksAndIncreaseTimer( long increase ) {
        long endTime = this.timer.get() + increase;
        TimerJobInstance item = peek();
        long fireTime;
        while (item != null && item.getTrigger().hasNextFireTime() != null && (fireTime = item.getTrigger().hasNextFireTime().getTime()) <= endTime) {
            // remove the head
            queue.poll();

            if ( !item.getJobHandle().isCancel() ) {
                try {
                    // set the clock back to the trigger's fire time
                    this.timer.getAndSet(fireTime);
                    // execute the call
                    ((Callable<Void>) item).call();
                } catch (Exception e) {
                    logger.error("Exception running callbacks: ", e);
                }
            }

            // get next head
            item = peek();
        }
        this.timer.set( endTime );
        return this.timer.get();
    }

    private TimerJobInstance peek() {
        TimerJobInstance peek = queue.peek();
        while (peek != null && peek.isCanceled()) {
            cancelledJob--;
            queue.poll();
            peek = queue.peek();
        }
        return peek;
    }

    private void purgeCancelledJob() {
        Iterator<TimerJobInstance> i = queue.iterator();
        while (i.hasNext()) {
            if (i.next().isCanceled()) {
                i.remove();
            }
        }
        cancelledJob = 0;
    }

    @Override
    public synchronized long getTimeToNextJob() {
        TimerJobInstance item = peek();
        return (item != null) ? item.getTrigger().hasNextFireTime().getTime() - this.timer.get() : -1;
    }

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances(long id) {
        return jobFactoryManager.getTimerJobInstances();
    }
}
