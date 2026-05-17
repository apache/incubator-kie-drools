/*
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
package org.drools.metric.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongSupplier;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;

/**
 * Bridge class that isolates all Micrometer references.
 * <p>
 * This class is only loaded when Micrometer is on the classpath,
 * keeping the dependency optional for {@link SessionMetricCollector}.
 */
final class SessionMicrometerBridge {

    private final MeterRegistry registry;
    private final Timer firingTimer;
    private final Map<String, Counter> ruleCounterCache = new ConcurrentHashMap<>();
    private final AtomicBoolean factGaugeRegistered = new AtomicBoolean(false);

    SessionMicrometerBridge() {
        this.registry = Metrics.globalRegistry;
        this.firingTimer = Timer.builder("org.drools.metric.session.firing.time")
                .description("Total time spent firing rules in this session")
                .register(registry);
    }

    void recordRuleFiring(String ruleName, long elapsedNanos) {
        // Record timer
        firingTimer.record(Duration.ofNanos(elapsedNanos));

        // Increment per-rule counter
        Counter counter = ruleCounterCache.computeIfAbsent(ruleName, name ->
                Counter.builder("org.drools.metric.rules.fired")
                        .description("Number of times a rule has fired")
                        .tag("rule", name)
                        .register(registry));
        counter.increment();
    }

    void registerFactCountGauge(LongSupplier factCountSupplier) {
        if (factGaugeRegistered.compareAndSet(false, true)) {
            Gauge.builder("org.drools.metric.session.fact.count", factCountSupplier::getAsLong)
                    .description("Current number of facts in the session")
                    .register(registry);
        }
    }
}
