/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.util.LinkedList;

import java.util.concurrent.atomic.AtomicInteger;

public class EventFactHandle extends DefaultFactHandle implements Comparable<EventFactHandle> {

    private static final long serialVersionUID = 510l;

    static final String EVENT_FORMAT_VERSION = "5";

    private long              startTimestamp;
    private long              duration;
    private boolean           expired;
    private boolean           pendingRemoveFromStore;
    private long              activationsCount;
    private int               otnCount;

    private EventFactHandle   linkedFactHandle;

    private AtomicInteger     notExpiredPartitions;

    private final transient LinkedList<JobHandle> jobs = new LinkedList<JobHandle>();

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public EventFactHandle() {
        super();
        this.startTimestamp = 0;
        this.duration = 0;
    }

    /**
     * Creates a new event fact handle.
     *
     * @param id this event fact handle ID
     * @param object the event object encapsulated in this event fact handle
     * @param recency the recency of this event fact handle
     * @param timestamp the timestamp of the occurrence of this event
     * @param duration the duration of this event. May be 0 (zero) in case this is a primitive event.
     */
    public EventFactHandle(int id,
                           Object object,
                           long recency,
                           long timestamp,
                           long duration,
                           InternalWorkingMemoryEntryPoint wmEntryPoint ) {
        this( id, object, recency, timestamp, duration, wmEntryPoint, false );
    }

    public EventFactHandle(int id,
                           Object object,
                           long recency,
                           long timestamp,
                           long duration,
                           InternalWorkingMemoryEntryPoint wmEntryPoint,
                           boolean isTraitOrTraitable ) {
        super( id,
               object,
               recency,
               wmEntryPoint,
               isTraitOrTraitable );
        this.startTimestamp = timestamp;
        this.duration = duration;

        if ( wmEntryPoint.getKnowledgeBase() != null && wmEntryPoint.getKnowledgeBase().getConfiguration().isMultithreadEvaluation() ) {
            notExpiredPartitions = new AtomicInteger( RuleBasePartitionId.PARALLEL_PARTITIONS_NUMBER );
        }
    }

    protected String getFormatVersion() {
        return EVENT_FORMAT_VERSION;
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    /**
     * Always returns true, since the EventFactHandle is
     * only used for Events, and not for regular Facts
     */
    public boolean isEvent() {
        return true;
    }

    /**
     * Returns the timestamp of the occurrence of this event.
     * @return
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Returns the duration of this event. In case this is a primitive event,
     * returns 0 (zero).
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the end timestamp for this event. This is the same as:
     *
     * startTimestamp + duration
     *
     * @return
     */
    public long getEndTimestamp() {
        return this.startTimestamp + this.duration;
    }

    public EventFactHandle getLinkedFactHandle() {
        return linkedFactHandle;
    }

    @Override
    public void invalidate() {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.invalidate();
        }  else {
            super.invalidate();
        }
    }

    @Override
    public boolean isValid() {
        if ( linkedFactHandle != null ) {
            return linkedFactHandle.isValid();
        }  else {
            return super.isValid();
        }
    }

    @Override
    public boolean isExpired() {
        if ( linkedFactHandle != null ) {
            return linkedFactHandle.isExpired();
        }  else {
            return expired;
        }
    }

    public boolean expirePartition() {
        if ( linkedFactHandle != null ) {
            return linkedFactHandle.expirePartition();
        }  else {
            return notExpiredPartitions == null || notExpiredPartitions.decrementAndGet() == 0;
        }
    }

