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
package org.kie.kogito.index.jpa.query;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.index.jpa.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.index.test.query.AbstractProcessInstanceQueryIT;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.*;

public abstract class AbstractProcessInstanceEntityQueryIT extends AbstractProcessInstanceQueryIT {

    ProcessInstanceEntityStorage storage;

    public AbstractProcessInstanceEntityQueryIT(ProcessInstanceEntityStorage storage) {
        this.storage = storage;
    }

    @Override
    public ProcessInstanceEntityStorage getStorage() {
        return storage;
    }

    @Test
    void testCount() {
        ProcessInstanceStateDataEvent processInstanceEvent = TestUtils.createProcessInstanceEvent(UUID.randomUUID().toString(), "counting", null, null, COMPLETED.ordinal());
        storage.indexState(processInstanceEvent);
        assertThat(storage.query().count()).isNotZero();
        assertThat(storage.query().filter(List.of(in("state", List.of(34)))).count()).isZero();
    }

    // ========================================
    // NOT Operator Tests for Collection Operations
    // ========================================

    /**
     * Test: NOT with CONTAINS on collection attribute
     * Verifies entity-level negation using NOT EXISTS subquery
     */
    @Test
    @Transactional
    void testNotContainsOnCollectionAttribute() {
        // Setup: Create process instances with different nodes
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();
        String pi3 = UUID.randomUUID().toString();

        // PI1: Has nodes ["Start", "HR Interview", "End"]
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "Start", "node1");
        addNodeToProcessInstance(pi1, "HR Interview", "node2");
        addNodeToProcessInstance(pi1, "End", "node3");

        // PI2: Has nodes ["Start", "Technical Interview", "End"]
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Start", "node1");
        addNodeToProcessInstance(pi2, "Technical Interview", "node4");
        addNodeToProcessInstance(pi2, "End", "node3");

        // PI3: Has nodes ["Start", "End"]
        ProcessInstanceStateDataEvent event3 = TestUtils.getProcessCloudEvent(processId, pi3, ACTIVE, null, null, null, "user1");
        storage.indexState(event3);
        addNodeToProcessInstance(pi3, "Start", "node1");
        addNodeToProcessInstance(pi3, "End", "node3");

