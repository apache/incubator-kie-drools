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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.drools.audit.event.AgendaOperationEvent;
import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.event.SessionOperationEvent;
import org.drools.audit.store.AuditPersistenceException;
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
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

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
            throw new AuditPersistenceException(
                    "Failed to persist audit event: " + event.getId(), ex);
        } finally {
            em.close();
        }
    }

    @Override
    public List<AuditEvent> findBySessionId(String sessionId) {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid ORDER BY e.sequenceNumber",
                q -> q.setParameter("sid", sessionId));
    }

    @Override
    public List<AuditEvent> findBySessionIdAndType(String sessionId, AuditEventType type) {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid AND e.eventType = :etype ORDER BY e.sequenceNumber",
                q -> q.setParameter("sid", sessionId).setParameter("etype", type.getCode()));
    }

    @Override
    public List<AuditEvent> findByTimeRange(Instant from, Instant to) {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e WHERE e.eventTimestamp >= :from AND e.eventTimestamp <= :to ORDER BY e.eventTimestamp, e.sessionId, e.sequenceNumber",
                q -> q.setParameter("from", from).setParameter("to", to));
    }

    @Override
    public List<AuditEvent> findByRuleName(String ruleName) {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e WHERE e.ruleName = :rname ORDER BY e.sequenceNumber",
                q -> q.setParameter("rname", ruleName));
    }

    @Override
    public List<AuditEvent> findBySessionIdAndTimeRange(String sessionId, Instant from, Instant to) {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e WHERE e.sessionId = :sid AND e.eventTimestamp >= :from AND e.eventTimestamp <= :to ORDER BY e.eventTimestamp, e.sequenceNumber",
                q -> q.setParameter("sid", sessionId).setParameter("from", from).setParameter("to", to));
    }

    @Override
    public List<AuditEvent> findAll() {
        return executeQuery(
                "SELECT e FROM AuditEventEntity e ORDER BY e.eventTimestamp, e.sequenceNumber",
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

    private List<AuditEvent> executeQuery(String jpql,
                                          java.util.function.Consumer<TypedQuery<AuditEventEntity>> paramSetter) {
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
        } else if (event instanceof FactOperationEvent foe) {
            entity.setObjectClassName(foe.getObjectClassName());
            entity.setFactHandleId(foe.getFactHandleId());
            entity.setRuleName(foe.getTriggeringRuleName());
        }

        entity.setEventPayload(serializePayload(event));
        return entity;
    }

    private AuditEvent fromEntity(AuditEventEntity entity) {
        AuditEventType type = AuditEventType.valueOf(entity.getEventType());
        String id = entity.getId();
        Instant timestamp = entity.getEventTimestamp();
        String sessionId = entity.getSessionId();
        long seq = entity.getSequenceNumber();
        Map<String, Object> payload = deserializePayload(entity.getEventPayload());

        return switch (type.getCategory()) {
            case "rule" -> new RuleFiredEvent(
                    id, type, timestamp, sessionId, seq,
                    entity.getRuleName(),
                    entity.getPackageName(),
                    getPayloadDeclarations(payload),
                    getPayloadFactHandleIds(payload),
                    entity.getSalience());
            case "fact" -> new FactOperationEvent(
                    id, type, timestamp, sessionId, seq,
                    entity.getFactHandleId() != null ? entity.getFactHandleId() : -1,
                    entity.getObjectClassName(),
                    getPayloadString(payload, "objectRepresentation"),
                    getPayloadString(payload, "previousObjectRepresentation"),
                    entity.getRuleName());
            case "agenda" -> new AgendaOperationEvent(
                    id, type, timestamp, sessionId, seq,
                    getPayloadString(payload, "groupName"));
            default -> new SessionOperationEvent(
                    id, type, timestamp, sessionId, seq,
                    getPayloadInt(payload, "rulesFiredCount"),
                    getPayloadLong(payload, "durationMillis"));
        };
    }

    private String serializePayload(AuditEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (event instanceof RuleFiredEvent rfe) {
            payload.put("declarations", rfe.getDeclarations());
            payload.put("factHandleIds", rfe.getFactHandleIds());
        } else if (event instanceof FactOperationEvent foe) {
            payload.put("objectRepresentation", foe.getObjectRepresentation());
            payload.put("previousObjectRepresentation", foe.getPreviousObjectRepresentation());
        } else if (event instanceof AgendaOperationEvent aoe) {
            payload.put("groupName", aoe.getGroupName());
        } else if (event instanceof SessionOperationEvent soe) {
            payload.put("rulesFiredCount", soe.getRulesFiredCount());
            payload.put("durationMillis", soe.getDurationMillis());
        }
        try {
            return MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize event payload for event {}", event.getId(), e);
            return "{}";
        }
    }

    private Map<String, Object> deserializePayload(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            LOG.debug("Could not deserialize event payload as JSON (possibly legacy format)");
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getPayloadDeclarations(Map<String, Object> payload) {
        Object decls = payload.get("declarations");
        if (decls instanceof Map) {
            Map<String, Object> raw = (Map<String, Object>) decls;
            Map<String, String> result = new LinkedHashMap<>();
            raw.forEach((k, v) -> result.put(k, v != null ? v.toString() : null));
            return result;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private List<Long> getPayloadFactHandleIds(Map<String, Object> payload) {
        Object ids = payload.get("factHandleIds");
        if (ids instanceof List) {
            return ((List<Number>) ids).stream()
                    .map(Number::longValue)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String getPayloadString(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        return val != null ? val.toString() : null;
    }

    private int getPayloadInt(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        return val instanceof Number n ? n.intValue() : 0;
    }

    private long getPayloadLong(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        return val instanceof Number n ? n.longValue() : 0;
    }
}
