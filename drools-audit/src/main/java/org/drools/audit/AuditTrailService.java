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
package org.drools.audit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.SessionOperationEvent;
import org.drools.audit.listener.AuditEventListener;
import org.drools.audit.store.AuditStore;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary facade for the Drools Audit Trail subsystem.
 *
 * <p>Provides a single entry point for:</p>
 * <ul>
 *   <li>Attaching/detaching audit listeners to KieSessions</li>
 *   <li>Querying the audit event history</li>
 *   <li>Managing session-scoped audit lifecycles</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * AuditStore store = new InMemoryAuditStore();
 * AuditTrailService auditService = new AuditTrailService(store);
 *
 * KieSession session = kieBase.newKieSession();
 * String sessionId = auditService.startAudit(session);
 *
 * session.insert(new MyFact());
 * session.fireAllRules();
 *
 * List<AuditEvent> trail = auditService.getAuditTrail(sessionId);
 * auditService.stopAudit(session, sessionId);
 * }</pre>
 */
public class AuditTrailService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailService.class);

    private final AuditStore store;
    private final ConcurrentHashMap<String, AuditEventListener> activeListeners = new ConcurrentHashMap<>();

    public AuditTrailService(AuditStore store) {
        this.store = store;
    }

    /**
     * Attaches an audit listener to the session and begins recording.
     *
     * @return the generated session audit ID for correlation
     */
    public String startAudit(KieSession session) {
        return startAudit(session, UUID.randomUUID().toString());
    }

    /**
     * Attaches an audit listener with a caller-supplied session ID.
     */
    public String startAudit(KieSession session, String sessionId) {
        AuditEventListener listener = new AuditEventListener(store, sessionId);
        listener.attach(session);
        activeListeners.put(sessionId, listener);
        store.store(new SessionOperationEvent(
                AuditEventType.SESSION_CREATED, sessionId, 0, 0, 0));
        LOG.info("Audit started for session {}", sessionId);
        return sessionId;
    }

    /**
     * Detaches the audit listener and records a session-disposed event.
     */
    public void stopAudit(KieSession session, String sessionId) {
        AuditEventListener listener = activeListeners.remove(sessionId);
        if (listener != null) {
            listener.detach(session);
            store.store(new SessionOperationEvent(
                    AuditEventType.SESSION_DISPOSED, sessionId, Long.MAX_VALUE, 0, 0));
            LOG.info("Audit stopped for session {}", sessionId);
        }
    }

    /**
     * Returns the full ordered audit trail for a session.
     */
    public List<AuditEvent> getAuditTrail(String sessionId) {
        return store.findBySessionId(sessionId);
    }

    /**
     * Returns audit events filtered by type for a session.
     */
    public List<AuditEvent> getAuditTrailByType(String sessionId, AuditEventType type) {
        return store.findBySessionIdAndType(sessionId, type);
    }

    /**
     * Returns all audit events within a time window.
     */
    public List<AuditEvent> getAuditTrailByTimeRange(Instant from, Instant to) {
        return store.findByTimeRange(from, to);
    }

    /**
     * Returns all audit events for a specific rule across all sessions.
     */
    public List<AuditEvent> getAuditTrailByRuleName(String ruleName) {
        return store.findByRuleName(ruleName);
    }

    /**
     * Returns events for a session within a time window.
     */
    public List<AuditEvent> getAuditTrailBySessionAndTimeRange(String sessionId, Instant from, Instant to) {
        return store.findBySessionIdAndTimeRange(sessionId, from, to);
    }

    /**
     * Returns the total number of audit events stored.
     */
    public long getEventCount() {
        return store.count();
    }

    /**
     * Returns the event count for a specific session.
     */
    public long getEventCount(String sessionId) {
        return store.countBySessionId(sessionId);
    }

    /**
     * Purges all audit data for a session.
     */
    public void purgeSession(String sessionId) {
        activeListeners.remove(sessionId);
        store.deleteBySessionId(sessionId);
        LOG.info("Audit data purged for session {}", sessionId);
    }

    /**
     * Purges all audit data.
     */
    public void purgeAll() {
        activeListeners.clear();
        store.deleteAll();
        LOG.info("All audit data purged");
    }

    /**
     * Returns whether auditing is active for the given session ID.
     */
    public boolean isAuditing(String sessionId) {
        return activeListeners.containsKey(sessionId);
    }

    /**
     * Provides direct access to the underlying store for advanced queries.
     */
    public AuditStore getStore() {
        return store;
    }

    @Override
    public void close() throws Exception {
        activeListeners.clear();
        store.close();
    }
}
