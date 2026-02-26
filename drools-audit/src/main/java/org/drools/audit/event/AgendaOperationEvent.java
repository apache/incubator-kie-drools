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
 * Records agenda-level operations: group pushes/pops and ruleflow
 * group activation/deactivation.
 */
public class AgendaOperationEvent extends AuditEvent {

    private static final long serialVersionUID = 1L;

    private final String groupName;

    public AgendaOperationEvent(AuditEventType type,
                                String sessionId,
                                long sequenceNumber,
                                String groupName) {
        super(type, sessionId, sequenceNumber);
        this.groupName = groupName;
    }

    public AgendaOperationEvent(String id, AuditEventType type, Instant timestamp,
                                String sessionId, long sequenceNumber,
                                String groupName) {
        super(id, type, timestamp, sessionId, sequenceNumber);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String toString() {
        return "AgendaOperationEvent{" +
                "type=" + getType() +
                ", groupName='" + groupName + '\'' +
                ", sessionId='" + getSessionId() + '\'' +
                ", seq=" + getSequenceNumber() +
                '}';
    }
}
