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
package org.drools.metric;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.metric.util.RuleMetricStats;
import org.drools.metric.util.SessionMetricCollector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SessionMetricCollector} and {@link RuleMetricStats}.
 */
public class SessionMetricCollectorTest {

    @Test
    public void testRecordSingleRuleFiring() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("default.Rule1", 500_000); // 0.5ms

        assertThat(collector.getTotalRulesFired()).isEqualTo(1);
        assertThat(collector.getDistinctRuleCount()).isEqualTo(1);

        RuleMetricStats stats = collector.getRuleStats("default.Rule1");
        assertThat(stats).isNotNull();
        assertThat(stats.getFireCount()).isEqualTo(1);
        assertThat(stats.getTotalTimeNanos()).isEqualTo(500_000);
        assertThat(stats.getMinTimeNanos()).isEqualTo(500_000);
        assertThat(stats.getMaxTimeNanos()).isEqualTo(500_000);
    }

    @Test
    public void testMultipleRulesTrackedIndependently() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("pkg.RuleA", 100_000);
        collector.recordRuleFiring("pkg.RuleA", 200_000);
        collector.recordRuleFiring("pkg.RuleB", 300_000);

        assertThat(collector.getTotalRulesFired()).isEqualTo(3);
        assertThat(collector.getDistinctRuleCount()).isEqualTo(2);

        RuleMetricStats statsA = collector.getRuleStats("pkg.RuleA");
        assertThat(statsA.getFireCount()).isEqualTo(2);
        assertThat(statsA.getTotalTimeNanos()).isEqualTo(300_000);
        assertThat(statsA.getMinTimeNanos()).isEqualTo(100_000);
        assertThat(statsA.getMaxTimeNanos()).isEqualTo(200_000);
        assertThat(statsA.getAvgTimeNanos()).isEqualTo(150_000.0);

        RuleMetricStats statsB = collector.getRuleStats("pkg.RuleB");
        assertThat(statsB.getFireCount()).isEqualTo(1);
        assertThat(statsB.getTotalTimeNanos()).isEqualTo(300_000);
    }

    @Test
    public void testMatchCounters() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.incrementMatchesCreated();
        collector.incrementMatchesCreated();
        collector.incrementMatchesCancelled();

        assertThat(collector.getMatchesCreated()).isEqualTo(2);
        assertThat(collector.getMatchesCancelled()).isEqualTo(1);
    }

    @Test
    public void testFactCountSupplier() {
        SessionMetricCollector collector = new SessionMetricCollector();
        assertThat(collector.getCurrentFactCount()).isEqualTo(-1); // no supplier

        collector.setFactCountSupplier(() -> 42L);
        assertThat(collector.getCurrentFactCount()).isEqualTo(42);
    }

    @Test
    public void testTopRulesByTime() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("pkg.Slow", 5_000_000);
        collector.recordRuleFiring("pkg.Medium", 2_000_000);
        collector.recordRuleFiring("pkg.Fast", 1_000_000);

        Collection<RuleMetricStats> top2 = collector.getTopRulesByTime(2);
        assertThat(top2).hasSize(2);
        assertThat(top2.iterator().next().getRuleName()).isEqualTo("pkg.Slow");
    }

    @Test
    public void testTopRulesByFireCount() {
        SessionMetricCollector collector = new SessionMetricCollector();
        for (int i = 0; i < 10; i++) {
            collector.recordRuleFiring("pkg.Hot", 1_000);
        }
        collector.recordRuleFiring("pkg.Cold", 1_000);

        Collection<RuleMetricStats> top1 = collector.getTopRulesByFireCount(1);
        assertThat(top1).hasSize(1);
        assertThat(top1.iterator().next().getRuleName()).isEqualTo("pkg.Hot");
        assertThat(top1.iterator().next().getFireCount()).isEqualTo(10);
    }

    @Test
    public void testConcurrentRecording() throws Exception {
        SessionMetricCollector collector = new SessionMetricCollector();
        int threadCount = 8;
        int iterationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final String ruleName = "pkg.Rule" + (t % 2); // 2 distinct rules
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        collector.recordRuleFiring(ruleName, 1_000);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(collector.getTotalRulesFired()).isEqualTo(threadCount * iterationsPerThread);
        assertThat(collector.getDistinctRuleCount()).isEqualTo(2);

        Map<String, RuleMetricStats> allStats = collector.getAllRuleStats();
        long totalFireCount = allStats.values().stream().mapToLong(RuleMetricStats::getFireCount).sum();
        assertThat(totalFireCount).isEqualTo(threadCount * iterationsPerThread);
    }

    @Test
    public void testRuleMetricStatsFormatting() {
        RuleMetricStats stats = new RuleMetricStats("test.MyRule");
        stats.recordFiring(1_500_000); // 1.5ms
        stats.recordFiring(2_500_000); // 2.5ms

        assertThat(stats.getTotalTimeMicros()).isEqualTo(4_000);
        assertThat(stats.getTotalTimeMillis()).isCloseTo(4.0, org.assertj.core.api.Assertions.within(0.01));
        assertThat(stats.getAvgTimeMillis()).isCloseTo(2.0, org.assertj.core.api.Assertions.within(0.01));
        assertThat(stats.toString()).contains("test.MyRule");
    }

    @Test
    public void testSessionStartTimeAndDuration() {
        SessionMetricCollector collector = new SessionMetricCollector();
        assertThat(collector.getSessionStartTime()).isNotNull();
        assertThat(collector.getSessionDuration()).isNotNull();
        assertThat(collector.getSessionDuration().toMillis()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testCollectorToString() {
        SessionMetricCollector collector = new SessionMetricCollector();
        collector.recordRuleFiring("pkg.R1", 100_000);
        String str = collector.toString();
        assertThat(str).contains("SessionMetricCollector");
        assertThat(str).contains("rulesFired=1");
    }
}
