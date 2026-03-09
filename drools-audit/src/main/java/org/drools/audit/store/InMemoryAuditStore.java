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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;

/**
 * Thread-safe, bounded in-memory audit store suitable for development,
 * testing, and short-lived sessions. Events exceeding {@code maxCapacity}
 * cause the oldest entries to be evicted.
 *
 * <p>All structural mutations are guarded by the intrinsic lock on
 * {@code events} so that size tracking and element removal are atomic.
 * Read operations snapshot the deque under the same lock.</p>
 */
public class InMemoryAuditStore implements AuditStore {

    private static final int DEFAULT_MAX_CAPACITY = 100_000;

    private static final Comparator<AuditEvent> TIMESTAMP_ORDER =
            Comparator.comparing(AuditEvent::getTimestamp)
                      .thenComparing(AuditEvent::getSessionId)
                      .thenComparingLong(AuditEvent::getSequenceNumber);

    private final Deque<AuditEvent> events = new ArrayDeque<>();
    private final AtomicInteger size = new AtomicInteger(0);
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
        synchronized (events) {
            events.addLast(event);
            while (size.get() > maxCapacity) {
                events.pollFirst();
            }
        }
    }

    @Override
    public List<AuditEvent> findBySessionId(String sessionId) {
        return snapshot().stream()
                .filter(e -> e.getSessionId().equals(sessionId))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findBySessionIdAndType(String sessionId, AuditEventType type) {
        return snapshot().stream()
                .filter(e -> e.getSessionId().equals(sessionId) && e.getType() == type)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByTimeRange(Instant from, Instant to) {
        return snapshot().stream()
                .filter(e -> !e.getTimestamp().isBefore(from) && !e.getTimestamp().isAfter(to))
                .sorted(TIMESTAMP_ORDER)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByRuleName(String ruleName) {
        return snapshot().stream()
                .filter(e -> ruleName.equals(extractRuleName(e)))
                .sorted()
                .collect(Collectors.toList());
    }

    private static String extractRuleName(AuditEvent event) {
        if (event instanceof RuleFiredEvent rfe) {
            return rfe.getRuleName();
        }
        if (event instanceof FactOperationEvent foe) {
            return foe.getTriggeringRuleName();
        }
        return null;
    }

    @Override
    public List<AuditEvent> findBySessionIdAndTimeRange(String sessionId, Instant from, Instant to) {
        return snapshot().stream()
                .filter(e -> e.getSessionId().equals(sessionId)
                        && !e.getTimestamp().isBefore(from)
                        && !e.getTimestamp().isAfter(to))
                .sorted(TIMESTAMP_ORDER)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findAll() {
        List<AuditEvent> snap = snapshot();
        Collections.sort(snap);
        return snap;
    }

    @Override
    public long count() {
        synchronized (events) {
            return events.size();
        }
    }

    @Override
    public long countBySessionId(String sessionId) {
        return snapshot().stream()
                .filter(e -> e.getSessionId().equals(sessionId))
                .count();
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        synchronized (events) {
            events.removeIf(e -> e.getSessionId().equals(sessionId));
        }
    }

    @Override
    public void deleteAll() {
        synchronized (events) {
            events.clear();
        }
    }

    private List<AuditEvent> snapshot() {
        synchronized (events) {
            return new ArrayList<>(events);
        }
    }
}
