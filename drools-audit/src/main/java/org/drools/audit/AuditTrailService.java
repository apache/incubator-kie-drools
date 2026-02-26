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
import java.util.Objects;
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

    /**
     * Maximum allowed length for caller-supplied session IDs,
     * matching the JPA column constraint.
     */
    public static final int MAX_SESSION_ID_LENGTH = 255;

    private final AuditStore store;
    private final ConcurrentHashMap<String, AuditRegistration> activeListeners = new ConcurrentHashMap<>();

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
     *
     * @throws IllegalStateException    if auditing is already active for the given session ID
     * @throws IllegalArgumentException if the session ID is null, empty, or exceeds {@link #MAX_SESSION_ID_LENGTH}
     */
    public String startAudit(KieSession session, String sessionId) {
        validateSessionId(sessionId);

        AuditEventListener listener = new AuditEventListener(store, sessionId);
        AuditRegistration registration = new AuditRegistration(listener, session);

        if (activeListeners.putIfAbsent(sessionId, registration) != null) {
            throw new IllegalStateException("Audit already active for session " + sessionId
                    + "; call stopAudit before starting a new audit for the same session ID");
        }

        listener.attach(session);
        store.store(new SessionOperationEvent(
                AuditEventType.SESSION_CREATED, sessionId, 0, 0, 0));
        LOG.info("Audit started for session {}", sessionId);
        return sessionId;
    }

    /**
     * Detaches the audit listener and records a session-disposed event.
     */
    public void stopAudit(KieSession session, String sessionId) {
        AuditRegistration registration = activeListeners.remove(sessionId);
        if (registration != null) {
            registration.listener().detach(session);
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
     * Detaches the audit listener (if still active) from its KieSession
     * and purges all audit data for the given session.
     */
    public void purgeSession(String sessionId) {
        AuditRegistration registration = activeListeners.remove(sessionId);
        if (registration != null) {
            try {
                registration.listener().detach(registration.session());
            } catch (Exception e) {
                LOG.warn("Failed to detach audit listener for session {} during purge", sessionId, e);
            }
        }
        store.deleteBySessionId(sessionId);
        LOG.info("Audit data purged for session {}", sessionId);
    }

    /**
     * Detaches all active audit listeners from their KieSessions
     * and purges all audit data.
     */
    public void purgeAll() {
        detachAllListeners();
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
        detachAllListeners();
        store.close();
    }

    private void detachAllListeners() {
        activeListeners.forEach((sessionId, registration) -> {
            try {
                registration.listener().detach(registration.session());
            } catch (Exception e) {
                LOG.warn("Failed to detach audit listener for session {}", sessionId, e);
            }
        });
        activeListeners.clear();
    }

    private void validateSessionId(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        if (sessionId.isEmpty()) {
            throw new IllegalArgumentException("sessionId must not be empty");
        }
        if (sessionId.length() > MAX_SESSION_ID_LENGTH) {
            throw new IllegalArgumentException("sessionId length " + sessionId.length()
                    + " exceeds maximum of " + MAX_SESSION_ID_LENGTH + " characters");
        }
    }

    private record AuditRegistration(AuditEventListener listener, KieSession session) {}
}
