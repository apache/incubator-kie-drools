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

package org.drools.reteoo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.concurrent.ExternalExecutorService;
import org.drools.reteoo.PartitionTaskManager.Action;
import org.drools.reteoo.PartitionTaskManager.PartitionTask;

/**
 * A manager class for all partition tasks managers.
 * 
 * The purpose of this class is to keep the reference to all individual partition task managers 
 * and centralise the synchronisation mechanism between the network and the agenda.
 */
public class PartitionManager {

    // these are the actual task managers for each partition
    private Map<RuleBasePartitionId, PartitionTaskManager> partitionManagers;
    private InternalWorkingMemory                          workingMemory;
    // this is the actual thread pool
    private AtomicReference<ExternalExecutorService>       executorService;

    // this is a queue that holds new tasks whenever the actual task submission is on hold
    private PriorityBlockingQueue<PartitionTask>           queue;
    // a boolean flag and monitor lock for holding the task queue
    private AtomicBoolean                                  onHold;

    public PartitionManager(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.executorService = new AtomicReference<ExternalExecutorService>();
        this.partitionManagers = new ConcurrentHashMap<RuleBasePartitionId, PartitionTaskManager>();
        this.queue = new PriorityBlockingQueue<PartitionTask>();
        this.onHold = new AtomicBoolean( false );
    }

    /**
     * Add partition to the list of managed partitions
     * 
     * @param partitionId
     */
    public void manage(RuleBasePartitionId partitionId) {
        if ( !this.partitionManagers.containsKey( partitionId ) ) {
            this.partitionManagers.put( partitionId,
                                        new PartitionTaskManager( this,
                                                                  workingMemory ) );
        }
    }

    public void setPool(final ExternalExecutorService executorService) {
        // any monitor/lock could be used... using onHold so that it is obvious
        synchronized ( onHold ) {
            this.executorService.set( executorService );
            while( this.executorService.get() != null && ! queue.isEmpty() ) {
                PartitionTask task = queue.poll();
                this.executorService.get().execute( task );
            }
        }
    }

    public void execute(PartitionTask task) {
        // any monitor/lock could be used... using onHold so that it is obvious
        synchronized ( onHold ) {
            if ( (task.getPriority() < Action.PRIORITY_HIGH && onHold.get()) || this.executorService.get() == null ) {
                this.queue.add( task );
            } else {
                this.executorService.get().execute( task );
            }
        }
    }

    public void holdTasks() {
        // any monitor/lock could be used... using onHold so that it is obvious
        synchronized ( onHold ) {
            this.onHold.set( true );
        }
    }

    public void waitForPendingTasks() {
        this.executorService.get().waitUntilEmpty();
    }

    public void releaseTasks() {
        // any monitor/lock could be used... using onHold so that it is obvious
        synchronized ( onHold ) {
            ExecutorService service = this.executorService.get();
            if ( service != null ) {
                for ( PartitionTask task : queue ) {
                    service.execute( task );
                }
            }
            this.onHold.set( false );
        }
    }

    public void shutdown() {
        this.setPool( null );
    }

    public PartitionTaskManager getPartitionTaskManager(RuleBasePartitionId partitionId) {
        return partitionManagers.get( partitionId );
    }

    public boolean isOnHold() {
        return this.onHold.get();
    }

}
