/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reliability.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.time.impl.TimerJobInstance;

public class ReliablePseudoClockScheduler extends PseudoClockScheduler {

    private final transient Storage<String, Object> storage;
    private transient ReteEvaluator reteEvaluator;
    private transient Map<String, Map<Long, DefaultEventHandle>> eventHandleMap = new HashMap<>();

    public ReliablePseudoClockScheduler() {
        throw new UnsupportedOperationException("This constructor should not be used");
    }

    @SuppressWarnings("unchecked")
    public ReliablePseudoClockScheduler(Storage<String, Object> storage, ReteEvaluator reteEvaluator) {
        this.storage = storage;
        this.timer = new AtomicLong( (Long) storage.getOrDefault("timer", 0L) );
        this.idCounter = new AtomicLong( (Long) storage.getOrDefault("idCounter", 0L) );
        this.queue = (PriorityQueue) storage.getOrDefault("queue", new PriorityQueue<>());
        this.reteEvaluator = reteEvaluator;
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
        storage.put("queue", queue);
    }

    public void putHandleIdAssociation(long oldHandleId, DefaultEventHandle eFh) {
        String entryPointName = eFh.getEntryPointName();
        Map<Long, DefaultEventHandle> eventHandleMapPerEntryPoint = eventHandleMap.computeIfAbsent(entryPointName, key -> new HashMap<>());
        eventHandleMapPerEntryPoint.put(oldHandleId, eFh);
    }

    public void rewireTimerJobs() {
        List<TimerJobInstance> toBeRemoved = new ArrayList<>();
        queue.stream()
             .filter(DefaultTimerJobInstance.class::isInstance)
             .map(DefaultTimerJobInstance.class::cast)
             .forEach(job -> rewireJobContext(job, toBeRemoved));

        queue.removeAll(toBeRemoved);
    }

    private void rewireJobContext(DefaultTimerJobInstance jobInstance, List<TimerJobInstance> toBeRemoved) {
        JobContext jobContext = jobInstance.getJobContext();
        if (jobContext instanceof ExpireJobContext) {
            ExpireJobContext expireJobContext = (ExpireJobContext) jobContext;
            expireJobContext.setReteEvaluator(reteEvaluator);
            WorkingMemoryReteExpireAction expireAction = expireJobContext.getExpireAction();
            if (expireAction.getFactHandle().getObject() == null) {
                // rewire new eventHandle
                String entryPointName = expireAction.getFactHandle().getEntryPointName();
                long oldHandleId = expireAction.getFactHandle().getId();
                if (eventHandleMap.containsKey(entryPointName) && eventHandleMap.get(entryPointName).containsKey(oldHandleId)) {
                    expireAction.setFactHandle(eventHandleMap.get(entryPointName).get(oldHandleId));
                } else {
                    throw new ReliabilityRuntimeException("new handle to rewire is not found : entryPointName = " + entryPointName + ", oldHandleId = " + oldHandleId);
                }
            } else {
                // job created by re-propagation. Duplicate
                toBeRemoved.add(jobInstance);
            }
        }
        // TODO: deal with other JobContext
    }
}
