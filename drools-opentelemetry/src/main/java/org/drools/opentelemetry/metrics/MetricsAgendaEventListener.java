/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.opentelemetry.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

/**
 * An {@link AgendaEventListener} that records OpenTelemetry metrics for rule execution.
 * <p>
 * Metrics recorded:
 * <ul>
 * <li>{@code drools.rules.fired} - Counter of total rules fired, tagged by rule name</li>
 * <li>{@code drools.rules.firing.duration} - Histogram of rule firing duration in milliseconds</li>
 * <li>{@code drools.matches.created} - Counter of matches (activations) created</li>
 * <li>{@code drools.matches.cancelled} - Counter of matches canceled</li>
 * </ul>
 */

public class MetricsAgendaEventListener implements AgendaEventListener {

    static final String INSTRUMENTATION_NAME = "org.drools.opentelemetry";
    static final AttributeKey<String> ATTR_RULE_NAME = AttributeKey.stringKey("drools.rule.name");
    static final AttributeKey<String> ATTR_RULE_PACKAGE = AttributeKey.stringKey("drools.rule.package");
    private final LongCounter rulesFiredCounter;
    private final DoubleHistogram firingDurationHistogram;
    private final LongCounter matchesCreatedCounter;
    private final LongCounter matchesCancelledCounter;
    private final Map<Long, Long> firingStartTimes = new ConcurrentHashMap<>();
    
    public MetricsAgendaEventListener(Meter meter) {
        this.rulesFiredCounter = meter.counterBuilder("drools.rules.fired")
                .setDescription("Total number of rules fired")
                .setUnit("{rules}")
                .build();
        this.firingDurationHistogram = meter.histogramBuilder("drools.rules.firing.duration")
                .setDescription("Duration of rule firing in milliseconds")
                .setUnit("ms")
                .build();
        this.matchesCreatedCounter = meter.counterBuilder("drools.matches.created")
                .setDescription("Total number of matches (activations) created")
                .setUnit("{matches}")
                .build();
        this.matchesCancelledCounter = meter.counterBuilder("drools.matches.cancelled")
                .setDescription("Total number of matches cancelled")
                .setUnit("{matches}")
                .build();
    }
    
    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        firingStartTimes.put(Thread.currentThread().getId(), System.nanoTime());
    }
    
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        String rulePackage = event.getMatch().getRule().getPackageName();
        Attributes attributes = Attributes.of(ATTR_RULE_NAME, ruleName, ATTR_RULE_PACKAGE, rulePackage);
        rulesFiredCounter.add(1, attributes);
        Long startTime = firingStartTimes.remove(Thread.currentThread().getId());
        if (startTime != null) {
            double durationMs = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime) / 1000.0;
            firingDurationHistogram.record(durationMs, attributes);
        }
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        Attributes attributes = Attributes.of(
                ATTR_RULE_NAME, event.getMatch().getRule().getName());
        matchesCreatedCounter.add(1, attributes);
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        Attributes attributes = Attributes.of(
                ATTR_RULE_NAME, event.getMatch().getRule().getName());
        matchesCancelledCounter.add(1, attributes);
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }
}