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
package org.drools.replay.recorder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.drools.replay.event.EventType;
import org.drools.replay.event.FactDeletedEvent;
import org.drools.replay.event.FactInsertedEvent;
import org.drools.replay.event.FactUpdatedEvent;
import org.drools.replay.event.RuleMatchEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.event.rule.RuleRuntimeEventManager;
import org.kie.api.runtime.rule.Match;

/**
 * Records all agenda and runtime events from a Drools session into an {@link ExecutionLog}.
 *
 * <p>Usage:
 * <pre>{@code
 *   RuleExecutionRecorder recorder = new RuleExecutionRecorder();
 *   recorder.attach(kieSession);
 *   kieSession.insert(new MyFact());
 *   kieSession.fireAllRules();
 *   ExecutionLog log = recorder.getLog();
 *   System.out.println(log.dump());
 * }</pre>
 */
public class RuleExecutionRecorder implements AgendaEventListener, RuleRuntimeEventListener {

    private final ExecutionLog log;
    private final AtomicLong sequenceCounter = new AtomicLong(0);
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    public RuleExecutionRecorder() {
        this.log = new ExecutionLog();
    }

    public RuleExecutionRecorder(ExecutionLog log) {
        this.log = log;
    }

    /**
     * Registers this recorder as both an {@link AgendaEventListener} and a
     * {@link RuleRuntimeEventListener} on the given session.
     */
    public void attach(RuleRuntimeEventManager session) {
        session.addEventListener((AgendaEventListener) this);
        session.addEventListener((RuleRuntimeEventListener) this);
    }

    /**
     * Removes this recorder from the given session.
     */
    public void detach(RuleRuntimeEventManager session) {
        session.removeEventListener((AgendaEventListener) this);
        session.removeEventListener((RuleRuntimeEventListener) this);
    }

    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public ExecutionLog getLog() {
        return log;
    }

    // --- RuleRuntimeEventListener ---

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        if (!enabled.get()) {
            return;
        }
        Object fact = event.getObject();
        log.addEvent(new FactInsertedEvent(
                sequenceCounter.incrementAndGet(),
                className(fact),
                identity(fact),
                safeToString(fact),
                ruleName(event.getRule())));
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        if (!enabled.get()) {
            return;
        }
        Object newFact = event.getObject();
        Object oldFact = event.getOldObject();
        log.addEvent(new FactUpdatedEvent(
                sequenceCounter.incrementAndGet(),
                className(newFact),
                identity(newFact),
                safeToString(oldFact),
                safeToString(newFact),
                ruleName(event.getRule())));
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        if (!enabled.get()) {
            return;
        }
        Object fact = event.getOldObject();
        log.addEvent(new FactDeletedEvent(
                sequenceCounter.incrementAndGet(),
                className(fact),
                identity(fact),
                safeToString(fact),
                ruleName(event.getRule())));
    }

    // --- AgendaEventListener ---

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        if (!enabled.get()) {
            return;
        }
        log.addEvent(buildRuleMatchEvent(event.getMatch(), EventType.MATCH_CREATED));
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        if (!enabled.get()) {
            return;
        }
        log.addEvent(buildRuleMatchEvent(event.getMatch(), EventType.MATCH_CANCELLED));
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        if (!enabled.get()) {
            return;
        }
        log.addEvent(buildRuleMatchEvent(event.getMatch(), EventType.BEFORE_RULE_FIRED));
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        if (!enabled.get()) {
            return;
        }
        log.addEvent(buildRuleMatchEvent(event.getMatch(), EventType.AFTER_RULE_FIRED));
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

    // --- Internal helpers ---

    private RuleMatchEvent buildRuleMatchEvent(Match match, EventType type) {
        List<String> factDescriptions = match.getObjects().stream()
                .map(this::safeToString)
                .collect(Collectors.toList());
        return new RuleMatchEvent(
                sequenceCounter.incrementAndGet(),
                type,
                match.getRule().getName(),
                match.getRule().getPackageName(),
                factDescriptions);
    }

    private static String className(Object obj) {
        return obj != null ? obj.getClass().getName() : "null";
    }

    private static String identity(Object obj) {
        return obj != null ? Integer.toHexString(System.identityHashCode(obj)) : "null";
    }

    private String safeToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return obj.toString();
        } catch (Exception e) {
            return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
        }
    }

    private static String ruleName(org.kie.api.definition.rule.Rule rule) {
        return rule != null ? rule.getName() : null;
    }
}