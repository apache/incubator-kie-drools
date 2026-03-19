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
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session-scoped collector that aggregates per-rule and session-level metrics.
 * <p>
 * This collector is thread-safe and designed to be attached to a single
 * {@link org.kie.api.runtime.KieSession} via the
 * {@link org.drools.metric.profiling.MetricAgendaEventListener}.
 * <p>
 * When Micrometer is on the classpath and metric logging is enabled,
 * it publishes the following meters:
 * <ul>
 *   <li>{@code org.drools.metric.rules.fired} — Counter per rule</li>
 *   <li>{@code org.drools.metric.session.firing.time} — Timer</li>
 *   <li>{@code org.drools.metric.session.fact.count} — Gauge</li>
 * </ul>
 */
public class SessionMetricCollector {

    private static final Logger logger = LoggerFactory.getLogger(SessionMetricCollector.class);

    private final Instant sessionStartTime;
    private final ConcurrentHashMap<String, RuleMetricStats> ruleStats = new ConcurrentHashMap<>();
    private final LongAdder totalRulesFired = new LongAdder();
    private final LongAdder totalFiringTimeNanos = new LongAdder();
    private final LongAdder matchesCreated = new LongAdder();
    private final LongAdder matchesCancelled = new LongAdder();
    private volatile LongSupplier factCountSupplier;

    private final boolean micrometerAvailable;
    private Object micrometerBridge; // SessionMicrometerBridge, loaded reflectively

    public SessionMetricCollector() {
        this.sessionStartTime = Instant.now();
        this.micrometerAvailable = isMicrometerAvailable();
        if (micrometerAvailable) {
            try {
                micrometerBridge = new SessionMicrometerBridge();
            } catch (Exception e) {
                logger.trace("Failed to initialize Micrometer bridge for session metrics.", e);
            }
        }
    }

    private static boolean isMicrometerAvailable() {
        try {
            Class.forName("io.micrometer.core.instrument.Tag");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Records a rule firing event.
     *
     * @param ruleName     the fully-qualified rule name
     * @param elapsedNanos the elapsed time in nanoseconds
     */
    public void recordRuleFiring(String ruleName, long elapsedNanos) {
        totalRulesFired.increment();
        totalFiringTimeNanos.add(elapsedNanos);

        RuleMetricStats stats = ruleStats.computeIfAbsent(ruleName, RuleMetricStats::new);
        stats.recordFiring(elapsedNanos);

        if (micrometerAvailable && micrometerBridge != null) {
            ((SessionMicrometerBridge) micrometerBridge).recordRuleFiring(ruleName, elapsedNanos);
        }
    }

    /** Increments the match-created counter. */
    public void incrementMatchesCreated() {
        matchesCreated.increment();
    }

    /** Increments the match-cancelled counter. */
    public void incrementMatchesCancelled() {
        matchesCancelled.increment();
    }

    /**
     * Sets a supplier for the current fact count in the session.
     * This is called by the listener when it's attached to a session.
     */
    public void setFactCountSupplier(LongSupplier supplier) {
        this.factCountSupplier = supplier;
        if (micrometerAvailable && micrometerBridge != null) {
            ((SessionMicrometerBridge) micrometerBridge).registerFactCountGauge(supplier);
        }
    }

    // ---- Accessors ----

    public Instant getSessionStartTime() {
        return sessionStartTime;
    }

    public Duration getSessionDuration() {
        return Duration.between(sessionStartTime, Instant.now());
    }

    public long getTotalRulesFired() {
        return totalRulesFired.sum();
    }

    public long getTotalFiringTimeNanos() {
        return totalFiringTimeNanos.sum();
    }

    public double getTotalFiringTimeMillis() {
        return totalFiringTimeNanos.sum() / 1_000_000.0;
    }

    public double getAverageFiringTimeMillis() {
        long count = totalRulesFired.sum();
        return count > 0 ? getTotalFiringTimeMillis() / count : 0.0;
    }

    public long getMatchesCreated() {
        return matchesCreated.sum();
    }

    public long getMatchesCancelled() {
        return matchesCancelled.sum();
    }

    public long getCurrentFactCount() {
        LongSupplier supplier = this.factCountSupplier;
        return supplier != null ? supplier.getAsLong() : -1;
    }

    /**
     * Returns per-rule stats for the given rule.
     *
     * @return the stats, or {@code null} if the rule was never fired
     */
    public RuleMetricStats getRuleStats(String ruleName) {
        return ruleStats.get(ruleName);
    }

    /** Returns an unmodifiable view of all per-rule stats. */
    public Map<String, RuleMetricStats> getAllRuleStats() {
        return Collections.unmodifiableMap(ruleStats);
    }

    /** Returns the distinct number of rules that have fired. */
    public int getDistinctRuleCount() {
        return ruleStats.size();
    }

    /**
     * Returns the top-N rules by total firing time, sorted descending.
     */
    public Collection<RuleMetricStats> getTopRulesByTime(int n) {
        return ruleStats.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalTimeNanos(), a.getTotalTimeNanos()))
                .limit(n)
                .toList();
    }

    /**
     * Returns the top-N rules by fire count, sorted descending.
     */
    public Collection<RuleMetricStats> getTopRulesByFireCount(int n) {
        return ruleStats.values().stream()
                .sorted((a, b) -> Long.compare(b.getFireCount(), a.getFireCount()))
                .limit(n)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("SessionMetricCollector[started=%s, rulesFired=%d, totalFiringMs=%.3f, distinctRules=%d, matchesCreated=%d, matchesCancelled=%d]",
                sessionStartTime, getTotalRulesFired(), getTotalFiringTimeMillis(),
                getDistinctRuleCount(), getMatchesCreated(), getMatchesCancelled());
    }
}
