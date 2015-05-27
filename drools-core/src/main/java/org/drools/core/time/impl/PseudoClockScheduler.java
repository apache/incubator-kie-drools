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

package org.drools.core.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PseudoClockScheduler is a scheduler based on a user controlled clock 
 * that allows the user to explicitly control current time.
 */
public class PseudoClockScheduler
    implements
    TimerService,
    SessionPseudoClock,
    Externalizable,
    InternalSchedulerService,
    AcceptsTimerJobFactoryManager {
    
    private Logger logger = LoggerFactory.getLogger( PseudoClockScheduler.class ); 

    private AtomicLong                      timer;
    private PriorityBlockingQueue<Callable<Void>>   queue;
    private transient InternalWorkingMemory session;

    private TimerJobFactoryManager          jobFactoryManager = DefaultTimerJobFactoryManager.instance;

    private AtomicLong                      idCounter         = new AtomicLong();

    public PseudoClockScheduler() {
        this( null );
    }

    public PseudoClockScheduler(final InternalWorkingMemory session) {
        this.timer = new AtomicLong(0);
        this.queue = new PriorityBlockingQueue<Callable<Void>>();
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        timer = new AtomicLong( in.readLong() );
        final PriorityBlockingQueue<Callable<Void>> tmp = (PriorityBlockingQueue<Callable<Void>>) in.readObject();
        if ( tmp != null ) {
            queue = tmp;
        }
        session = ((DroolsObjectInputStream) in).getWorkingMemory();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong( timer.get() );
        // this is a work around to a bug in the object stream code, where it raises exceptions
        // when trying to de-serialize an empty priority queue.
        out.writeObject( queue.isEmpty() ? null : queue );
    }

    @Override
    public void setTimerJobFactoryManager(final TimerJobFactoryManager timerJobFactoryManager) {
        this.jobFactoryManager = timerJobFactoryManager;
    }

    @Override
    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return this.jobFactoryManager;
    }    

    /**
     * @inheritDoc
     * 
     * @see org.kie.api.time.SessionClock#getCurrentTime()
     */
    @Override
    public long getCurrentTime() {
        return this.timer.get();
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.core.time.TimerService#scheduleJob(Job, JobContext, Trigger)
     */
    @Override
    public JobHandle scheduleJob(final Job job,
                                 final JobContext ctx,
                                 final Trigger trigger) {

        final Date date = trigger.hasNextFireTime();

        if ( date != null ) {
            DefaultJobHandle jobHandle = new DefaultJobHandle( idCounter.getAndIncrement() );
            TimerJobInstance jobInstance = jobFactoryManager.createTimerJobInstance( job,
                                                                                   ctx,
                                                                                   trigger,
                                                                                   jobHandle,
                                                                                   this );
            jobHandle.setTimerJobInstance( (TimerJobInstance) jobInstance );
            internalSchedule( jobInstance );

            return jobHandle;
        }

        return null;
    }

    @Override
    public void internalSchedule(final TimerJobInstance timerJobInstance) {
        jobFactoryManager.addTimerJobInstance(timerJobInstance);
        synchronized(queue) {
            queue.add( ( Callable<Void> ) timerJobInstance );
        }
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.core.time.TimerService#removeJob(JobHandle)
     */
    @Override
    public boolean removeJob(final JobHandle jobHandle) {
        jobHandle.setCancel( true );
        jobFactoryManager.removeTimerJobInstance( ((DefaultJobHandle) jobHandle).getTimerJobInstance() );
        synchronized( queue ) {
            return this.queue.remove( ((DefaultJobHandle) jobHandle).getTimerJobInstance() );
        }
    }

    @Override
    public long advanceTime(final long amount,
                            final TimeUnit unit) {
        return this.runCallBacksAndIncreaseTimer( unit.toMillis( amount ) );
    }

    @Override
    public long advanceTime(final long amount,
                            final TimeUnit unit,
                            final int numberOfTimes) {
        final int numberOfSteps = Math.abs(numberOfTimes);
        // If steps == 0, the result is current time.
        long resultTime = getCurrentTime();
        for (int i = 0; i < numberOfSteps; i++) {
            // If the time is moving backwards...
            if (numberOfTimes < 0) {
                resultTime = this.runCallBacksAndIncreaseTimer( unit.toMillis( -amount ) );
            } else {
                resultTime = this.runCallBacksAndIncreaseTimer( unit.toMillis( amount ) );
            }
        }
        return resultTime;
    }

    public void setStartupTime(final long i) {
        this.timer.set( i );
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
    public synchronized void setSession(final InternalWorkingMemory session) {
        this.session = session;
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    @SuppressWarnings("unchecked")
    private synchronized long runCallBacksAndIncreaseTimer( final long increase ) {
        final long endTime = this.timer.get() + increase;
        TimerJobInstance item = (TimerJobInstance) queue.peek();
        long fireTime;
        while ( item != null && ((item.getTrigger().hasNextFireTime() != null && ( ( fireTime = item.getTrigger().hasNextFireTime().getTime()) <= endTime ) ) )  ) {
            // remove the head
            synchronized( queue ) {
                queue.remove(item);
            }

            if ( item.getJobHandle().isCancel() ) {
                // do not call it, do not reschedule it
                continue;
            }
            
            try {
                // set the clock back to the trigger's fire time
                this.timer.getAndSet( fireTime );
                // execute the call
                ((Callable<Void>) item).call();
            } catch ( Exception e ) {
                logger.error( "Exception running callbacks: ", e );
            }
            // get next head
            synchronized( queue ) {
                item = (TimerJobInstance) queue.peek();
            }
        }
        this.timer.set( endTime );
        return this.timer.get(); 
    }

    @Override
    public long getTimeToNextJob() {
        synchronized( queue ) {
            final TimerJobInstance item = (TimerJobInstance) queue.peek();
            return (item != null) ? item.getTrigger().hasNextFireTime().getTime() - this.timer.get() : -1;
        }
    }

    @Override
    public Collection<TimerJobInstance> getTimerJobInstances(long id) {
        return jobFactoryManager.getTimerJobInstances();
    }
}