    public void setExpired(boolean expired) {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.setExpired(expired);
        }  else {
            this.expired = expired;
        }
    }

    public boolean isPendingRemoveFromStore() {
        if ( linkedFactHandle != null ) {
            return linkedFactHandle.isPendingRemoveFromStore();
        }  else {
            return pendingRemoveFromStore;
        }
    }

    public void setPendingRemoveFromStore(boolean pendingRemove) {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.setPendingRemoveFromStore(pendingRemove);
        }  else {
            this.pendingRemoveFromStore = pendingRemove;
        }
    }

    public long getActivationsCount() {
        if ( linkedFactHandle != null ) {
            return linkedFactHandle.getActivationsCount();
        } else {
            return activationsCount;
        }
    }
    
    public void setActivationsCount(long activationsCount) {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.setActivationsCount( activationsCount );
        }  else {
            this.activationsCount = activationsCount;
        }

    }

    public void increaseActivationsCount() {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.increaseActivationsCount();
        }  else {
            this.activationsCount++;
        }
    }

    public void decreaseActivationsCount() {
        if ( linkedFactHandle != null ) {
            linkedFactHandle.decreaseActivationsCount();
        }  else {
            this.activationsCount--;
        }
    }

    public void increaseOtnCount() {
        otnCount++;
    }

    public void decreaseOtnCount() {
        otnCount--;
    }

    public int getOtnCount() {
        return otnCount;
    }

    public void setOtnCount( int otnCount ) {
        this.otnCount = otnCount;
    }

    public EventFactHandle clone() {
        EventFactHandle clone = new EventFactHandle( getId(),
                                                      getObject(),
                                                      getRecency(),
                                                      getStartTimestamp(),
                                                      getDuration(),
                                                      getEntryPoint(),
                                                      isTraitOrTraitable() );
        clone.setActivationsCount( getActivationsCount() );
        clone.setOtnCount( getOtnCount() );
        clone.setExpired( isExpired() );
        clone.entryPoint = entryPoint;
        clone.setEqualityKey( getEqualityKey() );
        clone.linkedTuples = this.linkedTuples.clone();
        clone.setObjectHashCode(getObjectHashCode());
        return clone;
    }

    private EventFactHandle cloneWithoutTuples() {
        EventFactHandle clone = new EventFactHandle( getId(),
                                                     getObject(),
                                                     getRecency(),
                                                     getStartTimestamp(),
                                                     getDuration(),
                                                     getEntryPoint(),
                                                     isTraitOrTraitable() );
        clone.setActivationsCount( getActivationsCount() );
        clone.setOtnCount( getOtnCount() );
        clone.setExpired( isExpired() );
        clone.entryPoint = entryPoint;
        clone.setEqualityKey( getEqualityKey() );
        clone.setObjectHashCode(getObjectHashCode());
        return clone;
    }

    public EventFactHandle cloneAndLink() {
        EventFactHandle clone = cloneWithoutTuples();
        clone.linkedFactHandle = this;
        return clone;
    }

    public void quickCloneUpdate(DefaultFactHandle clone) {
        clone.setObject( getObject() );
        clone.setRecency( getRecency() );
        clone.setEqualityKey( getEqualityKey() );

        clone.setObjectHashCode( getObjectHashCode() );
        clone.setIdentityHashCode( getIdentityHashCode() );
        clone.setTraitType( getTraitType() );
        clone.setDisconnected( isDisconnected() );
        clone.setNegated( isNegated() );
    }

    public int compareTo(EventFactHandle e) {
        return (getStartTimestamp() < e.getStartTimestamp()) ? -1 : (getStartTimestamp() == e.getStartTimestamp() ? 0 : 1);
    }

    public void addJob(JobHandle job) {
        synchronized (jobs) {
            jobs.add(job);
        }
    }

    public void removeJob(JobHandle job) {
        synchronized (jobs) {
            // the job could have been already removed if the event has been just retracted
            // and then the unscheduleAllJobs method has been invoked concurrently
            if (jobs.contains(job)) {
                jobs.remove(job);
            }
        }
    }

    public void unscheduleAllJobs(InternalWorkingMemory workingMemory) {
        if (!jobs.isEmpty()) {
            synchronized (jobs) {
                TimerService clock = workingMemory.getTimerService();
                while ( !jobs.isEmpty() ) {
                    JobHandle job = jobs.removeFirst();
                    clock.removeJob(job);
                }
            }
        }
    }
}
