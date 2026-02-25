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
package org.drools.audit.jpa;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.store.AuditStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA-backed audit store for durable, queryable audit trails.
 * Suitable for compliance workloads requiring long-term event retention
 * in a relational database.
 */
public class JpaAuditStore implements AuditStore {

    private static final Logger LOG = LoggerFactory.getLogger(JpaAuditStore.class);

    private final EntityManagerFactory emf;

    public JpaAuditStore(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void store(AuditEvent event) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(toEntity(event));
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOG.error("Failed to persist audit event: {}", event.getId(), ex);
        } finally {
            em.close();
        }
    }

    @Override
    public List<AuditEvent> findBySessionId(String sessionId) {
        return executeQuery("SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid ORDER BY e.sequenceNumber",
                q -> q.setParameter("sid", sessionId));
    }

    @Override
    public List<AuditEvent> findBySessionIdAndType(String sessionId, AuditEventType type) {
        return executeQuery("SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid AND e.eventType = :etype ORDER BY e.sequenceNumber",
                q -> q.setParameter("sid", sessionId).setParameter("etype", type.getCode()));
    }

    @Override
    public List<AuditEvent> findByTimeRange(Instant from, Instant to) {
        return executeQuery("SELECT e FROM AuditEventEntity e WHERE e.eventTimestamp >= :from AND e.eventTimestamp <= :to ORDER BY e.eventTimestamp",
                q -> q.setParameter("from", from).setParameter("to", to));
    }

    @Override
    public List<AuditEvent> findByRuleName(String ruleName) {
        return executeQuery("SELECT e FROM AuditEventEntity e WHERE e.ruleName = :rname ORDER BY e.sequenceNumber",
                q -> q.setParameter("rname", ruleName));
    }

    @Override
    public List<AuditEvent> findBySessionIdAndTimeRange(String sessionId, Instant from, Instant to) {
        return executeQuery("SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid AND e.eventTimestamp >= :from AND e.eventTimestamp <= :to ORDER BY e.sequenceNumber",
                q -> q.setParameter("sid", sessionId).setParameter("from", from).setParameter("to", to));
    }

    @Override
    public List<AuditEvent> findAll() {
        return executeQuery("SELECT e FROM AuditEventEntity e ORDER BY e.eventTimestamp, e.sequenceNumber",
                q -> {});
    }

    @Override
    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(e) FROM AuditEventEntity e", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countBySessionId(String sessionId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(e) FROM AuditEventEntity e WHERE e.sessionId = :sid", Long.class)
                    .setParameter("sid", sessionId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM AuditEventEntity e WHERE e.sessionId = :sid")
                    .setParameter("sid", sessionId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOG.error("Failed to delete audit events for session: {}", sessionId, ex);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM AuditEventEntity e").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOG.error("Failed to delete all audit events", ex);
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        // emf lifecycle is managed by the caller
    }

    private List<AuditEvent> executeQuery(String jpql, java.util.function.Consumer<TypedQuery<AuditEventEntity>> paramSetter) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<AuditEventEntity> query = em.createQuery(jpql, AuditEventEntity.class);
            paramSetter.accept(query);
            return query.getResultList().stream()
                    .map(this::fromEntity)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private AuditEventEntity toEntity(AuditEvent event) {
        AuditEventEntity entity = new AuditEventEntity();
        entity.setId(event.getId());
        entity.setEventType(event.getType().getCode());
        entity.setEventCategory(event.getType().getCategory());
        entity.setEventTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId());
        entity.setSequenceNumber(event.getSequenceNumber());

        if (event instanceof RuleFiredEvent rfe) {
            entity.setRuleName(rfe.getRuleName());
            entity.setPackageName(rfe.getPackageName());
            entity.setSalience(rfe.getSalience());
            entity.setEventPayload(rfe.getDeclarations().toString());
        } else if (event instanceof FactOperationEvent foe) {
            entity.setObjectClassName(foe.getObjectClassName());
            entity.setFactHandleId(foe.getFactHandleId());
            entity.setRuleName(foe.getTriggeringRuleName());
            entity.setEventPayload(foe.getObjectRepresentation());
        }
        return entity;
    }

    private AuditEvent fromEntity(AuditEventEntity entity) {
        AuditEventType type = AuditEventType.valueOf(entity.getEventType());
        return switch (type.getCategory()) {
            case "rule" -> new RuleFiredEvent(
                    type,
                    entity.getSessionId(),
                    entity.getSequenceNumber(),
                    entity.getRuleName(),
                    entity.getPackageName(),
                    java.util.Collections.emptyMap(),
                    java.util.Collections.emptyList(),
                    entity.getSalience());
            case "fact" -> new FactOperationEvent(
                    type,
                    entity.getSessionId(),
                    entity.getSequenceNumber(),
                    entity.getFactHandleId() != null ? entity.getFactHandleId() : -1,
                    entity.getObjectClassName(),
                    entity.getEventPayload(),
                    null,
                    entity.getRuleName());
            default -> new org.drools.audit.event.SessionOperationEvent(
                    type,
                    entity.getSessionId(),
                    entity.getSequenceNumber(),
                    0, 0);
        };
    }
}
