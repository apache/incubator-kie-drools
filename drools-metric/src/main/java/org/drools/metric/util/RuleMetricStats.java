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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Thread-safe per-rule performance statistics.
 * <p>
 * Tracks fire count, total/min/max firing time in nanoseconds,
 * and computes average on read. Uses {@link LongAdder} for the
 * high-contention fire count and {@link AtomicLong} for min/max
 * to avoid synchronization overhead.
 */
public class RuleMetricStats {

    private final String ruleName;
    private final LongAdder fireCount = new LongAdder();
    private final LongAdder totalTimeNanos = new LongAdder();
    private final AtomicLong minTimeNanos = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxTimeNanos = new AtomicLong(0);

    public RuleMetricStats(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * Records a single rule firing with the given elapsed time.
     *
     * @param elapsedNanos the elapsed time in nanoseconds
     */
    public void recordFiring(long elapsedNanos) {
        fireCount.increment();
        totalTimeNanos.add(elapsedNanos);
        updateMin(elapsedNanos);
        updateMax(elapsedNanos);
    }

    private void updateMin(long value) {
        long current;
        do {
            current = minTimeNanos.get();
            if (value >= current) {
                return;
            }
        } while (!minTimeNanos.compareAndSet(current, value));
    }

    private void updateMax(long value) {
        long current;
        do {
            current = maxTimeNanos.get();
            if (value <= current) {
                return;
            }
        } while (!maxTimeNanos.compareAndSet(current, value));
    }

    public String getRuleName() {
        return ruleName;
    }

    public long getFireCount() {
        return fireCount.sum();
    }

    public long getTotalTimeNanos() {
        return totalTimeNanos.sum();
    }

    /**
     * @return total firing time in microseconds
     */
    public long getTotalTimeMicros() {
        return totalTimeNanos.sum() / 1_000;
    }

    /**
     * @return total firing time in milliseconds
     */
    public double getTotalTimeMillis() {
        return totalTimeNanos.sum() / 1_000_000.0;
    }

    /**
     * @return minimum firing time in nanoseconds, or 0 if no firings recorded
     */
    public long getMinTimeNanos() {
        long count = fireCount.sum();
        return count > 0 ? minTimeNanos.get() : 0;
    }

    /**
     * @return maximum firing time in nanoseconds
     */
    public long getMaxTimeNanos() {
        return maxTimeNanos.get();
    }

    /**
     * @return average firing time in nanoseconds, or 0 if no firings recorded
     */
    public double getAvgTimeNanos() {
        long count = fireCount.sum();
        return count > 0 ? (double) totalTimeNanos.sum() / count : 0.0;
    }

    /**
     * @return average firing time in milliseconds
     */
    public double getAvgTimeMillis() {
        return getAvgTimeNanos() / 1_000_000.0;
    }

    @Override
    public String toString() {
        return String.format("RuleMetricStats[rule=%s, fireCount=%d, totalMs=%.3f, avgMs=%.3f, minNs=%d, maxNs=%d]",
                ruleName, getFireCount(), getTotalTimeMillis(), getAvgTimeMillis(), getMinTimeNanos(), getMaxTimeNanos());
    }
}
