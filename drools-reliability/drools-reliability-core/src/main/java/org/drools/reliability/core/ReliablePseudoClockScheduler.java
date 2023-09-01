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
package org.drools.reliability.core;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.drools.core.common.Storage;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeJob;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJob;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.time.impl.TimerJobInstance;

public class ReliablePseudoClockScheduler extends PseudoClockScheduler {

    private final transient Storage<String, Object> storage;
    private AtomicLong persistedTimer;

    public ReliablePseudoClockScheduler() {
        throw new UnsupportedOperationException("This constructor should not be used");
    }

    @SuppressWarnings("unchecked")
    public ReliablePseudoClockScheduler(Storage<String, Object> storage) {
        this.storage = storage;
        this.timer = new AtomicLong(0);
        this.persistedTimer = new AtomicLong((Long) storage.getOrDefault("timer", 0L));
        this.idCounter = new AtomicLong( (Long) storage.getOrDefault("idCounter", 0L) );
        List<TimerJobInstance> internalQueue = (List<TimerJobInstance>) storage.getOrDefault("internalQueue", new ArrayList<>());
        this.queue = new PriorityQueue<>(internalQueue);
    }

    public AtomicLong getPersistedTimer() {
        return persistedTimer;
    }

    @Override
    public long advanceTime(long amount, TimeUnit unit) {
        long time = super.advanceTime(amount, unit);
        updateStorage();
        return time;
    }

    private void updateStorage() {
        storage.put("timer", timer.get());
        storage.put("idCounter", idCounter.get());
        storage.put("internalQueue", createFilteredInternalQueueForPersistence(queue));
    }

    /**
     * ExpireJob and TimerNodeJob are recreated by repropagate, so we don't need to persist
     */
    public List<TimerJobInstance> createFilteredInternalQueueForPersistence(PriorityQueue<TimerJobInstance> queue) {
        return queue.stream()
                    .filter(job -> !(job.getJob() instanceof ExpireJob || job.getJob() instanceof TimerNodeJob))
                    .collect(Collectors.toList());
    }
}
