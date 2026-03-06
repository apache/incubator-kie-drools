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
package org.drools.opentelemetry.tracing;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.rule.Match;

/**
 * An {@link AgendaEventListener} that creates OpenTelemetry spans for each rule firing.
 * A span starts at {@code beforeMatchFired} and ends at {@code afterMatchFired},
 * capturing the rule's name, package, and matched fact count as span attributes.
 */

public class TracingAgendaEventListener implements AgendaEventListener {
    
    static final String INSTRUMENTATION_NAME = "org.drools.opentelemetry";
    static final AttributeKey<String> ATTR_RULE_NAME = AttributeKey.stringKey("drools.rule.name");
    static final AttributeKey<String> ATTR_RULE_PACKAGE = AttributeKey.stringKey("drools.rule.package");
    static final AttributeKey<Long> ATTR_FACT_COUNT = AttributeKey.longKey("drools.match.fact.count");
    private final Tracer tracer;
    private final Map<Long, Span> activeSpans = new ConcurrentHashMap<>();
    
    public TracingAgendaEventListener(Tracer tracer) {
        this.tracer = tracer;
    }
    
    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        Match match = event.getMatch();
        Rule rule = match.getRule();
        Span span = tracer.spanBuilder("rule: " + rule.getName())
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute(ATTR_RULE_NAME, rule.getName())
                .setAttribute(ATTR_RULE_PACKAGE, rule.getPackageName())
                .setAttribute(ATTR_FACT_COUNT, (long) match.getFactHandles().size())
                .startSpan();
        activeSpans.put(Thread.currentThread().getId(), span);
    }
    
    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Span span = activeSpans.remove(Thread.currentThread().getId());
        if (span != null) {
            span.setStatus(StatusCode.OK);
            span.end();
        }
    }
    @Override
    public void matchCreated(MatchCreatedEvent event) {
        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            current.addEvent("match.created", Attributes.of(
                    ATTR_RULE_NAME, event.getMatch().getRule().getName()));
        }
    }
    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        Span current = Span.current();
        if (current.getSpanContext().isValid()) {
            current.addEvent("match.cancelled", Attributes.of(
                    ATTR_RULE_NAME, event.getMatch().getRule().getName()));
        }
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