        // Test: Find processes that DON'T have "HR Interview" node
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(contains("nodes.name", "HR Interview"))))
                .execute();

        // Verify: Should return PI2 and PI3 (not PI1)
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProcessInstance::getId).containsExactlyInAnyOrder(pi2, pi3);
        assertThat(result).extracting(ProcessInstance::getId).doesNotContain(pi1);
    }

    /**
     * Test: NOT with CONTAINS_ALL on collection
     * Verifies De Morgan's Law: NOT (A AND B) = (NOT A) OR (NOT B)
     */
    @Test
    @Transactional
    void testNotContainsAllOnCollection() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();
        String pi3 = UUID.randomUUID().toString();

        // PI1: Has all required nodes ["Start", "HR Interview", "End"]
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "Start", "node1");
        addNodeToProcessInstance(pi1, "HR Interview", "node2");
        addNodeToProcessInstance(pi1, "End", "node3");

        // PI2: Missing "HR Interview" - has ["Start", "End"]
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Start", "node1");
        addNodeToProcessInstance(pi2, "End", "node3");

        // PI3: Missing "End" - has ["Start", "HR Interview"]
        ProcessInstanceStateDataEvent event3 = TestUtils.getProcessCloudEvent(processId, pi3, ACTIVE, null, null, null, "user1");
        storage.indexState(event3);
        addNodeToProcessInstance(pi3, "Start", "node1");
        addNodeToProcessInstance(pi3, "HR Interview", "node2");

        // Test: Find processes that DON'T have ALL of ["HR Interview", "End"]
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(containsAll("nodes.name", List.of("HR Interview", "End")))))
                .execute();

        // Verify: Should return PI2 and PI3 (missing at least one node)
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProcessInstance::getId).containsExactlyInAnyOrder(pi2, pi3);
        assertThat(result).extracting(ProcessInstance::getId).doesNotContain(pi1);
    }

    /**
     * Test: NOT with CONTAINS_ANY on collection
     * Verifies De Morgan's Law: NOT (A OR B) = (NOT A) AND (NOT B)
     */
    @Test
    @Transactional
    void testNotContainsAnyOnCollection() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();
        String pi3 = UUID.randomUUID().toString();

        // PI1: Has "HR Interview"
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "Start", "node1");
        addNodeToProcessInstance(pi1, "HR Interview", "node2");

        // PI2: Has "Technical Interview"
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Start", "node1");
        addNodeToProcessInstance(pi2, "Technical Interview", "node3");

        // PI3: Has neither
        ProcessInstanceStateDataEvent event3 = TestUtils.getProcessCloudEvent(processId, pi3, ACTIVE, null, null, null, "user1");
        storage.indexState(event3);
        addNodeToProcessInstance(pi3, "Start", "node1");
        addNodeToProcessInstance(pi3, "End", "node4");

        // Test: Find processes that DON'T have ANY of ["HR Interview", "Technical Interview"]
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(containsAny("nodes.name", List.of("HR Interview", "Technical Interview")))))
                .execute();

        // Verify: Should return only PI3 (has neither)
        assertThat(result).hasSize(1);
        assertThat(result).extracting(ProcessInstance::getId).containsExactly(pi3);
    }

    /**
     * Test: NOT with AND containing collection operations
     * Verifies De Morgan's Law: NOT (A AND B) = (NOT A) OR (NOT B)
     */
    @Test
    @Transactional
    void testNotWithAndContainingCollectionOperations() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();
        String pi3 = UUID.randomUUID().toString();

        // PI1: Has both "HR Interview" and "Technical Interview"
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "HR Interview", "node1");
        addNodeToProcessInstance(pi1, "Technical Interview", "node2");

        // PI2: Has only "HR Interview"
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "HR Interview", "node1");

        // PI3: Has only "Technical Interview"
        ProcessInstanceStateDataEvent event3 = TestUtils.getProcessCloudEvent(processId, pi3, ACTIVE, null, null, null, "user1");
        storage.indexState(event3);
        addNodeToProcessInstance(pi3, "Technical Interview", "node2");

        // Test: Find processes that DON'T have (HR Interview AND Technical Interview)
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(and(List.of(
                                contains("nodes.name", "HR Interview"),
                                contains("nodes.name", "Technical Interview"))))))
                .execute();

        // Verify: Should return PI2 and PI3 (missing at least one)
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProcessInstance::getId).containsExactlyInAnyOrder(pi2, pi3);
    }

    /**
     * Test: NOT with OR containing collection operations
     * Verifies De Morgan's Law: NOT (A OR B) = (NOT A) AND (NOT B)
     */
    @Test
    @Transactional
    void testNotWithOrContainingCollectionOperations() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();
        String pi3 = UUID.randomUUID().toString();

        // PI1: Has "HR Interview"
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "HR Interview", "node1");

        // PI2: Has "Technical Interview"
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Technical Interview", "node2");

        // PI3: Has neither
        ProcessInstanceStateDataEvent event3 = TestUtils.getProcessCloudEvent(processId, pi3, ACTIVE, null, null, null, "user1");
        storage.indexState(event3);
        addNodeToProcessInstance(pi3, "Start", "node3");

        // Test: Find processes that DON'T have (HR Interview OR Technical Interview)
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(or(List.of(
                                contains("nodes.name", "HR Interview"),
                                contains("nodes.name", "Technical Interview"))))))
                .execute();

        // Verify: Should return only PI3 (has neither)
        assertThat(result).hasSize(1);
        assertThat(result).extracting(ProcessInstance::getId).containsExactly(pi3);
    }

    /**
     * Test: NOT with simple field (non-collection)
     * Verifies regular NOT is used for simple fields (no NOT EXISTS overhead)
     */
    @Test
    @Transactional
    void testNotWithSimpleField() {
        String processId1 = "hiringProcess";
        String processId2 = "onboardingProcess";
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();

        // PI1: hiringProcess
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId1, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);

        // PI2: onboardingProcess
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId2, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);

        // Test: Find processes that are NOT "hiringProcess"
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        not(equalTo("processId", processId1))))
                .execute();

        // Verify: Should return PI2
        assertThat(result).extracting(ProcessInstance::getId).contains(pi2);
        assertThat(result).extracting(ProcessInstance::getId).doesNotContain(pi1);
    }

    /**
     * Test: NOT with mixed collection and simple field operations
     * Verifies correct handling of mixed filter types
     */
    @Test
    @Transactional
    void testNotWithMixedCollectionAndSimpleFields() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();

        // PI1: hiringProcess with "HR Interview" node
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "HR Interview", "node1");

        // PI2: hiringProcess without "HR Interview" node
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Start", "node2");

        // Test: Find processes that are NOT (processId = testProcess AND has HR Interview node)
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        not(and(List.of(
                                equalTo("processId", processId),
                                contains("nodes.name", "HR Interview"))))))
                .execute();

        // Verify: Should return PI2 and any other processes
        assertThat(result).extracting(ProcessInstance::getId).contains(pi2);
        assertThat(result).extracting(ProcessInstance::getId).doesNotContain(pi1);
    }

    /**
     * Test: Backward compatibility - existing queries still work
     * Verifies no breaking changes to existing functionality
     */
    @Test
    @Transactional
    void testBackwardCompatibilityWithExistingQueries() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();

        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "HR Interview", "node1");

        // Test: Old-style positive query still works
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        contains("nodes.name", "HR Interview")))
                .execute();

        // Verify: Should return PI1
        assertThat(result).hasSize(1);
        assertThat(result).extracting(ProcessInstance::getId).containsExactly(pi1);
    }

    /**
     * Test: Complex nested NOT operations
     * Verifies recursive handling of deeply nested filters
     */
    @Test
    @Transactional
    void testComplexNestedNotOperations() {
        String processId = "testProcess_" + UUID.randomUUID().toString();
        String pi1 = UUID.randomUUID().toString();
        String pi2 = UUID.randomUUID().toString();

        // PI1: Has "HR Interview" and "Technical Interview"
        ProcessInstanceStateDataEvent event1 = TestUtils.getProcessCloudEvent(processId, pi1, ACTIVE, null, null, null, "user1");
        storage.indexState(event1);
        addNodeToProcessInstance(pi1, "HR Interview", "node1");
        addNodeToProcessInstance(pi1, "Technical Interview", "node2");

        // PI2: Has only "Start"
        ProcessInstanceStateDataEvent event2 = TestUtils.getProcessCloudEvent(processId, pi2, ACTIVE, null, null, null, "user1");
        storage.indexState(event2);
        addNodeToProcessInstance(pi2, "Start", "node3");

        // Test: Complex nested NOT: NOT (NOT (has HR Interview) OR NOT (has Technical Interview))
        // This should return processes that have BOTH interviews
        List<ProcessInstance> result = storage.query()
                .filter(List.of(
                        equalTo("processId", processId),
                        not(or(List.of(
                                not(contains("nodes.name", "HR Interview")),
                                not(contains("nodes.name", "Technical Interview")))))))
                .execute();

        // Verify: Should return only PI1 (has both)
        assertThat(result).hasSize(1);
        assertThat(result).extracting(ProcessInstance::getId).containsExactly(pi1);
    }

    // Helper method to add nodes to process instances
    private void addNodeToProcessInstance(String processInstanceId, String nodeName, String nodeId) {
        ProcessInstanceNodeDataEvent nodeEvent = TestUtils.createProcessInstanceNodeDataEvent(
                processInstanceId,
                "testProcess",
                nodeId,
                UUID.randomUUID().toString(),
                nodeName,
                "HumanTaskNode",
                ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER);
        storage.indexNode(nodeEvent);
    }
}
