package org.drools.audit;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.drools.audit.event.*;
import org.drools.audit.jpa.JpaAuditStore;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JpaAuditStoreTest {

    private static EntityManagerFactory emf;
    private JpaAuditStore store;

    @BeforeAll
    static void setUpEmf() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }

    @AfterAll
    static void tearDownEmf() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        store = new JpaAuditStore(emf);
        store.deleteAll(); // Clean up before each test
    }

    @Test
    void storeAndRetrieveBySessionId() {
        String sessionId = "session-1";
        RuleFiredEvent event = new RuleFiredEvent(
                AuditEventType.RULE_FIRED, sessionId, 1,
                "testRule", "org.test",
                Map.of("$fact", "value"), List.of(1L, 2L), 10);
        store.store(event);

        List<AuditEvent> result = store.findBySessionId(sessionId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(RuleFiredEvent.class);
        RuleFiredEvent retrieved = (RuleFiredEvent) result.get(0);
        assertThat(retrieved.getRuleName()).isEqualTo("testRule");
        assertThat(retrieved.getPackageName()).isEqualTo("org.test");
        assertThat(retrieved.getSalience()).isEqualTo(10);
        assertThat(retrieved.getFactHandleIds()).containsExactly(1L, 2L);
    }

    @Test
    void filterByEventType() {
        String sessionId = "session-2";
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new FactOperationEvent(AuditEventType.FACT_INSERTED, sessionId, 2,
                100L, "com.Fact", "Fact{}", null, null));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 3,
                "rule2", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> ruleEvents = store.findBySessionIdAndType(sessionId, AuditEventType.RULE_FIRED);
        assertThat(ruleEvents).hasSize(2);

        List<AuditEvent> factEvents = store.findBySessionIdAndType(sessionId, AuditEventType.FACT_INSERTED);
        assertThat(factEvents).hasSize(1);
    }

    @Test
    void findByRuleName() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "targetRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "otherRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s2", 1,
                "targetRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> result = store.findByRuleName("targetRule");
        assertThat(result).hasSize(2);
    }

    @Test
    void findByTimeRange() {
        Instant before = Instant.now();
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        Instant after = Instant.now();

        List<AuditEvent> result = store.findByTimeRange(before, after);
        assertThat(result).hasSize(1);

        List<AuditEvent> empty = store.findByTimeRange(
                Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-02T00:00:00Z"));
        assertThat(empty).isEmpty();
    }

    @Test
    void deleteBySessionId() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));
        assertThat(store.count()).isEqualTo(2);

        store.deleteBySessionId("s1");
        assertThat(store.count()).isEqualTo(1);
        assertThat(store.findBySessionId("s1")).isEmpty();
        assertThat(store.findBySessionId("s2")).hasSize(1);
    }

    @Test
    void deleteAll() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));
        store.deleteAll();
        assertThat(store.count()).isZero();
    }

    @Test
    void countBySessionId() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "rule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));

        assertThat(store.countBySessionId("s1")).isEqualTo(2);
        assertThat(store.countBySessionId("s2")).isEqualTo(1);
        assertThat(store.countBySessionId("s3")).isZero();
    }

    @Test
    void eventsAreOrderedByTimestamp() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 3,
                "rule3", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "rule2", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> result = store.findBySessionId("s1");
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSequenceNumber()).isEqualTo(1);
        assertThat(result.get(1).getSequenceNumber()).isEqualTo(2);
        assertThat(result.get(2).getSequenceNumber()).isEqualTo(3);
    }

    @Test
    void factOperationEventCapturesDetails() {
        FactOperationEvent event = new FactOperationEvent(
                AuditEventType.FACT_UPDATED, "s1", 1,
                42L, "com.example.Person",
                "Person{name='John', age=31}",
                "Person{name='John', age=30}",
                "ageIncrementRule");
        store.store(event);

        List<AuditEvent> result = store.findBySessionId("s1");
        assertThat(result).hasSize(1);
        FactOperationEvent retrieved = (FactOperationEvent) result.get(0);
        assertThat(retrieved.getFactHandleId()).isEqualTo(42L);
        assertThat(retrieved.getObjectClassName()).isEqualTo("com.example.Person");
        assertThat(retrieved.getObjectRepresentation()).isEqualTo("Person{name='John', age=31}");
        assertThat(retrieved.getPreviousObjectRepresentation()).isEqualTo("Person{name='John', age=30}");
        assertThat(retrieved.getTriggeringRuleName()).isEqualTo("ageIncrementRule");
    }
}
