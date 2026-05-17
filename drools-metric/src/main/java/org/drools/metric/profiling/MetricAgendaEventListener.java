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
package org.drools.metric.profiling;

import org.drools.metric.util.SessionMetricCollector;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieSession;

/**
 * An {@link org.kie.api.event.rule.AgendaEventListener} that profiles rule
 * execution using the existing Micrometer dependency in {@code drools-metric}.
 * <p>
 * Unlike the OpenTelemetry-based listener in {@code drools-opentelemetry},
 * this listener requires no external telemetry infrastructure and provides
 * in-process session-level aggregation with min/max/avg statistics via
 * {@link SessionMetricCollector}.
 * <p>
 * Usage:
 * <pre>{@code
 * KieSession session = kieBase.newKieSession();
 * MetricAgendaEventListener listener = new MetricAgendaEventListener();
 * listener.attach(session);
 *
 * session.insert(new MyFact());
 * session.fireAllRules();
 *
 * // Access profiling results
 * SessionMetricCollector collector = listener.getCollector();
 * System.out.println("Total rules fired: " + collector.getTotalRulesFired());
 *
 * // Generate HTML dashboard
 * String html = MetricDashboardReporter.generateReport(collector);
 * }</pre>
 */
public class MetricAgendaEventListener extends DefaultAgendaEventListener {

    private final SessionMetricCollector collector;
    private final ThreadLocal<Long> firingStartTime = new ThreadLocal<>();

    /**
     * Creates a listener with a new {@link SessionMetricCollector}.
     */
    public MetricAgendaEventListener() {
        this(new SessionMetricCollector());
    }

    /**
     * Creates a listener backed by the given collector,
     * allowing shared collectors or custom configurations.
     */
    public MetricAgendaEventListener(SessionMetricCollector collector) {
        this.collector = collector;
    }

    /**
     * Attaches this listener to the given session and configures
     * the fact-count supplier.
     */
    public void attach(KieSession session) {
        session.addEventListener(this);
        collector.setFactCountSupplier(session::getFactCount);
    }

    /**
     * Detaches this listener from the given session.
     */
    public void detach(KieSession session) {
        session.removeEventListener(this);
    }

    /**
     * Returns the underlying collector with all accumulated stats.
     */
    public SessionMetricCollector getCollector() {
        return collector;
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        collector.incrementMatchesCreated();
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        collector.incrementMatchesCancelled();
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        firingStartTime.set(System.nanoTime());
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Long startTime = firingStartTime.get();
        if (startTime != null) {
            long elapsedNanos = System.nanoTime() - startTime;
            String ruleName = event.getMatch().getRule().getPackageName()
                    + "." + event.getMatch().getRule().getName();
            collector.recordRuleFiring(ruleName, elapsedNanos);
            firingStartTime.remove();
        }
    }
}
