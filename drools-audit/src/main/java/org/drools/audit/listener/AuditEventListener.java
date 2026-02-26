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
package org.drools.audit.listener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.audit.event.AgendaOperationEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.store.AuditStore;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Composite listener that attaches to a KieSession as both an
 * {@link org.kie.api.event.rule.AgendaEventListener} and a
 * {@link org.kie.api.event.rule.RuleRuntimeEventListener}, recording
 * every rule evaluation and fact mutation into an {@link AuditStore}.
 *
 * <p>Thread-safe: the sequence counter is atomic and the store
 * implementation is responsible for its own concurrency guarantees.</p>
 */
public class AuditEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditStore store;
    private final String sessionId;
    private final AtomicLong sequenceCounter = new AtomicLong(0);
    private final AgendaListener agendaListener = new AgendaListener();
    private final RuntimeListener runtimeListener = new RuntimeListener();

    public AuditEventListener(AuditStore store, String sessionId) {
        this.store = store;
        this.sessionId = sessionId;
    }

    /**
     * Registers both listener facets on the given session.
     */
    public void attach(KieSession session) {
        session.addEventListener(agendaListener);
        session.addEventListener(runtimeListener);
        LOG.debug("Audit listener attached to session {}", sessionId);
    }

    /**
     * Removes both listener facets from the given session.
     */
    public void detach(KieSession session) {
        session.removeEventListener(agendaListener);
        session.removeEventListener(runtimeListener);
        LOG.debug("Audit listener detached from session {}", sessionId);
    }

    public AgendaListener getAgendaListener() {
        return agendaListener;
    }

    public RuntimeListener getRuntimeListener() {
        return runtimeListener;
    }

    public String getSessionId() {
        return sessionId;
    }

    private long nextSeq() {
        return sequenceCounter.incrementAndGet();
    }

    private Map<String, String> extractDeclarations(Match match) {
        Map<String, String> decls = new LinkedHashMap<>();
        for (String declId : match.getDeclarationIds()) {
            Object value = match.getDeclarationValue(declId);
            decls.put(declId, value != null ? value.toString() : "null");
        }
        return decls;
    }

    private List<Long> extractFactHandleIds(Match match) {
        List<Long> ids = new ArrayList<>();
        for (FactHandle fh : match.getFactHandles()) {
            if (fh instanceof InternalFactHandle) {
                ids.add(((InternalFactHandle) fh).getId());
            }
        }
        return ids;
    }

    private String ruleName(org.kie.api.definition.rule.Rule rule) {
        return rule != null ? rule.getName() : null;
    }

    /**
     * Inner listener for agenda (rule activation) events.
     */
    public class AgendaListener extends DefaultAgendaEventListener {

        @Override
        public void matchCreated(MatchCreatedEvent event) {
            RuleFiredEvent auditEvent = new RuleFiredEvent(
                    AuditEventType.RULE_MATCH_CREATED,
                    sessionId, nextSeq(),
                    event.getMatch().getRule().getName(),
                    event.getMatch().getRule().getPackageName(),
                    extractDeclarations(event.getMatch()),
                    extractFactHandleIds(event.getMatch()),
                    event.getMatch().getSalience());
            store.store(auditEvent);
        }

        @Override
        public void matchCancelled(MatchCancelledEvent event) {
            RuleFiredEvent auditEvent = new RuleFiredEvent(
                    AuditEventType.RULE_MATCH_CANCELLED,
                    sessionId, nextSeq(),
                    event.getMatch().getRule().getName(),
                    event.getMatch().getRule().getPackageName(),
                    extractDeclarations(event.getMatch()),
                    extractFactHandleIds(event.getMatch()),
                    event.getMatch().getSalience());
            store.store(auditEvent);
        }

        @Override
        public void beforeMatchFired(BeforeMatchFiredEvent event) {
            // captured for timing; rule-fired is recorded on afterMatchFired
        }

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
            RuleFiredEvent auditEvent = new RuleFiredEvent(
                    AuditEventType.RULE_FIRED,
                    sessionId, nextSeq(),
                    event.getMatch().getRule().getName(),
                    event.getMatch().getRule().getPackageName(),
                    extractDeclarations(event.getMatch()),
                    extractFactHandleIds(event.getMatch()),
                    event.getMatch().getSalience());
            store.store(auditEvent);
            LOG.trace("Audit: rule fired [{}] in session {}", event.getMatch().getRule().getName(), sessionId);
        }

        @Override
        public void agendaGroupPushed(AgendaGroupPushedEvent event) {
            store.store(new AgendaOperationEvent(
                    AuditEventType.AGENDA_GROUP_PUSHED,
                    sessionId, nextSeq(),
                    event.getAgendaGroup().getName()));
        }

        @Override
        public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
            store.store(new AgendaOperationEvent(
                    AuditEventType.AGENDA_GROUP_POPPED,
                    sessionId, nextSeq(),
                    event.getAgendaGroup().getName()));
        }

        @Override
        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
            store.store(new AgendaOperationEvent(
                    AuditEventType.RULEFLOW_GROUP_ACTIVATED,
                    sessionId, nextSeq(),
                    event.getRuleFlowGroup().getName()));
        }

        @Override
        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
            store.store(new AgendaOperationEvent(
                    AuditEventType.RULEFLOW_GROUP_DEACTIVATED,
                    sessionId, nextSeq(),
                    event.getRuleFlowGroup().getName()));
        }
    }

    /**
     * Inner listener for fact lifecycle (working memory) events.
     */
    public class RuntimeListener extends DefaultRuleRuntimeEventListener {

        @Override
        public void objectInserted(ObjectInsertedEvent event) {
            Object obj = event.getObject();
            store.store(new FactOperationEvent(
                    AuditEventType.FACT_INSERTED,
                    sessionId, nextSeq(),
                    factHandleId(event.getFactHandle()),
                    obj.getClass().getName(),
                    obj.toString(),
                    null,
                    ruleName(event.getRule())));
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) {
            Object obj = event.getObject();
            Object oldObj = event.getOldObject();
            store.store(new FactOperationEvent(
                    AuditEventType.FACT_UPDATED,
                    sessionId, nextSeq(),
                    factHandleId(event.getFactHandle()),
                    obj.getClass().getName(),
                    obj.toString(),
                    oldObj != null ? oldObj.toString() : null,
                    ruleName(event.getRule())));
        }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) {
            Object oldObj = event.getOldObject();
            store.store(new FactOperationEvent(
                    AuditEventType.FACT_DELETED,
                    sessionId, nextSeq(),
                    factHandleId(event.getFactHandle()),
                    oldObj != null ? oldObj.getClass().getName() : "unknown",
                    oldObj != null ? oldObj.toString() : null,
                    null,
                    ruleName(event.getRule())));
        }

        private long factHandleId(FactHandle fh) {
            if (fh instanceof InternalFactHandle) {
                return ((InternalFactHandle) fh).getId();
            }
            return -1;
        }
    }
}
