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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.RuleFiredEvent;

/**
 * Thread-safe, bounded in-memory audit store suitable for development,
 * testing, and short-lived sessions. Events exceeding {@code maxCapacity}
 * cause the oldest entries to be evicted.
 */
public class InMemoryAuditStore implements AuditStore {

    private static final int DEFAULT_MAX_CAPACITY = 100_000;

    private final ConcurrentLinkedDeque<AuditEvent> events = new ConcurrentLinkedDeque<>();
    private final int maxCapacity;

    public InMemoryAuditStore() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public InMemoryAuditStore(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be positive");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void store(AuditEvent event) {
        events.addLast(event);
        while (events.size() > maxCapacity) {
            events.pollFirst();
        }
    }

    @Override
    public List<AuditEvent> findBySessionId(String sessionId) {
        return events.stream()
                .filter(e -> e.getSessionId().equals(sessionId))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findBySessionIdAndType(String sessionId, AuditEventType type) {
        return events.stream()
                .filter(e -> e.getSessionId().equals(sessionId) && e.getType() == type)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByTimeRange(Instant from, Instant to) {
        return events.stream()
                .filter(e -> !e.getTimestamp().isBefore(from) && !e.getTimestamp().isAfter(to))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByRuleName(String ruleName) {
        return events.stream()
                .filter(e -> e instanceof RuleFiredEvent)
                .map(e -> (RuleFiredEvent) e)
                .filter(e -> ruleName.equals(e.getRuleName()))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findBySessionIdAndTimeRange(String sessionId, Instant from, Instant to) {
        return events.stream()
                .filter(e -> e.getSessionId().equals(sessionId)
                        && !e.getTimestamp().isBefore(from)
                        && !e.getTimestamp().isAfter(to))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findAll() {
        List<AuditEvent> snapshot = new ArrayList<>(events);
        Collections.sort(snapshot);
        return snapshot;
    }

    @Override
    public long count() {
        return events.size();
    }

    @Override
    public long countBySessionId(String sessionId) {
        return events.stream()
                .filter(e -> e.getSessionId().equals(sessionId))
                .count();
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        events.removeIf(e -> e.getSessionId().equals(sessionId));
    }

    @Override
    public void deleteAll() {
        events.clear();
    }
}
