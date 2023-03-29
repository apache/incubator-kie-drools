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

package org.drools.reliability;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.infinispan.Cache;

public class ReliablePseudoClockScheduler extends PseudoClockScheduler {

    private final Cache<String, Object> cache;

    public ReliablePseudoClockScheduler(Cache<String, Object> cache) {
        this.cache = cache;
        this.timer = new AtomicLong( (Long) cache.getOrDefault("timer", 0L) );
        this.idCounter = new AtomicLong( (Long) cache.getOrDefault("idCounter", 0L) );
        this.queue = (PriorityQueue) cache.getOrDefault("queue", new PriorityQueue<>());
    }

    @Override
    public long advanceTime(long amount, TimeUnit unit) {
        long time = super.advanceTime(amount, unit);
        updateCache();
        return time;
    }

    private void updateCache() {
        cache.put("timer", timer.get());
        cache.put("idCounter", idCounter.get());
        cache.put("queue", queue);
    }
}
