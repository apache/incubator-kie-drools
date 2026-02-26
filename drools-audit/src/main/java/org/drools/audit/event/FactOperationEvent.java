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

import java.time.Instant;

/**
 * Records a fact lifecycle operation (insert, update, delete).
 * Captures both the object's class type and its string representation
 * at the time of the operation, plus the triggering rule if any.
 *
 * <p>Convention for field semantics by operation type:</p>
 * <ul>
 *   <li>INSERT: objectRepresentation = inserted object, previousObjectRepresentation = null</li>
 *   <li>UPDATE: objectRepresentation = new state, previousObjectRepresentation = old state</li>
 *   <li>DELETE: objectRepresentation = deleted object, previousObjectRepresentation = null</li>
 * </ul>
 */
public class FactOperationEvent extends AuditEvent {

    private static final long serialVersionUID = 1L;

    private final long factHandleId;
    private final String objectClassName;
    private final String objectRepresentation;
    private final String previousObjectRepresentation;
    private final String triggeringRuleName;

    public FactOperationEvent(AuditEventType type,
                              String sessionId,
                              long sequenceNumber,
                              long factHandleId,
                              String objectClassName,
                              String objectRepresentation,
                              String previousObjectRepresentation,
                              String triggeringRuleName) {
        super(type, sessionId, sequenceNumber);
        this.factHandleId = factHandleId;
        this.objectClassName = objectClassName;
        this.objectRepresentation = objectRepresentation;
        this.previousObjectRepresentation = previousObjectRepresentation;
        this.triggeringRuleName = triggeringRuleName;
    }

    public FactOperationEvent(String id, AuditEventType type, Instant timestamp,
                              String sessionId, long sequenceNumber,
                              long factHandleId, String objectClassName,
                              String objectRepresentation,
                              String previousObjectRepresentation,
                              String triggeringRuleName) {
        super(id, type, timestamp, sessionId, sequenceNumber);
        this.factHandleId = factHandleId;
        this.objectClassName = objectClassName;
        this.objectRepresentation = objectRepresentation;
        this.previousObjectRepresentation = previousObjectRepresentation;
        this.triggeringRuleName = triggeringRuleName;
    }

    public long getFactHandleId() {
        return factHandleId;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public String getObjectRepresentation() {
        return objectRepresentation;
    }

    public String getPreviousObjectRepresentation() {
        return previousObjectRepresentation;
    }

    public String getTriggeringRuleName() {
        return triggeringRuleName;
    }

    @Override
    public String toString() {
        return "FactOperationEvent{" +
                "type=" + getType() +
                ", factHandleId=" + factHandleId +
                ", objectClass='" + objectClassName + '\'' +
                ", object='" + objectRepresentation + '\'' +
                ", triggeringRule='" + triggeringRuleName + '\'' +
                ", sessionId='" + getSessionId() + '\'' +
                ", seq=" + getSequenceNumber() +
                '}';
    }
}
