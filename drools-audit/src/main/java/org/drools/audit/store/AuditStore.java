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
package org.drools.audit.store;

import java.time.Instant;
import java.util.List;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;

/**
 * Service Provider Interface for persisting and querying audit events.
 * Implementations may store events in memory, on disk, in a relational
 * database (JPA), or in a dedicated event store.
 */
public interface AuditStore extends AutoCloseable {

    void store(AuditEvent event);

    List<AuditEvent> findBySessionId(String sessionId);

    List<AuditEvent> findBySessionIdAndType(String sessionId, AuditEventType type);

    List<AuditEvent> findByTimeRange(Instant from, Instant to);

    List<AuditEvent> findByRuleName(String ruleName);

    List<AuditEvent> findBySessionIdAndTimeRange(String sessionId, Instant from, Instant to);

    List<AuditEvent> findAll();

    long count();

    long countBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    void deleteAll();

    @Override
    default void close() {
    }
}
