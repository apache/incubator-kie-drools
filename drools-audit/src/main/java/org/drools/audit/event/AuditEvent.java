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
package org.drools.audit.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable base class for all audit trail events.
 * Each event captures a single atomic operation within the rule engine,
 * tagged with a correlation ID for session-level grouping and a monotonically
 * increasing sequence number for total ordering within a session.
 */
public abstract class AuditEvent implements Serializable, Comparable<AuditEvent> {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final AuditEventType type;
    private final Instant timestamp;
    private final String sessionId;
    private final long sequenceNumber;

    protected AuditEvent(AuditEventType type, String sessionId, long sequenceNumber) {
        this.id = UUID.randomUUID().toString();
        this.type = Objects.requireNonNull(type, "type");
        this.timestamp = Instant.now();
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Hydration constructor for reconstructing a persisted event
     * with its original identity and timestamp.
     */
    protected AuditEvent(String id, AuditEventType type, Instant timestamp,
                         String sessionId, long sequenceNumber) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.sequenceNumber = sequenceNumber;
    }

    public String getId() {
        return id;
    }

    public AuditEventType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public int compareTo(AuditEvent other) {
        int cmp = this.sessionId.compareTo(other.sessionId);
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(this.sequenceNumber, other.sequenceNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditEvent)) return false;
        AuditEvent that = (AuditEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", sessionId='" + sessionId + '\'' +
                ", seq=" + sequenceNumber +
                ", timestamp=" + timestamp +
                '}';
    }
}
