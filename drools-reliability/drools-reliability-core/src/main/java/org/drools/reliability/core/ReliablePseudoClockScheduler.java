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

import org.drools.core.time.impl.PseudoClockScheduler;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ReliablePseudoClockScheduler extends PseudoClockScheduler {

    private final Storage<String, Object> storage;

    @SuppressWarnings("unchecked")
    public ReliablePseudoClockScheduler(Storage<String, Object> storage) {
        this.storage = storage;
        this.timer = new AtomicLong( (Long) storage.getOrDefault("timer", 0L) );
        this.idCounter = new AtomicLong( (Long) storage.getOrDefault("idCounter", 0L) );
        this.queue = (PriorityQueue) storage.getOrDefault("queue", new PriorityQueue<>());
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
}
