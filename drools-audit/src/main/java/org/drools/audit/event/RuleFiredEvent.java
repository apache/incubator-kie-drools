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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Records a rule activation event (match created, fired, or cancelled).
 * Captures the full state needed for compliance replay: rule identity,
 * matched declarations, fact handle IDs, and salience.
 */
public class RuleFiredEvent extends AuditEvent {

    private static final long serialVersionUID = 1L;

    private final String ruleName;
    private final String packageName;
    private final Map<String, String> declarations;
    private final List<Long> factHandleIds;
    private final int salience;

    public RuleFiredEvent(AuditEventType type,
                          String sessionId,
                          long sequenceNumber,
                          String ruleName,
                          String packageName,
                          Map<String, String> declarations,
                          List<Long> factHandleIds,
                          int salience) {
        super(type, sessionId, sequenceNumber);
        this.ruleName = ruleName;
        this.packageName = packageName;
        this.declarations = declarations != null ? Collections.unmodifiableMap(declarations) : Collections.emptyMap();
        this.factHandleIds = factHandleIds != null ? Collections.unmodifiableList(factHandleIds) : Collections.emptyList();
        this.salience = salience;
    }

    public RuleFiredEvent(String id, AuditEventType type, Instant timestamp,
                          String sessionId, long sequenceNumber,
                          String ruleName, String packageName,
                          Map<String, String> declarations, List<Long> factHandleIds,
                          int salience) {
        super(id, type, timestamp, sessionId, sequenceNumber);
        this.ruleName = ruleName;
        this.packageName = packageName;
        this.declarations = declarations != null ? Collections.unmodifiableMap(declarations) : Collections.emptyMap();
        this.factHandleIds = factHandleIds != null ? Collections.unmodifiableList(factHandleIds) : Collections.emptyList();
        this.salience = salience;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Map<String, String> getDeclarations() {
        return declarations;
    }

    public List<Long> getFactHandleIds() {
        return factHandleIds;
    }

    public int getSalience() {
        return salience;
    }

    @Override
    public String toString() {
        return "RuleFiredEvent{" +
                "type=" + getType() +
                ", rule='" + packageName + "." + ruleName + '\'' +
                ", salience=" + salience +
                ", factHandles=" + factHandleIds +
                ", declarations=" + declarations +
                ", sessionId='" + getSessionId() + '\'' +
                ", seq=" + getSequenceNumber() +
                '}';
    }
}
